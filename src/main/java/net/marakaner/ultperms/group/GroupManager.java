package net.marakaner.ultperms.group;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.database.DatabaseManager;

import net.marakaner.ultperms.document.IDocument;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class GroupManager {

    private final Type stringListType = new TypeToken<List<String>>() {
    }.getType();
    private final Type groupListType = new TypeToken<List<Group>>() {
    }.getType();
    private final DatabaseManager databaseManager;

    private final Map<String, Group> groups = new HashMap<>();
    private final Gson gson = UltPerms.getInstance().getGson();

    public GroupManager(DatabaseManager databaseManager, Consumer<Boolean> finished) {
        this.databaseManager = databaseManager;
        new BukkitRunnable() {
            @Override
            public void run() {
                createTable();
                loadGroups();
                finished.accept(true);
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    public Group getDefaultGroup() {
        for (Group group : groups.values()) {
            if (group.isDefaultGroup()) {
                return group;
            }
        }
        return null;
    }

    public Group getGroup(String identifier) {
        return this.groups.getOrDefault(identifier.toLowerCase(), null);
    }

    public void createGroup(Group group) {
        groups.put(group.getIdentifier().toLowerCase(), group);
        new BukkitRunnable() {
            @Override
            public void run() {
                registerNewGroup(group.getIdentifier(), group.getPermission());

                File file = new File("plugins/UltPerms/Groups.json");
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(gson.toJson(new ArrayList<>(groups.values())));
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    public void deleteGroup(String groupIdentifier) {
        Group group = getGroup(groupIdentifier);
        groups.remove(groupIdentifier);

        new BukkitRunnable() {
            @Override
            public void run() {
                File file = new File("plugins/UltPerms/Groups.json");
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(gson.toJson(new ArrayList<>(groups.values())));
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                PreparedStatement ps = null;

                try {
                    ps = databaseManager.getConnection().prepareStatement("DELETE FROM group_info WHERE identifier=?");
                    ps.setString(1, groupIdentifier);
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

    public void addPermission(String groupIdentifier, String permission) {
        Group group = getGroup(groupIdentifier);
        List<String> permissions = group.getPermission();
        permissions.add(permission.toLowerCase());
        group.setPermission(permissions);
        this.groups.replace(groupIdentifier, group);
        saveGroups();
    }

    public void removePermission(String groupIdentifier, String permission) {
        Group group = getGroup(groupIdentifier);
        List<String> permissions = group.getPermission();
        permissions.remove(permission.toLowerCase());
        group.setPermission(permissions);
        this.groups.replace(groupIdentifier, group);
        saveGroups();
    }

    public void setDisplayName(String groupIdentifier, String displayName) {
        Group group = getGroup(groupIdentifier);
        group.setDisplayName(displayName);
        this.groups.replace(groupIdentifier, group);
        saveGroups();
    }

    public void setColor(String groupIdentifier, ChatColor color) {
        Group group = getGroup(groupIdentifier);
        group.setColor(color);
        this.groups.replace(groupIdentifier, group);
        saveGroups();
    }

    public void setTabPriority(String groupIdentifier, int tabPriority) {
        Group group = getGroup(groupIdentifier);
        group.setTabPriority(tabPriority);
        this.groups.replace(groupIdentifier, group);
        saveGroups();
    }

    public void setPriority(String groupIdentifier, int priority) {
        Group group = getGroup(groupIdentifier);
        group.setPriority(priority);
        this.groups.replace(groupIdentifier, group);
        saveGroups();
    }

    public void setTabPrefix(String groupIdentifier, String tabPrefix) {
        Group group = getGroup(groupIdentifier);
        group.setTabPrefix(tabPrefix);
        this.groups.replace(groupIdentifier, group);
        saveGroups();
    }

    public void setChatPrefix(String groupIdentifier, String chatPrefix) {
        Group group = getGroup(groupIdentifier);
        group.setChatPrefix(chatPrefix);
        this.groups.replace(groupIdentifier, group);
        saveGroups();
    }

    public void setDefaultGroup(String groupIdentifier, boolean defaultGroup) {
        Group group = getGroup(groupIdentifier);
        group.setDefaultGroup(defaultGroup);
        this.groups.replace(groupIdentifier, group);
        saveGroups();
    }

    private void loadGroups() {

        File file = new File("plugins/UltPerms/Groups.json");

        if (!file.exists()) {
            try {

                generateDefaultGroups();

                file.createNewFile();

                FileWriter fileWriter = new FileWriter(file);
                gson.toJson(new ArrayList<>(groups.values()), fileWriter);
                fileWriter.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {

            try {
                Reader reader = Files.newBufferedReader(file.toPath());
                List<Group> groupList = gson.fromJson(reader, groupListType);
                reader.close();

                for (Group all : groupList) {
                    all.setPermission(getPermission(all.getIdentifier()));
                    this.groups.put(all.getIdentifier().toLowerCase(), all);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private List<String> getPermission(String groupIdentifier) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("SELECT * FROM group_info WHERE identifier=?");
            ps.setString(1, groupIdentifier);
            rs = ps.executeQuery();
            if (rs.next()) {
                return gson.fromJson(rs.getString("permission"), stringListType);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }

    private void generateDefaultGroups() {

        Group adminGroup = new Group("admin", "Admin", ChatColor.DARK_RED, "&4A &7| &4", "&4Admin &7| &4", false, 0, 100, Arrays.asList("*"));
        Group defaultGroup = new Group("default", "Default", ChatColor.GRAY, "&7", "&7", true, 100, 0, new ArrayList<>());

        registerNewGroup(adminGroup.getIdentifier(), adminGroup.getPermission());
        registerNewGroup(defaultGroup.getIdentifier(), defaultGroup.getPermission());

        this.groups.put(adminGroup.getIdentifier(), adminGroup);
        this.groups.put(defaultGroup.getIdentifier(), defaultGroup);
    }

    private void saveGroups() {
        new BukkitRunnable() {
            @Override
            public void run() {
                File file = new File("plugins/UltPerms/Groups.json");

                try {
                    FileWriter fileWriter = new FileWriter(file);
                    gson.toJson(new ArrayList<>(groups.values()), fileWriter);
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for (Group all : groups.values()) {
                    updateGroup(all.getIdentifier(), all.getPermission());
                }

            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    private void updateGroup(String identifier, List<String> permission) {
        PreparedStatement ps = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("UPDATE group_info SET permission=? WHERE identifier=?");
            ps.setString(1, gson.toJson(permission, stringListType));
            ps.setString(2, identifier);
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

    private void registerNewGroup(String identifier, List<String> permission) {
        PreparedStatement ps = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("INSERT INTO group_info(identifier, permission) VALUES (?,?)");
            ps.setString(1, identifier);
            ps.setString(2, gson.toJson(permission, stringListType));
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

    private void createTable() {
        PreparedStatement ps = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS group_info(identifier VARCHAR(100), permission MEDIUMTEXT)");
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
