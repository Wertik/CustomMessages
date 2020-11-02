package space.devport.wertik.custommessages;

import space.devport.utils.DevportPlugin;
import space.devport.utils.text.language.LanguageDefaults;

public class MessageLanguage extends LanguageDefaults {

    public MessageLanguage(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public void setDefaults() {
        addDefault("Commands.Invalid-Type", "&cType &f%param% &cis invalid.");
        addDefault("Commands.Invalid-Message", "&cMessage &f%param% &cis not valid in type &f%type%");
        addDefault("Commands.Invalid-Player", "&cPlayer &f%player% &cis not online.");

        addDefault("Commands.Set.Done", "&7Set message &f%message% &7in message type &f%type%");
        addDefault("Commands.Set.Done-Others", "&7Set message &f%message% &7in message type &f%type% &7for &f%player%");

        addDefault("Commands.Preview.Done", "&7Preview: &f'%message%&f'");

        addDefault("Commands.Menu.Done-Others", "&7Opened &f%type% &7message menu for &f%player%");
    }
}
