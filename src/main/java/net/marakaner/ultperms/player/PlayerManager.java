package net.marakaner.ultperms.player;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Getter;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.database.DatabaseManager;
import net.marakaner.ultperms.group.Group;
import net.marakaner.ultperms.group.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PlayerManager {

    private final Type stringListType = new TypeToken<List<String>>() {}.getType();
    private final Type groupMapType = new TypeToken<Map<String, Long>>() {}.getType();
    private final Gson gson = UltPerms.getInstance().getGson();
    private final DatabaseManager databaseManager;
    private final GroupManager groupManager;

    @Getter
    private final Map<UUID, PermissionPlayer> permissionPlayers = new HashMap<>();

    public PlayerManager(DatabaseManager databaseManager, GroupManager groupManager, Consumer<Boolean> finished) {
        this.databaseManager = databaseManager;
        this.groupManager = groupManager;
        new BukkitRunnable() {
            @Override
            public void run() {
                createTables();
                finished.accept(true);
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    private void createTables() {
        PreparedStatement ps = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS player_info(uuid VARCHAR(100), name VARCHAR(100), lower_name VARCHAR(100), permission MEDIUMTEXT, groups MEDIUMTEXT, language VARCHAR(100))");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getHighestPermissionGroup(UUID uniqueId, Consumer<Group> groupConsumer) {
        getPermissionPlayer(uniqueId, permissionPlayer -> {
            Group highestGroup = null;

            for(String identifier : permissionPlayer.getGroups().keySet()) {
                Group group = groupManager.getGroup(identifier);
                if(highestGroup == null || highestGroup.getPriority() < group.getPriority()) {
                    highestGroup = group;
                }
            }

            groupConsumer.accept(highestGroup);
        });
    }

    public void getRemainingGroupTime(UUID uniqueId, String groupIdentifier, Consumer<Long> remainingTime) {
        getPermissionPlayer(uniqueId, permissionPlayer -> {
            remainingTime.accept(permissionPlayer.getGroups().get(groupIdentifier) - System.currentTimeMillis());
        });
    }

    public void getRemainingGroupTimeInDays(UUID uniqueId, String groupIdentifier, Consumer<Long> remainingTime) {
        getRemainingGroupTime(uniqueId, groupIdentifier, time -> {
            remainingTime.accept(TimeUnit.MILLISECONDS.toDays(time));
        });
    }

    public void getRemainingGroupTimeInMinutes(UUID uniqueId, String groupIdentifier, Consumer<Long> remainingTime) {
        getRemainingGroupTime(uniqueId, groupIdentifier, time -> {
            long days = TimeUnit.MILLISECONDS.toDays(time);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(time - TimeUnit.DAYS.toMillis(days));
            remainingTime.accept(minutes);
        });
    }

    public void getRemainingGroupTimeInSeconds(UUID uniqueId, String groupIdentifier, Consumer<Long> remainingTime) {
        getRemainingGroupTime(uniqueId, groupIdentifier, time -> {
            long days = TimeUnit.MILLISECONDS.toDays(time);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(time - TimeUnit.DAYS.toMillis(days));
            long seconds = TimeUnit.MILLISECONDS.toSeconds(time - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.DAYS.toMillis(days));
            remainingTime.accept(seconds);
        });
    }

    public void addGroup(UUID uniqueId, String groupIdentifier, Long timestamp, Consumer<Boolean> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                getPermissionPlayer(uniqueId, permissionPlayer -> {

                    Map<String, Long> groups = permissionPlayer.getDirectGroups();
                    groups.put(groupIdentifier, timestamp);
                    permissionPlayer.setGroups(groups);
                    updatePlayer(permissionPlayer);

                    if(isInCache(uniqueId)) applyPermission(permissionPlayer);

                    consumer.accept(true);
                });
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    public void removeGroup(UUID uniqueId, String groupIdentifier, Consumer<Boolean> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                getPermissionPlayer(uniqueId, permissionPlayer -> {

                    Map<String, Long> groups = permissionPlayer.getDirectGroups();
                    groups.remove(groupIdentifier);
                    permissionPlayer.setGroups(groups);
                    updatePlayer(permissionPlayer);

                    if(isInCache(uniqueId)) applyPermission(permissionPlayer);

                    consumer.accept(true);
                });
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    public void addPermission(UUID uniqueId, String permission, Consumer<Boolean> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                getPermissionPlayer(uniqueId, permissionPlayer -> {

                    List<String> playerPermissions = permissionPlayer.getPermissions();
                    playerPermissions.add(permission.toLowerCase());
                    permissionPlayer.setPermissions(playerPermissions);
                    updatePlayer(permissionPlayer);

                    if(isInCache(uniqueId)) applyPermission(permissionPlayer);

                    consumer.accept(true);
                });
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    public void removePermission(UUID uniqueId, String permission, Consumer<Boolean> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                getPermissionPlayer(uniqueId, permissionPlayer -> {

                    List<String> playerPermissions = permissionPlayer.getPermissions();
                    playerPermissions.remove(permission.toLowerCase());
                    permissionPlayer.setPermissions(playerPermissions);
                    updatePlayer(permissionPlayer);

                    if(isInCache(uniqueId)) applyPermission(permissionPlayer);

                    consumer.accept(true);
                });
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    public PermissionPlayer getCachedPlayer(UUID uniqueId) {
        return permissionPlayers.getOrDefault(uniqueId, null);
    }

    private PermissionPlayer getCachedPlayer(String name) {
        for(PermissionPlayer all : this.permissionPlayers.values()) {
            if(all.getName().equalsIgnoreCase(name)) {
                return all;
            }
        }
        return null;
    }

    public boolean isInCache(UUID uniqueId) {
        for(PermissionPlayer player : this.permissionPlayers.values()) {
            if(player.getUniqueId().equals(uniqueId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCache(String name) {
        for (PermissionPlayer player : this.permissionPlayers.values()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void registerPlayer(Player player, Consumer<PermissionPlayer> consumer) {
        getPermissionPlayer(player.getUniqueId(), permissionPlayer -> {
            if (permissionPlayer == null) {
                permissionPlayer = new PermissionPlayer(player.getUniqueId());
                permissionPlayer.setName(player.getName());

                permissionPlayer.setGroups(new HashMap<>());
                permissionPlayer.setPermissions(new ArrayList<>());
                createPlayer(permissionPlayer);

                permissionPlayer.setAttachment(player.addAttachment(UltPerms.getInstance()));
            } else {
                permissionPlayer.setAttachment(player.addAttachment(UltPerms.getInstance()));

                if (!permissionPlayer.getName().equalsIgnoreCase(player.getName())) {
                    permissionPlayer.setName(player.getName());
                    updatePlayer(permissionPlayer);
                }

            }

            permissionPlayers.put(player.getUniqueId(), permissionPlayer);
            applyPermission(permissionPlayer);
            consumer.accept(permissionPlayer);
        });
    }

    public void unregisterPlayer(Player player) {
        this.permissionPlayers.remove(player.getUniqueId());
    }

    public void groupUpdated(Group group) {
        for(PermissionPlayer player : this.permissionPlayers.values()) {
            if(player.getDirectGroups().containsKey(group.getIdentifier())) {
                applyPermission(player);
            }
        }
    }

    public void groupDeleted(Group group) {
        for(PermissionPlayer player : this.permissionPlayers.values()) {
            if (player.getDirectGroups().containsKey(group.getIdentifier())) {
                removeGroup(player.getUniqueId(), group.getIdentifier(), succeed -> {});
            }
        }
    }

    private void applyPermission(PermissionPlayer permissionPlayer) {
        for(String all : permissionPlayer.getAttachment().getPermissions().keySet()) {
            permissionPlayer.getAttachment().unsetPermission(all);
        }

        for(String perm : permissionPlayer.getPermissions()) {
            permissionPlayer.getAttachment().setPermission(perm, true);
        }

        for(String groupIdentifier : permissionPlayer.getDirectGroups().keySet()) {
            Group group = groupManager.getGroup(groupIdentifier);
            for(String perm : group.getPermission()) {
                permissionPlayer.getAttachment().setPermission(perm, true);
            }
        }
    }

    public void getPermissionPlayer(UUID uuid, Consumer<PermissionPlayer> playerConsumer) {

        if(isInCache(uuid)) {
            playerConsumer.accept(getCachedPlayer(uuid));
        } else {
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                ps = databaseManager.getConnection().prepareStatement("SELECT * FROM player_info WHERE uuid=?");
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    PermissionPlayer permissionPlayer = new PermissionPlayer(UUID.fromString(rs.getString("uuid")));

                    permissionPlayer.setName(rs.getString("name"));
                    permissionPlayer.setGroups(gson.fromJson(rs.getString("groups"), groupMapType));
                    permissionPlayer.setPermissions(gson.fromJson(rs.getString("permission"), stringListType));
                    permissionPlayer.setLanguage(rs.getString("language"));

                    playerConsumer.accept(permissionPlayer);
                } else {
                    playerConsumer.accept(null);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void getPermissionPlayer(String name, Consumer<PermissionPlayer> playerConsumer) {
        if(isInCache(name)) {
            playerConsumer.accept(getCachedPlayer(name));
        } else {
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                ps = databaseManager.getConnection().prepareStatement("SELECT * FROM player_info WHERE lower_name=?");
                ps.setString(1, name.toLowerCase());
                rs = ps.executeQuery();
                if (rs.next()) {
                    PermissionPlayer permissionPlayer = new PermissionPlayer(UUID.fromString(rs.getString("uuid")));

                    permissionPlayer.setName(rs.getString("name"));
                    permissionPlayer.setGroups(gson.fromJson(rs.getString("groups"), groupMapType));
                    permissionPlayer.setPermissions(gson.fromJson(rs.getString("permission"), stringListType));
                    permissionPlayer.setLanguage(rs.getString("language"));

                    playerConsumer.accept(permissionPlayer);
                } else {
                    playerConsumer.accept(null);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void updatePlayer(PermissionPlayer permissionPlayer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;

                try {
                    ps = databaseManager.getConnection().prepareStatement("UPDATE player_info SET name=?, lower_name=?, permission=?, groups=?, language=? WHERE uuid=?");
                    ps.setString(1, permissionPlayer.getName());
                    ps.setString(2, permissionPlayer.getName().toLowerCase());
                    ps.setString(3, gson.toJson(permissionPlayer.getPermissions()));
                    ps.setString(4, gson.toJson(permissionPlayer.getDirectGroups()));
                    ps.setString(5, permissionPlayer.getLanguage());
                    ps.setString(6, permissionPlayer.getUniqueId().toString());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }.runTaskAsynchronously(UltPerms.getInstance());

    }

    private void createPlayer(PermissionPlayer permissionPlayer) {

        PreparedStatement ps = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("INSERT INTO player_info (uuid, name, lower_name, permission, groups, language) VALUES (?,?,?,?,?,?)");
            ps.setString(1, permissionPlayer.getUniqueId().toString());
            ps.setString(2, permissionPlayer.getName());
            ps.setString(3, permissionPlayer.getName().toLowerCase());
            ps.setString(4, gson.toJson(permissionPlayer.getPermissions()));
            ps.setString(5, gson.toJson(permissionPlayer.getDirectGroups()));
            ps.setString(6, permissionPlayer.getLanguage());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
