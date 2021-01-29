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
        addDefault("Commands.Invalid-Player", "&cPlayer &f%param% &cis not online.");

        addDefault("Commands.Set.Done", "&7Set message &f%message% &7in message type &f%type%");
        addDefault("Commands.Set.Done-Others", "&7Set message &f%message% &7in message type &f%type% &7for &f%player%");

        addDefault("Commands.Preview.Done", "&7Preview: &f'%message%&f'");

        addDefault("Commands.Menu.Done-Others", "&7Opened &f%type% &7message menu for &f%player%");

        addDefault("Commands.Show.Header", "&7Messages of &f%player%&7");
        addDefault("Commands.Show.Line-Format", "&e%type% &8&l| &f%message% (&7 &r%preview% &7)");

        addDefault("Type-Defaults.kill.Killer", "md_5");
        addDefault("Type-Defaults.kill.Health", "256");
    }
}
