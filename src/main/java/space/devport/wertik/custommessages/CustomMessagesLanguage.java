package space.devport.wertik.custommessages;

import space.devport.utils.text.language.LanguageDefaults;

public class CustomMessagesLanguage extends LanguageDefaults {

    @Override
    public void setDefaults() {
        addDefault("Commands.Invalid-Type", "&cType &f%param% &cis invalid.");
        addDefault("Commands.Invalid-Message", "&cMessage &f%param% &cis not valid in type &f%type%");
        addDefault("Commands.Invalid-Player", "&cPlayer &f%player% &cis not online.");

        addDefault("Commands.Set.Done", "&7Set message &f%message% &7in message type &f%type%");
        addDefault("Commands.Set.Done-Others", "&7Set message &f%message% &7in message type &f%type% &7for &f%player%");

        addDefault("Commands.Preview.Done-Others", "&7Sent preview to &f%player% &7in type &f%type%");
    }
}
