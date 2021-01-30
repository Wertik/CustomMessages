package space.devport.wertik.custommessages.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.java.Log;
import space.devport.utils.logging.DebugLevel;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Log
public class ServerConnection {

    @Getter
    private final ConnectionInfo connectionInfo;

    private HikariDataSource hikari;

    @Getter
    private boolean connected = false;

    public ServerConnection(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public void connect() {

        if (connectionInfo.getHost() == null) {
            throw new IllegalStateException("MySQL Connection not configured. Cannot continue.");
        }

        this.hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        hikari.addDataSourceProperty("serverName", connectionInfo.getHost());
        hikari.addDataSourceProperty("port", connectionInfo.getPort());
        hikari.addDataSourceProperty("databaseName", connectionInfo.getDatabase());

        hikari.addDataSourceProperty("user", connectionInfo.getUsername());
        hikari.addDataSourceProperty("password", connectionInfo.getPassword());

        hikari.addDataSourceProperty("characterEncoding", "utf8");
        hikari.addDataSourceProperty("useUnicode", "true");

        hikari.setReadOnly(connectionInfo.isReadOnly());

        try {
            hikari.validate();
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e);
        }

        Connection connection;
        try {
            connection = hikari.getConnection();
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }

        if (connection == null)
            throw new IllegalStateException("No connection");

        this.connected = true;
    }

    public CompletableFuture<Boolean> execute(String query, Object... parameters) {
        return CompletableFuture.supplyAsync(() -> {

            if (!isConnected())
                return false;

            try (Connection connection = hikari.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                if (parameters != null) {
                    for (int i = 0; i < parameters.length; i++) {
                        statement.setObject(i + 1, parameters[i]);
                    }
                }

                statement.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<ResultSet> executeQuery(String query, Object... parameters) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isConnected())
                return null;

            try (Connection connection = hikari.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                if (parameters != null) {
                    for (int i = 0; i < parameters.length; i++) {
                        statement.setObject(i + 1, parameters[i]);
                    }
                }

                CachedRowSet resultCached = RowSetProvider.newFactory().createCachedRowSet();
                ResultSet resultSet = statement.executeQuery();

                resultCached.populate(resultSet);
                resultSet.close();

                return resultCached;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    public void close() {
        this.hikari.close();
    }
}
