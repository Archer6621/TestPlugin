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
    private Main main;

    public CommandSetHome(Main main){
        this.main = main;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {



        if(sender instanceof Player){
            Player player = (Player) sender;

            if(main.resEnabled) {
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
                List<HomeInfo> data = main.getData();
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
            main.print("WARNING: sethome failed for player " + player.getName() + "!");
            return true;
        }

        return true;
    }

}
