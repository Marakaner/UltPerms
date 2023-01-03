package net.marakaner.ultperms.group;

import com.google.common.reflect.TypeToken;
import net.marakaner.ultperms.UltPerms;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupManager {

    private List<Group> groups;

    public GroupManager() {


    }

    private void loadConfig() {

        File folder = new File("plugins/UltPerms");

        if(!folder.exists()) {
            folder.mkdir();
        }

        File groupConfig = new File("plugins/UltPerms/Groups.json");


        if(groupConfig.exists()) {
            this.groups = UltPerms.getInstance().getGson().fromJson(groupConfig.getPath(), new TypeToken<List<Group>>(){}.getType());
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

        Group adminGroup = new Group("admin", "§4Admin", "§4A §8| ", "§4Admin §8| ", ChatColor.DARK_RED, Arrays.asList("*"), 0);
        Group defaultGroup = new Group("default", "§7Default", "§7", "§7", ChatColor.GRAY, Arrays.asList(""), 0);

        this.groups.add(adminGroup);
        this.groups.add(defaultGroup);

        FileWriter fileWriter = new FileWriter("plugins/UltPerms/Groups.json");

        fileWriter.write(UltPerms.getInstance().getGson().toJson(this.groups));

        fileWriter.close();
    }

}
