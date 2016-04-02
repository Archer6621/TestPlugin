import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

/**
 * Created by Archer on 30-Mar-16.
 */
public class CommandHome implements CommandExecutor {
    private Main main;
    private List<HomeInfo> data;
    private Player player;

    private boolean blackListed(String string) {
        boolean blackListed = false;
        String[] blackList = {"help", "ilist", "clear", "invite", "uninvite", "clear", "version", "tp"};
        for(int i = 0; i < blackList.length ; i++)
            if(blackList[i].equalsIgnoreCase(string))
                blackListed=true;
        return blackListed;
    }

    public CommandHome(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {

            player = (Player) sender;
            data = main.getData();

            if (!(data == null) && !(player == null)) {
                if (args.length == 0 && permCheck("homes.tp.self")) {

                    // /home
                    home();

                } else if (args.length > 0) {

                    // /home help
                    if (args[0].equals("help")) {
                        help();
                    }

                    // /home ilist
                    else if (args[0].equals("ilist") && permCheck("homes.ilist")) {
                        ilist();
                    }

                    // /home set
                    else if (args[0].equals("set") && permCheck("homes.set")) {
                        set();
                    }

                    // /home set
                    else if (args[0].equals("version") && permCheck("homes.admin")) {
                        version();
                    }

                    // /home tp <player>
                    else if (args[0].equals("tp") && permCheck("homes.tp.other")) {
                        if ((args.length == 2))
                            homeOther(args[1]);
                        else
                            player.sendMessage(Messages.USAGE_TP_HOME.parse("<player>"));
                    }

                    // /home invite <player>
                    else if (args[0].equals("invite") && permCheck("homes.invite")) {
                        if ((args.length == 2))
                            invite(args[1]);
                        else
                            player.sendMessage(Messages.USAGE_HOME_INVITE.parse("<player>"));
                    }

                    // /home uninvite <player>
                    else if (args[0].equals("uninvite") && permCheck("homes.invite")) {
                        if ((args.length == 2))
                            uninvite(args[1]);
                        else
                            player.sendMessage(Messages.USAGE_HOME_UNINVITE.parse("<player>"));
                    }

                    // /home clear <player>
                    else if (args[0].equals("clear") && permCheck("homes.admin")) {
                        if ((args.length == 2))
                            clear(args[1]);
                        else
                            player.sendMessage(Messages.USAGE_CLEAR.parse("<player>"));
                    }

                    // /home <player>
                    else if(permCheck("homes.tp.other") && !blackListed(args[0])) {
                        homeOther(args[0]);
                    }
                }

            }

        }
        return true;
    }

    public boolean home() {


        for (int i = 0; i < data.size(); i++) {
            if (player.getUniqueId().equals(UUID.fromString(data.get(i).getId()))) {
                player.teleport(data.get(i).toLocation(player.getWorld()));
                player.sendMessage(Messages.HOME_SELF.parse());
                return true;
            }
        }
        player.sendMessage(Messages.HOME_NOT_SET.parse());


        return true;


    }

    public boolean homeOther(String other) {

        if(other.equalsIgnoreCase(player.getName())){
            home();
            return true;
        }

        HomeInfo otherHome = getHome(other);

        if (!(otherHome == null)) {
            String name = otherHome.getInviteName(player);
            if (!(name == null)) {
                player.teleport(otherHome.toLocation(player.getWorld()));
                player.sendMessage(Messages.HOME_OTHER.parse(otherHome.getName()));

                Player otherPlayer = getPlayer(other);
                if (!(otherPlayer == null)) {
                    otherPlayer.sendMessage(Messages.HOME_OTHER_TO_SELF.parse(player.getName()));
                    return true;
                }
            }
            player.sendMessage(Messages.HOME_NOT_INVITED.parse(otherHome.getName()));
            return true;
        }
        player.sendMessage(Messages.HOME_OTHER_NOT_SET.parse(other));
        return true;
    }


    public boolean invite(String other) {

        if(other.equalsIgnoreCase(player.getName())){
            player.sendMessage(Messages.HOME_INVITE_SELF.parse());
            return true;
        }

        Player otherPlayer = getPlayer(other);

        if (!(otherPlayer == null)) {
            HomeInfo home = getHome(player);
            if (!(home == null)) {

                if(home.isInvited(otherPlayer)){
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

    public boolean uninvite(String other) {

        if(other.equalsIgnoreCase(player.getName())){
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

    public boolean help() {
        player.sendMessage(Messages.HOME_HELP.parse());
        return true;
    }

    public boolean helpAdmin() {
        player.sendMessage(Messages.HOME_HELP.parse() + "\n\n" + Messages.HOME_HELP_ADMIN.parse());
        return true;
    }


    public boolean set() {
        getServer().dispatchCommand(player, "sethome");
        return true;
    }

    public boolean clear(String other){
        HomeInfo home = getHome(other);
        if(!(home==null)){
            data.remove(home);
            player.sendMessage(Messages.HOME_CLEAR.parse(other));
            return true;
        }
        player.sendMessage(Messages.HOME_OTHER_NOT_SET.parse(other));
        return true;
    }

    public boolean version(){
        player.sendMessage(Messages.HOMES_VERSION.parse(main.getDescription().getVersion()));
        player.sendMessage(Messages.HOMES_SITE.parse("site"));
        return true;
    }

    public HomeInfo getHome(Player player) {
        HomeInfo home = null;
        for (int i = 0; i < data.size(); i++) {
            if (player.getUniqueId().equals(UUID.fromString(data.get(i).getId()))) {
                home = data.get(i);
            }
        }
        return home;
    }

    public HomeInfo getHome(String playerName) {
        HomeInfo home = null;
        for (int i = 0; i < data.size(); i++) {
            if (playerName.equalsIgnoreCase(data.get(i).getName())) {
                home = data.get(i);
            }
        }
        return home;
    }

    public boolean hasHome(Player player) {
        boolean home = false;
        for (int i = 0; i < data.size(); i++) {
            if (player.getUniqueId().equals(UUID.fromString(data.get(i).getId()))) {
                home = true;
            }
        }
        return home;
    }

    public boolean permCheck(String permission){
        boolean allowed = false;
        if(player.hasPermission(permission))
            allowed = true;
        else
            player.sendMessage(Messages.HOME_NO_PERM.parse());
        return allowed;
    }

}

