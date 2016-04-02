import org.bukkit.command.*;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.broadcastMessage;

/**
 * Created by Archer on 30-Mar-16.
 */


public class CommandTest implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            String name = player.getName();
            broadcastMessage(name);
        }

        return true;
    }

}
