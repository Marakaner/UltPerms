package net.marakaner.ultperms.group;

import com.google.common.reflect.TypeToken;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.permission.PermissionManager;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupManager {

    private final PermissionManager permissionManager;
    private List<Group> groups;

    public GroupManager(PermissionManager permissionManager) {
        loadConfig();
        this.permissionManager = permissionManager;
    }

    private void loadConfig() {

        File folder = new File("plugins/UltPerms");

        if(!folder.exists()) {
            folder.mkdir();
        }

        File groupConfig = new File("plugins/UltPerms/Groups.json");


        if(groupConfig.exists()) {
            List<Group> tempGroups = UltPerms.getInstance().getGson().fromJson(groupConfig.getPath(), new TypeToken<List<Group>>(){}.getType());


            new BukkitRunnable() {
                @Override
                public void run() {
                    for(Group group : groups) {
                        group.setPermission(permissionManager.getGroupPermission(group.getIdentifier()));
                    }

                    groups = tempGroups;
                }
            }.runTaskAsynchronously(UltPerms.getInstance());


        } else {
            try {
                generateDefaultConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void generateDefaultConfig() throws IOException {

        groups = new ArrayList<>();

        Group adminGroup = new Group("admin", "§4Admin", "§4A §8| ", "§4Admin §8| ", ChatColor.DARK_RED, Arrays.asList("*"), 10, 0);
        Group defaultGroup = new Group("default", "§7Default", "§7", "§7", ChatColor.GRAY, Arrays.asList(""), 10, 0);

        this.groups.add(adminGroup);
        this.groups.add(defaultGroup);

        FileWriter fileWriter = new FileWriter("plugins/UltPerms/Groups.json");

        fileWriter.write(UltPerms.getInstance().getGson().toJson(this.groups));

        fileWriter.close();
    }

    public Group getGroupByIdentifier(String identifier) {
        return groups.stream().filter(group -> group.getIdentifier().equals(identifier)).findFirst().get();
    }

}
