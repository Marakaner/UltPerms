package net.marakaner.ultperms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.marakaner.ultperms.commands.RankCommand;
import net.marakaner.ultperms.commands.TestCommand;
import net.marakaner.ultperms.commands.UltPermsCommand;
import net.marakaner.ultperms.database.DatabaseManager;
import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.listener.ChatListener;
import net.marakaner.ultperms.listener.JoinListener;
import net.marakaner.ultperms.player.PlayerManager;
import net.marakaner.ultperms.sign.SignManager;
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

    @Getter
    private LanguageManager languageManager;

    @Getter
    private SignManager signManager;


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

                this.languageManager = new LanguageManager(playerManager);
                this.signManager = new SignManager();

                Bukkit.getPluginManager().registerEvents(new JoinListener(playerManager), this);
                Bukkit.getPluginManager().registerEvents(new ChatListener(playerManager), this);
                Bukkit.getPluginCommand("rank").setExecutor(new RankCommand(playerManager, languageManager, groupManager));
                Bukkit.getPluginCommand("ultperms").setExecutor(new UltPermsCommand(playerManager, groupManager, languageManager));
                Bukkit.getPluginCommand("test").setExecutor(new TestCommand());
            });
        });


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
