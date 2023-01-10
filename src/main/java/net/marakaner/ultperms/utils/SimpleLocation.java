package net.marakaner.ultperms.utils;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleLocation {

    @Expose
    private String world;
    @Expose
    private int x;
    @Expose
    private int y;
    @Expose
    private int z;

    public SimpleLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }


    public Location toBukkitLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

}
