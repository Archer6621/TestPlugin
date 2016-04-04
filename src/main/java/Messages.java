import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 * Created by Archer on 01-Apr-16.
 */
public enum Messages {
    //Home
    HOME_SELF(                      "Moved you to your home!"),
    HOME_OTHER(                     "Moved you to %p's home!"),
    HOME_OTHER_TO_SELF(             "%p teleported to your home..."),
    HOME_OBSOLETE(                  "The world your home was in is gone, you have to set a new home!"),

    //Home setting
    HOME_SET(                       "Your home has been set!"),
    HOME_MOVED(                     "Home location changed successfully!"),
    HOME_NOT_SET(                   "You have no home yet! Set one with /sethome..."),
    HOME_OTHER_NOT_SET(             "%p doesn't seem to have a home yet."),

    //Invitations
    HOME_INVITE(                    "%p has been invited to your home!"),
    HOME_INVITED(                   "You have been invited to %p's home!"),
    HOME_OTHER_NOT_INVITED(         "%p wasn't invited to your home!"),
    HOME_OTHER_INVITED(             "%p is already invited to your home!"),
    HOME_UNINVITE(                  "%p has been removed from your home invites."),
    HOME_UNINVITED(                 "%p has uninvited you from his/her home."),
    HOME_NOT_INVITED(               "You are not invited to %p's home!"),
    HOME_INVITE_SELF(               "You cannot invite yourself..."),
    HOME_UNINVITE_SELF(             "You cannot uninvite yourself..."),
    PLAYER_ONLINE(                  "%p has to be online for this!"),

    //Usages
    USAGE_HOME(                     "Usage: /home %p"),
    USAGE_TP_HOME(                  "Usage: /home %p"),
    USAGE_HOME_INVITE(              "Usage: /home invite %p"),
    USAGE_HOME_SET(                 "Usage: /sethome"),
    USAGE_HOME_UNINVITE(            "Usage: /home uninvite %p"),
    USAGE_HOME_ILIST(               "Usage: /home ilist"),
    USAGE_CLEAR(                    "Usage: /home clear %p"),
    USAGE_EDIT(                     "Usage: /home edit %p <X> <Y> <Z> [<world>]"),

    //Console
    CONSOLE_ENABLING(               "Enabling Homes v%p..."),
    CONSOLE_DATA_EXISTS(            "Database exists, reading data..."),
    CONSOLE_DONE_READING(           "Done reading!"),
    CONSOLE_DATA_EXISTS_NOT(        "No existing database, creating a new one..."),
    CONSOLE_ERROR_FILE(             "Error while creating file.."),
    CONSOLE_DATA_SAVED(             "successfully serialized to JSON! saved locations to database"),
    CONSOLE_DATA_SAVE_FAIL(         "failed to save locations, file does not exist or is locked!"),
    CONSOLE_DISABLING(              "Disabling Homes..."),

    //Admin
    HOMES_VERSION(                  "Homes v%p"),
    HOMES_SITE(                     "Visit the following for more info: %p"),
    HOME_CLEAR(                     "Cleared %p's home from the database"),
    HOME_EDIT_INVALID(              "Invalid parameters, use numbers please"),
    HOME_EDIT_NOWORLD(              "This world does not exist, please check the name"),
    HOME_EDIT_SUCCESS(              "Home edited successfully!"),

    //Residence
    RES_SETHOME_NOT_ALLOWED(        "You need the %p flag in order to set your home here!"),
    RES_ENABLING(                   "Manually Enabling Residence!"),
    RES_ENABLED(                    "Successfully hooked into Residence!"),
    RES_DISABLED(                   "Residence NOT Installed, DISABLED!"),

    //Importers
    HOME_IMPORT_ESSENTIALS(         "FOUND ESSENTIALS PATH: %p"),
    HOME_IMPORT_COUNT(              "IMPORTED %p HOMES IN TOTAL"),
    HOME_IMPORT_NONE(               "FOUND NO (NEW) VALID HOMES HERE!"),
    HOME_IMPORT_INVALID(               "FOUND %p INVALID HOMES HERE!"),

    //Misc/utility
    HOME_INVITES(                   "Players invited to your home: %p"),
    HOME_HELP(
                                    "How to get along with Homes: \n" +
                                    ChatColor.WHITE+"=========================== \n" +
                                    ChatColor.GRAY+"-"+ChatColor.AQUA+" /home   "+ChatColor.YELLOW+"Teleports you to your home \n" +
                                    ChatColor.GRAY+"-"+ChatColor.AQUA+" /home set   "+ChatColor.YELLOW+"Sets your home to your location \n" +
                                    ChatColor.GRAY+"-"+ChatColor.AQUA+" /home (tp) <player>   "+ChatColor.YELLOW+"Teleports you to <player>'s home \n" +
                                    ChatColor.GRAY+"-"+ChatColor.AQUA+" /home invite <player>   "+ChatColor.YELLOW+"Invites <player> to your home \n" +
                                    ChatColor.GRAY+"-"+ChatColor.AQUA+" /home uninvite <player>   "+ChatColor.YELLOW+"Uninvites <player> from your home \n" +
                                    ChatColor.GRAY+"-"+ChatColor.AQUA+" /home ilist   "+ChatColor.YELLOW+"Displays a list of players invited to your home\n" +
                                    ChatColor.GRAY+"-"+ChatColor.AQUA+" /home help   "+ChatColor.YELLOW+"Displays this list of commands for Homes"
    ),
    HOME_HELP_ADMIN(
                                    ChatColor.GRAY+"-"+ChatColor.RED+" /home clear  "+ChatColor.YELLOW+"Clears somebody's home \n" +
                                    ChatColor.GRAY+"-"+ChatColor.RED+" /home version   "+ChatColor.YELLOW+"Shows version \n"
    ),
    HOME_SET_ERROR(                 "An error has occurred, please report this!"),
    HOME_NO_PERM(                   "You have no permission to use this command.");




    private final String message;

    private final static ChatColor textColor = ChatColor.YELLOW;
    private final static ChatColor tagColor = ChatColor.WHITE;
    private final static ChatColor nameColor = ChatColor.AQUA;


    public final static String tag = textColor+"["+tagColor+"Homes"+textColor+"] ";

    Messages(final String message){
        this.message = message;
    }

    public String parse(){
        return tag + this.message;
    }

    public String parseRaw(){
        return this.message;
    }

    public String parse(String player){
        return tag + this.message.replace("%p",nameColor+player+textColor);
    }

    public String parse(int integer){
        return tag + this.message.replace("%p",nameColor+Integer.toString(integer)+textColor);
    }

    public String parse(String[] players){
        String list = "";
        for(int i=0;i<players.length;i++){
            list+=nameColor+players[i]+textColor+", ";
        }

        if(!list.equals(""))
            list = list.substring(0,list.length()-2);
        else
            list+=nameColor+"(None)";

        return tag + this.message.replace("%p",list);
    }

    public String parse(NameID[] players){
        String list = "";
        for(int i=0;i<players.length;i++){
            list+=nameColor+players[i].getName()+textColor+", ";
        }
        if(!list.equals(""))
            list = list.substring(0,list.length()-2);
        else
            list+=nameColor+"(None)";

        return tag + this.message.replace("%p",list);
    }




}
