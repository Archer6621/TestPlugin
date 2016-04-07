package com.aquanova_mp.Homes;

import org.bukkit.entity.Player;

import java.util.Objects;

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
        if (!(o instanceof NameID)) return false;
        NameID nameID = (NameID) o;
        return Objects.equals(name, nameID.name) &&
                Objects.equals(id, nameID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
