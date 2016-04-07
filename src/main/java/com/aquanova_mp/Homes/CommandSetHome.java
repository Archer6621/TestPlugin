package com.aquanova_mp.Homes;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Created by Archer on 30-Mar-16.
 */
public class CommandSetHome implements CommandExecutor {
    private Homes homes;

    public CommandSetHome(Homes homes){
        this.homes = homes;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {



        if(sender instanceof Player){
            Player player = (Player) sender;

            if(homes.resEnabled || !player.hasPermission("homes.admin")) {
                Location loc = player.getLocation();
                ClaimedResidence res = Residence.getResidenceManager().getByLoc(loc);
                if (!(res == null)) {

                    ResidencePermissions perms = res.getPermissions();

                    String flag = "build";

                    boolean hasPermission = perms.playerHas(player.getName(), flag, true);

                    if (!hasPermission) {
                        player.sendMessage(Messages.RES_SETHOME_NOT_ALLOWED.parse(flag));
                        return true;
                    }
                }
            }

            if(!(player.getLocation()==null)) {
                List<HomeInfo> data = homes.getData();
                if(!(data==null)) {
                    for (int i = 0; i < data.size(); i++) {
                        if (player.getUniqueId().equals(UUID.fromString(data.get(i).getId()))) {
                            data.get(i).updateHome(player);
                            player.sendMessage(Messages.HOME_MOVED.parse());
                            return true;
                        }
                    }

                    data.add(new HomeInfo(player));
                    player.sendMessage(Messages.HOME_SET.parse());
                    return true;
                }
            }
            player.sendMessage(Messages.HOME_SET_ERROR.parse());
            homes.print("WARNING: sethome failed for player " + player.getName() + "!");
            return true;
        }

        return true;
    }

}
