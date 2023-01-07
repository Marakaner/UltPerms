package net.marakaner.ultperms.player;

import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.permission.PermissionManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.Permission;
import java.util.*;
import java.util.function.Consumer;

public class PlayerManager {

    private final GroupManager groupManager;
    private final PermissionManager permissionManager;

    private final Map<UUID, PermissionPlayer> players;

    public PlayerManager(GroupManager groupManager, PermissionManager permissionManager) {
        this.groupManager = groupManager;
        this.permissionManager = permissionManager;
        this.players = new HashMap<>();
    }

    //Loading player and register him in the cache

    public void registerPlayer(UUID uniqueId, Consumer<PermissionPlayer> succeed) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PermissionPlayer permissionPlayer = permissionManager.getPlayer(uniqueId);

                if(permissionPlayer == null) {

                    permissionPlayer = new PermissionPlayer()

                    permissionManager.registerPlayer();
                } else {
                    players.put(uniqueId, permissionPlayer);
                    succeed.accept(permissionPlayer);
                }
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    public void unregisterPlayer(UUID uniqueId, Consumer<PermissionPlayer> succeed) {

        PermissionPlayer permissionPlayer = this.players.get(uniqueId);
        this.players.remove(uniqueId.toString());
        succeed.accept(permissionPlayer);
    }

    //Getting the player by hand without storing it in the cache
    public void getPermissionPlayer(UUID uniqueId, Consumer<PermissionPlayer> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(permissionManager.getPlayer(uniqueId));
            }
        }.runTaskAsynchronously(UltPerms.getInstance());


    }


}
