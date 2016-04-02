import org.bukkit.entity.Player;

/**
 * Created by Archer on 30-Mar-16.
 */
public class NameID {
    private String name;
    private String id;

    public NameID(String name, String id){
        this.name = name;
        this.id = id;
    }

    public NameID(Player player){
        this.name = player.getName();
        this.id = player.getUniqueId().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
