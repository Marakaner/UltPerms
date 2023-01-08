package net.marakaner.ultperms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.marakaner.ultperms.commands.TestCommand;
import net.marakaner.ultperms.database.DatabaseManager;
import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.listener.JoinListener;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public final class UltPerms extends JavaPlugin {

    // Initializing a Singleton Instance to reach the Main Class
    @Getter
    private static UltPerms instance;

    @Getter
    private DatabaseManager databaseManager;

    @Getter
    private GroupManager groupManager;

    @Getter PlayerManager playerManager;


    // Creating a private instance for Gson
    @Getter
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    @Override
    public void onEnable() {
        instance = this;

        File folder = new File("plugins/UltPerms");

        if(!folder.exists()) folder.mkdir();

        this.databaseManager = new DatabaseManager();
        this.databaseManager.connect();

        this.groupManager = new GroupManager(databaseManager, finished -> {
            if(!finished) Bukkit.getPluginManager().disablePlugin(this);

            this.playerManager = new PlayerManager(databaseManager, groupManager, finished1 -> {
                if(!finished1) Bukkit.getPluginManager().disablePlugin(this);

                Bukkit.getPluginManager().registerEvents(new JoinListener(playerManager), this);
                Bukkit.getPluginCommand("test").setExecutor(new TestCommand());
            });
        });


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
