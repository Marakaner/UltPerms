package net.marakaner.ultperms.sign;

import com.google.gson.annotations.Expose;
import lombok.*;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.player.PlayerManager;
import net.marakaner.ultperms.utils.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
public class UltSign {

    private final static PlayerManager playerManager = UltPerms.getInstance().getPlayerManager();
    private final static GroupManager groupManager = UltPerms.getInstance().getGroupManager();

    private Sign sign;

    @Expose
    private SimpleLocation location;
    @Expose
    private UUID player;

    public void initSign() {
        this.sign = (Sign) Bukkit.getWorld(location.getWorld()).getBlockAt(location.getX(), location.getY(), location.getZ()).getState();
    }

    public void updateLines() {
        if(this.sign == null) {
            return;
        }

        playerManager.getPermissionPlayer(this.player, permissionPlayer -> {
            playerManager.getHighestPermissionGroup(this.player, group -> {
                this.sign.setLine(0, group.getColor() + permissionPlayer.getName());
                this.sign.setLine(1, group.getColor() + group.getDisplayName());

                long time = permissionPlayer.getGroups().get(group.getIdentifier());

                time -= System.currentTimeMillis();

                long days = TimeUnit.MILLISECONDS.toDays(time);
                long hours = TimeUnit.MILLISECONDS.toHours(time - TimeUnit.DAYS.toMillis(days));
                long minutes = TimeUnit.MILLISECONDS.toMinutes(time - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours));

                this.sign.setLine(2, "§e" + days + "days");
                this.sign.setLine(3, "§e" + hours + "hours" + ", " + minutes + "mins");
                this.sign.update();
            });
        });
    }


}
