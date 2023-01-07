package net.marakaner.ultperms.permission;

import com.google.common.reflect.TypeToken;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.database.DatabaseManager;
import net.marakaner.ultperms.language.Language;
import net.marakaner.ultperms.player.PermissionPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PermissionManager {

    private final Type type = new TypeToken<List<String>>(){}.getType();

    private DatabaseManager databaseManager;

    private final List<PermissionPlayer> registeredPlayer = new ArrayList<>();


    public PermissionManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;

        createTables();
    }

    private void createTables() {

        new BukkitRunnable() {
            @Override
            public void run() {

                PreparedStatement ps = null;

                try {
                    ps = databaseManager.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS group_permission(identifier VARCHAR(100) PRIMARY KEY, permission MEDIUMTEXT)");
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


                try {
                    ps = databaseManager.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS player_permission(uuid VARCHAR(100) PRIMARY KEY, permission MEDIUMTEXT, groups MEDIUMTEXT, language VARCHAR(100))");
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
        }.runTaskAsynchronously(UltPerms.getInstance());

    }

    public List<String> getGroupPermission(String identifier) {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("SELECT permission FROM group_permission WHERE identifier=?");
            ps.setString(1, identifier);
            rs = ps.executeQuery();
            if(rs.next()) {
                List<String> permission = UltPerms.getInstance().getGson().fromJson(rs.getString("permission"), type);
                return permission;
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

        return null;
    }

    public List<String> getPlayerPermission(UUID uuid) {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("SELECT permission FROM player_permission WHERE uuid=?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if(rs.next()) {
                List<String> permission = UltPerms.getInstance().getGson().fromJson(rs.getString("permission"), type);
                return permission;
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

        return null;
    }

    public List<String> getPlayerGroups(UUID uuid) {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("SELECT groups FROM player_permission WHERE uuid=?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if(rs.next()) {
                List<String> permission = UltPerms.getInstance().getGson().fromJson(rs.getString("groups"), type);
                return permission;
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

        return null;
    }

    public PermissionPlayer getPlayer(UUID uuid) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("SELECT * FROM player_permission WHERE uuid=?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if(rs.next()) {

                List<String> permission = UltPerms.getInstance().getGson().fromJson(rs.getString("permission"), type);
                List<String> groups = UltPerms.getInstance().getGson().fromJson(rs.getString("group"), type);
                String language = rs.getString("language");

                PermissionPlayer permissionPlayer = new PermissionPlayer(uuid);
                permissionPlayer.setGroups(groups);
                permissionPlayer.addPermission(permission);
                permissionPlayer.setLanguage(language);

                return permissionPlayer;
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

        return null;
    }

    public void registerPlayer(PermissionPlayer permissionPlayer) {
        PreparedStatement ps = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("INSERT INTO player_permission(uuid, permission, groups, language) VALUES (?,?,?,?)");
            ps.setString(1, permissionPlayer.getUniqueId().toString());
            ps.setString(2, UltPerms.getInstance().getGson().toJson(permissionPlayer.getPermission()));
            ps.setString(3, UltPerms.getInstance().getGson().toJson(permissionPlayer.getGroups()));
            ps.setString(4, UltPerms.getInstance().getGson().toJson(permissionPlayer.getLanguage()));
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
