package com.aquanova_mp.Homes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Archer on 30-Mar-16.
 */
public class HomeInfo {

    //All home/user data we need to save
    private String name;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String world;
    private String Id;
    private NameID[] invites;   //Array that holds the player NameID's that are invited to this particular home

    //Transient because we don't want GSON to serialize these variables
    private transient ArrayList<NameID> invitesArrayList;
    private transient boolean obsolete;


    //All relevant getters and setters
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

    //Adds a player's Name + ID combination (NameID) to the invite list
    public void invite(NameID playerID){
        invitesArrayList = new ArrayList<NameID>(Arrays.asList(invites));
        invitesArrayList.add(playerID);
        invites = invitesArrayList.toArray(new NameID[invitesArrayList.size()]);
    }

    //Removes a player's NameID from the invite list
    public void uninvite(NameID playerID){
        invitesArrayList = new ArrayList<NameID>(Arrays.asList(invites));
        invitesArrayList.remove(playerID);
        invites = invitesArrayList.toArray(new NameID[invitesArrayList.size()]);
    }

    //Constructor that is used by default
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

    //Constructor for handling home imports from other plugins
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

    //Convert location data to actual location
    public Location toLocation(){
        World w = getBukkitWorld();
        return new Location(w,x,y,z,yaw,pitch);
    }

    //Checks whether a certain player is invited by checking the UUID, updates the name as well in case it has changed
    public boolean isInvited(Player player){
        boolean invited = false;
        for (int j = 0; j < invites.length; j++) {
            if (invites[j].getId().equals(player.getUniqueId().toString())) {
                invited = true;
                invites[j].setName(player.getName());
            }
        }
        return invited;
    }

    //Finds the nameID of the requested player and returns it, null if not found (compares by name, since that is what the user inputs in the command)
    public NameID getInvite(String player){
        NameID nameId = null;
        for (int j = 0; j < invites.length; j++) {
            if (invites[j].getName().equalsIgnoreCase(player)) {
                nameId = invites[j];
            }
        }
        return nameId;
    }

    //Update the home's location, the world it is in, and the owner's name in case it has changed
    public void updateHome(Player player){
        Location loc=player.getLocation();
        this.name = player.getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.world = loc.getWorld().getName();
    }

    //Check whether home is obsolete. Homes get marked as obsolete if the world they were in no longer exists, here obsolete basically means: marked for removal.
    public boolean isObsolete(){
        return obsolete;
    }

    //Tries to find the actual world on the server that this home is tied to, by using the world name that is stored
    //Will mark a home as obsolete if no such world can be found, returns the default world in this case
    public World getBukkitWorld(){
        World w = null;
        for(int i = 0; i < Bukkit.getWorlds().size() ; i++) {
            if (Bukkit.getWorlds().get(i).getName().equals(world)) {
                w = Bukkit.getWorlds().get(i);
            }
        }
        if(w==null){
            System.out.println(Messages.tag+"world does not exist! Using default world... Home marked for deletion");
            w=Bukkit.getWorlds().get(0);
            obsolete = true;
        }
        return w;
    }

    //equals...
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HomeInfo)) return false;
        HomeInfo homeInfo = (HomeInfo) o;
        return Double.compare(homeInfo.x, x) == 0 &&
                Double.compare(homeInfo.y, y) == 0 &&
                Double.compare(homeInfo.z, z) == 0 &&
                Float.compare(homeInfo.yaw, yaw) == 0 &&
                Float.compare(homeInfo.pitch, pitch) == 0 &&
                obsolete == homeInfo.obsolete &&
                Objects.equals(name, homeInfo.name) &&
                Objects.equals(world, homeInfo.world) &&
                Objects.equals(Id, homeInfo.Id) &&
                Objects.equals(invitesArrayList, homeInfo.invitesArrayList) &&
                Arrays.equals(invites, homeInfo.invites);
    }

    //hashcode...
    @Override
    public int hashCode() {
        return Objects.hash(name, x, y, z, yaw, pitch, world, Id, invitesArrayList, obsolete, invites);
    }
}
