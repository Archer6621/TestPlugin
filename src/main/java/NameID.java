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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameID nameID = (NameID) o;

        if (name != null ? !name.equals(nameID.name) : nameID.name != null) return false;
        return id != null ? id.equals(nameID.id) : nameID.id == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
