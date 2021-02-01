package space.devport.wertik.custommessages.storage.mysql;

public enum Query {

    CREATE_TABLE("CREATE TABLE IF NOT EXISTS `%table%` (\n" +
            "`uuid` VARCHAR(40) NOT NULL,\n" +
            "`join` VARCHAR(32),\n" +
            "`leave` VARCHAR(32),\n" +
            "`kill` VARCHAR(32)\n," +
            "PRIMARY KEY (`uuid`),\n" +
            "UNIQUE (`uuid`)\n" +
            ") DEFAULT CHARSET=utf8"),

    LOAD_USER("SELECT `uuid`, `join`, `leave`, `kill` FROM `%table%` WHERE `uuid` = ?"),

    SAVE_USER("INSERT INTO `%table%` (`uuid`, `join`, `leave`, `kill`) " +
            "VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "`uuid` = ?, `join` = ?, `leave` = ?, `kill` = ?"),

    DELETE_USER("DELETE FROM `%table%` WHERE `uuid` = ?");

    private final String query;

    Query(String query) {
        this.query = query;
    }

    public String get(String table) {
        return query.replaceAll("(?i)%table%", table);
    }
}
