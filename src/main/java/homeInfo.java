import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Archer on 30-Mar-16.
 */
public class HomeInfo {

    private String name;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String world;
    private String Id;
    //Transient because we don't want GSON to serialize these variables
    private transient ArrayList<NameID> invitesArrayList;
    private transient boolean obsolete;
    private NameID[] invites;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NameID[] getInvites() {
        return invites;
    }

    public void setInvites(NameID[] invites) {
        this.invites = invites;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    //Bit of a retarded way to do this, but too lazy to deal with GSON's annoyances with generics
    public void invite(NameID playerID){
        invitesArrayList = new ArrayList<NameID>(Arrays.asList(invites));
        invitesArrayList.add(playerID);
        invites = invitesArrayList.toArray(new NameID[invitesArrayList.size()]);
    }

    public void uninvite(NameID playerID){
        invitesArrayList = new ArrayList<NameID>(Arrays.asList(invites));
        invitesArrayList.remove(playerID);
        invites = invitesArrayList.toArray(new NameID[invitesArrayList.size()]);
    }

    public HomeInfo(Player player){
        Location loc=player.getLocation();
        this.name = player.getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.world = loc.getWorld().getName();
        this.Id = player.getUniqueId().toString();
        this.invitesArrayList = new ArrayList<NameID>();
        this.invites = invitesArrayList.toArray(new NameID[invitesArrayList.size()]);
        this.obsolete = false;
    }

    public HomeInfo(NameID nameId, String world, double x, double y, double z, float yaw, float pitch ){
        this.name = nameId.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
        this.Id = nameId.getId();
        this.invitesArrayList = new ArrayList<NameID>();
        this.invites = invitesArrayList.toArray(new NameID[invitesArrayList.size()]);
        this.obsolete = false;
    }

    public Location toLocation(){
        World w = getBukkitWorld();
        return new Location(w,x,y,z,yaw,pitch);
    }

    public String getInviteName(Player player){
        String name = null;
        for (int j = 0; j < invites.length; j++) {
            if (invites[j].getName().equals(player.getName())) {
                name = invites[j].getName();
            }
        }
        return name;
    }

    public boolean isInvited(Player player){
        boolean invited = false;
        for (int j = 0; j < invites.length; j++) {
            if (invites[j].getName().equals(player.getName())) {
                invited = true;
            }
        }
        return invited;
    }


    public NameID getInvite(String player){
        NameID nameId = null;
        for (int j = 0; j < invites.length; j++) {
            if (invites[j].getName().equalsIgnoreCase(player)) {
                nameId = invites[j];
            }
        }
        return nameId;
    }

    public void updateHome(Player player){
        Location loc=player.getLocation();
        this.name = player.getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.world = loc.getWorld().getName();
    }

    public boolean isObsolete(){
        return obsolete;
    }

    public World getBukkitWorld(){
        World w = null;

        for(int i = 0; i < Bukkit.getWorlds().size() ; i++)
            if(Bukkit.getWorlds().get(i).getName().equals(world))
                w=Bukkit.getWorlds().get(i);

        if(w==null){
            Bukkit.getLogger().warning("[Homes] world does not exist! Using default world... Home marked for deletion");
            w=Bukkit.getWorlds().get(0);
            obsolete = true;
        }

        return w;
    }

}
