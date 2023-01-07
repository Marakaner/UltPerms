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

        PermissionPlayer permissionPlayer = new PermissionPlayer(uniqueId);

        new BukkitRunnable() {
            @Override
            public void run() {

                List<String> permission = permissionManager.getPlayerPermission(uniqueId);

                if(permission == null) {

                    PermissionPlayer newPlayer = new PermissionPlayer(uniqueId);
                    newPlayer.setGroups(Arrays.asList(groupManager.getDefaultGroup().getIdentifier()));


                } else {

                    List<String> groups = permissionManager.getPlayerGroups(uniqueId);

                    permissionPlayer.setGroups(groups);
                    permissionPlayer.addPermission(permission);
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
                PermissionPlayer permissionPlayer = players.get(uniqueId);

                if(permissionPlayer != null) {
                    consumer.accept(permissionPlayer);
                } else {

                    permissionPlayer = new PermissionPlayer(uniqueId);
                    permissionPlayer.setGroups(permissionManager.getPlayerGroups(uniqueId));
                    permissionPlayer.addPermission(permissionManager.getPlayerPermission(uniqueId));
                }
            }
        }.runTaskAsynchronously(UltPerms.getInstance());


    }


}
