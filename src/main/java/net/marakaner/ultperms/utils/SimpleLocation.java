package net.marakaner.ultperms.utils;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.marakaner.ultperms.sign.UltSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;

@AllArgsConstructor
@Setter
@Getter
public class SimpleLocation {

    @Expose
    private String world;
    @Expose
    private int x, y, z;

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SimpleLocation)) return false;

        SimpleLocation simpleLocation = (SimpleLocation) obj;

        return this.world.equals(simpleLocation.getWorld())
                && this.x == simpleLocation.getX()
                && this.y == simpleLocation.getY()
                && this.z == simpleLocation.getZ();
    }

    public SimpleLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = (int) location.getX();
        this.y = (int) location.getY();
        this.z = (int) location.getZ();
    }

    public Location toBukkitLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }


}
