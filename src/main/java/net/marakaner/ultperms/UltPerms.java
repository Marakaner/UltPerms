package net.marakaner.ultperms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.marakaner.ultperms.database.DatabaseManager;
import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.permission.PermissionManager;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class UltPerms extends JavaPlugin {

    // Initializing a Singleton Instance to reach the Main Class
    @Getter
    private static UltPerms instance;

    @Getter
    private DatabaseManager databaseManager;

    @Getter
    private PermissionManager permissionManager;

    @Getter
    private GroupManager groupManager;

    @Getter
    private PlayerManager playerManager;


    // Creating a private instance for Gson
    @Getter
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onEnable() {
        instance = this;

        this.databaseManager = new DatabaseManager();
        this.permissionManager = new PermissionManager(this.databaseManager);
        this.groupManager = new GroupManager(this.permissionManager);
        this.playerManager = new PlayerManager(this.groupManager, this.permissionManager);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
