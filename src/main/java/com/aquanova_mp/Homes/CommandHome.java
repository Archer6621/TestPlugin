package com.aquanova_mp.Homes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

/**
 * Created by Archer on 30-Mar-16.
 */
public class CommandHome implements CommandExecutor {
    private Homes homes;
    private List<HomeInfo> data;
    private Player player;

    // TODO: 08-Apr-16 Get rid of this crap after subcommand implementation (or rather, make it an array of subcommands)
    private boolean blackListed(String string) {
        boolean blackListed = false;
        String[] blackList = {"help", "ilist", "clear", "invite", "uninvite", "clear", "version", "tp", "edit","info","list"};
        for (int i = 0; i < blackList.length; i++)
            if (blackList[i].equalsIgnoreCase(string))
                blackListed = true;
        return blackListed;
    }

    public CommandHome(Homes homes) {
        this.homes = homes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //Note that there is no console usage support yet, coming soon // TODO: 08-Apr-16 Add console support for administrative tasks
        if (sender instanceof Player) {

            player = (Player) sender;
            data = homes.getData();

            //Below are all if-else cases for handling subcommmands, to be replaced with actual subcommand class to neaten things up //// TODO: 08-Apr-16 Create subcommand class
            if (!(data == null) && !(player == null)) {
                if (args.length == 0 && permCheckPlayer("homes.tp.self")) {

                    // /home
                    home();

                } else if (args.length > 0) {

                    // /home help
                    if (args[0].equals("help")) {
                        if (player.hasPermission("homes.admin"))
                            helpAdmin();
                        else
                            help();
                    }

                    // /home ilist
                    else if (args[0].equals("ilist") && permCheckPlayer("homes.set")) {
                        ilist();
                    }

                    // /home ilist
                    else if (args[0].equals("list") && permCheckPlayer("homes.tp.other")) {
                        list();
                    }

                    // /home set
                    else if (args[0].equals("set") && permCheckPlayer("homes.set")) {
                        set();
                    }

                    // /home version
                    else if (args[0].equals("version") && permCheck("homes.admin")) {
                        version();
                    }

                    // /home tp <player>  (this is needed in case a player has a name that is one of the subcommands)
                    else if (args[0].equals("tp") && permCheckPlayer("homes.tp.other")) {
                        if ((args.length == 2))
                            homeOther(args[1]);
                        else
                            player.sendMessage(Messages.USAGE_TP_HOME.parse("<player>"));
                    }

                    // /home invite <player>
                    else if (args[0].equals("invite") && permCheckPlayer("homes.invite")) {
                        if ((args.length == 2))
                            invite(args[1]);
                        else
                            player.sendMessage(Messages.USAGE_HOME_INVITE.parse("<player>"));
                    }

                    // /home uninvite <player>
                    else if (args[0].equals("uninvite") && permCheckPlayer("homes.invite")) {
                        if ((args.length == 2))
                            uninvite(args[1]);
                        else
                            sendUsage(Messages.USAGE_HOME_UNINVITE);
                    }

                    // /home clear <player>
                    else if (args[0].equals("clear") && permCheck("homes.admin")) {
                        if ((args.length == 2))
                            clear(args[1]);
                        else
                            sendUsage(Messages.USAGE_CLEAR);
                    }

                    // /home info <player>
                    else if (args[0].equals("info") && permCheck("homes.admin")) {
                        if ((args.length == 2))
                            info(args[1]);
                        else
                            sendUsage(Messages.USAGE_INFO);
                    }

                    // /home edit <player> <X> <Y> <Z> [<world>]
                    else if (args[0].equals("edit") && permCheck("homes.admin")) {
                        if ((args.length == 5)) {
                            edit(args[1],args[2],args[3],args[4]);
                        }
                        else if ((args.length == 6)) {
                            editWorld(args[1],args[2],args[3],args[4],args[5]);
                        }
                        else
                            sendUsage(Messages.USAGE_EDIT);
                    }

                    // /home <player>
                    else if (permCheckPlayer("homes.tp.other") && !blackListed(args[0])) {
                        homeOther(args[0]);
                    }
                }

            }

        }
        return true;
    }

    //Teleports a player to his home, also sends the necessary messages for this
    public boolean home() {
        HomeInfo home = getHome(player);
        if (!(home == null)) {
            Location loc = home.toLocation();
            if (!home.isObsolete()) {
                player.teleport(loc);
                player.sendMessage(Messages.HOME_SELF.parse());
                return true;
            }
            player.sendMessage(Messages.HOME_OBSOLETE.parse());
            clear(home);
            return true;
        }
        player.sendMessage(Messages.HOME_NOT_SET.parse());
        return true;
    }

    //Teleports a player to another's home, if invited (this check is bypassed if the player has admin rights)
    //Will mark a home as obsolete and delete it if the world it was in no longer exists
    public boolean homeOther(String other) {

        if (other.equalsIgnoreCase(player.getName())) {
            home();
            return true;
        }

        HomeInfo otherHome = getHome(other);

        if (!(otherHome == null)) {
            if (otherHome.isInvited(player) || permCheckSilent("homes.admin")) {
                Location loc = otherHome.toLocation();
                if (!otherHome.isObsolete()) {
                    player.teleport(loc);
                    player.sendMessage(Messages.HOME_OTHER.parse(otherHome.getName()));

                    Player otherPlayer = getPlayer(other);
                    if (!(otherPlayer == null)) {
                        otherPlayer.sendMessage(Messages.HOME_OTHER_TO_SELF.parse(player.getName()));
                        return true;
                    }
                    return true;
                }
                player.sendMessage(Messages.HOME_OBSOLETE.parse(otherHome.getWorld()));
                clear(otherHome);
                return true;
            }
            player.sendMessage(Messages.HOME_NOT_INVITED.parse(otherHome.getName()));
            return true;
        }
        player.sendMessage(Messages.HOME_OTHER_NOT_SET.parse(other));
        return true;
    }


    //Invites a specified player to the user's home, the player has to be online to retrieve the proper name/UUID combination (might change in the future) // TODO: 08-Apr-16 Request UUID from the web if possible
    public boolean invite(String other) {

        if (other.equalsIgnoreCase(player.getName())) {
            player.sendMessage(Messages.HOME_INVITE_SELF.parse());
            return true;
        }

        Player otherPlayer = getPlayer(other);

        if (!(otherPlayer == null)) {
            HomeInfo home = getHome(player);
            if (!(home == null)) {

                if (home.isInvited(otherPlayer)) {
                    player.sendMessage(Messages.HOME_OTHER_INVITED.parse(otherPlayer.getName()));
                    return true;
                }

                home.invite(new NameID(otherPlayer));
                player.sendMessage(Messages.HOME_INVITE.parse(otherPlayer.getName()));
                otherPlayer.sendMessage(Messages.HOME_INVITED.parse(player.getName()));
                return true;
            }
            player.sendMessage(Messages.HOME_NOT_SET.parse(other));
            return true;
        }
        player.sendMessage(Messages.PLAYER_ONLINE.parse(other));
        return true;
    }

    //Uninvites a player from the user's home
    public boolean uninvite(String other) {

        if (other.equalsIgnoreCase(player.getName())) {
            player.sendMessage(Messages.HOME_UNINVITE_SELF.parse());
            return true;
        }

        HomeInfo home = getHome(player);

        if (!(home == null)) {
            NameID nameId = home.getInvite(other);
            if (!(nameId == null)) {
                home.uninvite(nameId);

                player.sendMessage(Messages.HOME_UNINVITE.parse(nameId.getName()));

                Player otherPlayer = getPlayer(other);
                if (!(otherPlayer == null)) {
                    otherPlayer.sendMessage(Messages.HOME_UNINVITED.parse(player.getName()));
                    return true;
                }
            }
            player.sendMessage(Messages.HOME_OTHER_NOT_INVITED.parse(other));
            return true;
        }
        player.sendMessage(Messages.HOME_NOT_SET.parse());
        return true;
    }

    //Shows the user a list with players that are invited to his home
    public boolean ilist() {
        HomeInfo home = getHome(player);

        if (!(home == null)) {
            String ilist = Messages.HOME_INVITES.parse(getHome(player).getInvites());
            player.sendMessage(ilist);
            return true;
        }
        player.sendMessage(Messages.HOME_NOT_SET.parse());
        return true;
    }

    //Shows the user a list with homes the user is invited to
    //Highly inefficient, might store this per home as a list or consider a tree datastructure for all homes for use at runtime //// TODO: 08-Apr-16 Make this crap more efficient
    public boolean list() {
        ArrayList<String> players = new ArrayList<String>();
        for (HomeInfo otherHome : data) {
            if(otherHome.isInvited(player)){
                players.add(otherHome.getName());
            }
        }
        String list = Messages.HOME_INVITES_OTHER.parse(players.toArray(new String[players.size()]));
        player.sendMessage(list);
        return true;
    }

    //Shows help to user
    public boolean help() {
        player.sendMessage(Messages.HOME_HELP.parse());
        return true;
    }

    //Shows help to user along with additional admin help
    public boolean helpAdmin() {
        player.sendMessage(Messages.HOME_HELP.parse() + "\n\n" + Messages.HOME_HELP_ADMIN.parse());
        return true;
    }

    //Sets the user's home
    //Handled through separate class at the moment, but this will change soon // TODO: 08-Apr-16 Move sethome to this class
    public boolean set() {
        getServer().dispatchCommand(player, "sethome");
        return true;
    }

    //Clears a player's home through name
    public boolean clear(String other) {
        HomeInfo home = getHome(other);
        if (!(home == null)) {
            data.remove(home);
            player.sendMessage(Messages.HOME_CLEAR.parse(other));
            return true;
        }
        player.sendMessage(Messages.HOME_OTHER_NOT_SET.parse(other));
        return true;
    }

    //Clears a player's home through actual homeinfo object
    public boolean clear(HomeInfo home) {
        if (!(home == null))
            data.remove(home);
        return true;
    }

    //Edits a player's home location
    public boolean edit(String other, String cx,String cy, String cz) {
        HomeInfo home = getHome(other);
        if (!(home==null)) {
            if (isCoordinates(cx, cy, cz)) {
                double x = Double.parseDouble(cx);
                double y = Double.parseDouble(cy);
                double z = Double.parseDouble(cz);

                home.setX(x);
                home.setY(y);
                home.setZ(z);

                sendPlayer(Messages.HOME_EDIT_SUCCESS);
                return true;
            }
            sendPlayer(Messages.HOME_EDIT_INVALID);
            return true;
        }
        sendPlayer(Messages.HOME_OTHER_NOT_SET,other);
        return true;
    }

    //Edits a player's home location, handles the case of the optional world argument
    public boolean editWorld(String other, String cx,String cy, String cz, String world) {
        HomeInfo home = getHome(other);
        if (!(home==null)) {
            if (isCoordinates(cx, cy, cz)) {
                double x = Double.parseDouble(cx);
                double y = Double.parseDouble(cy);
                double z = Double.parseDouble(cz);

                home.setX(x);
                home.setY(y);
                home.setZ(z);

                World w = getBukkitWorld(world);

                if(!(w==null)){
                    home.setWorld(w.getName());
                    sendPlayer(Messages.HOME_EDIT_SUCCESS);
                    return true;
                }
                sendPlayer(Messages.HOME_EDIT_NOWORLD);
                return true;
            }
            sendPlayer(Messages.HOME_EDIT_INVALID);
            return true;
        }
        sendPlayer(Messages.HOME_OTHER_NOT_SET,other);
        return true;
    }

    //Shows the user the current version + website
    public boolean version() {
        player.sendMessage(Messages.HOMES_VERSION.parse(homes.getDescription().getVersion()));
        player.sendMessage(Messages.HOMES_SITE.parse(homes.getDescription().getWebsite()));
        return true;
    }

    //Shows the user information regarding a certain player's home, if it exists // TODO: 08-Apr-16 Pretty this up a bit
    public boolean info(String other){
        HomeInfo home = getHome(other);
        if(!(home==null)){
            player.sendMessage((home.getWorld()));
            player.sendMessage((Double.toString(home.getX())));
            player.sendMessage((Double.toString(home.getY())));
            player.sendMessage((Double.toString(home.getZ())));
            return true;
        }
        sendPlayer(Messages.HOME_OTHER_NOT_SET,other);
        return true;
    }

    //Associates a player with a home from the database, returns this home, or null if it does not exist
    public HomeInfo getHome(Player player) {
        HomeInfo home = null;
        for (int i = 0; i < data.size(); i++) {
            if (player.getUniqueId().equals(UUID.fromString(data.get(i).getId()))) {
                home = data.get(i);
            }
        }
        return home;
    }

    //Associates a player's name with a home from the database, returns this home, or null if it does not exist
    public HomeInfo getHome(String playerName) {
        HomeInfo home = null;
        for (int i = 0; i < data.size(); i++) {
            if (playerName.equalsIgnoreCase(data.get(i).getName())) {
                home = data.get(i);
            }
        }
        return home;
    }

    //Permission checking with message sending
    public boolean permCheck(String permission) {
        boolean allowed = false;
        if (player.hasPermission(permission))
            allowed = true;
        else
            player.sendMessage(Messages.HOME_NO_PERM.parse());
        return allowed;
    }

    //Permission checking specific to regular players, implemented this because of issues with child permissions
    public boolean permCheckPlayer(String permission) {
        boolean allowed = false;
        if (player.hasPermission("homes.player") || player.hasPermission(permission))
            allowed = true;
        else
            player.sendMessage(Messages.HOME_NO_PERM.parse());
        return allowed;
    }

    //Silent permission checking (no message)
    public boolean permCheckSilent(String permission) {
        boolean allowed = false;
        if (player.hasPermission(permission))
            allowed = true;
        return allowed;
    }

    //Send a message from Enum Messages to a player, replace the message's variable with parameter 'variable'
    public void sendOther(Player receiver, Messages message, String variable){
        receiver.sendMessage(message.parse(variable));
    }

    //Send message without variables
    public void sendOther(Player receiver, Messages message){
        receiver.sendMessage(message.parse());
    }

    //Send message to current user with variable
    public void sendPlayer(Messages message, String variable){
        player.sendMessage(message.parse(variable));
    }

    //Send message to current user without variable
    public void sendPlayer(Messages message){
        player.sendMessage(message.parse());
    }

    //Send message to current user with pre-determined variable (for usages) // TODO: 08-Apr-16 Remove this once subcommands are implemented
    public void sendUsage(Messages message){
        player.sendMessage(message.parse("<player>"));
    }

    //Checks whether something is a double, returns false if it's not
    public boolean isDouble(String input){
        boolean isDouble = true;
        double d;
        try {
            d = Double.parseDouble(input);
        }
        catch (NumberFormatException e) {
            isDouble = false;
        }
        return isDouble;
    }

    //Same as isDouble but then for three values at a time, for instance... Coordinates.
    public boolean isCoordinates(String cx, String cy, String cz){
        if(!isDouble(cx)){return false;}
        if(!isDouble(cy)){return false;}
        if(!isDouble(cz)){return false;}
        return true;
    }

    //Finds the world on the server that is associated to a name/String passed through parameter "world"
    public World getBukkitWorld(String world){
        World w = null;

        for(int i = 0; i < Bukkit.getWorlds().size() ; i++)
            if(Bukkit.getWorlds().get(i).getName().equals(world))
                w=Bukkit.getWorlds().get(i);

        return w;
    }
}

