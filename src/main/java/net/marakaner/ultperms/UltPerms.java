package net.marakaner.ultperms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.marakaner.ultperms.commands.RankCommand;
import net.marakaner.ultperms.commands.TestCommand;
import net.marakaner.ultperms.commands.UltPermsCommand;
import net.marakaner.ultperms.database.DatabaseManager;
import net.marakaner.ultperms.document.IDocument;
import net.marakaner.ultperms.document.gson.JsonDocument;
import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.listener.ChatListener;
import net.marakaner.ultperms.listener.JoinListener;
import net.marakaner.ultperms.player.PlayerManager;
import net.marakaner.ultperms.sign.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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

    @Getter
    IDocument config;

    @Getter
    private String prefix;

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

        loadConfig();
        this.prefix = ChatColor.translateAlternateColorCodes('&', this.config.getString("prefix"));

        this.databaseManager = new DatabaseManager();
        this.databaseManager.connect();


        this.groupManager = new GroupManager(databaseManager, finished -> {
            if(!finished) Bukkit.getPluginManager().disablePlugin(this);

            this.playerManager = new PlayerManager(databaseManager, groupManager, finished1 -> {
                if(!finished1) Bukkit.getPluginManager().disablePlugin(this);


                this.languageManager = new LanguageManager(playerManager);
                this.signManager = new SignManager(playerManager, databaseManager, config, finished2 -> {
                    Bukkit.getPluginManager().registerEvents(new JoinListener(playerManager), this);
                    Bukkit.getPluginManager().registerEvents(new ChatListener(playerManager), this);
                    Bukkit.getPluginCommand("rank").setExecutor(new RankCommand(playerManager, languageManager, groupManager));
                    Bukkit.getPluginCommand("ultperms").setExecutor(new UltPermsCommand(playerManager, groupManager, languageManager));
                    Bukkit.getPluginCommand("test").setExecutor(new TestCommand());
                });

            });
        });


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfig() {

        File file = new File("plugins/UltPerms/config.yml");

        if(!file.exists()) {
            generateDefaultConfig(file);
            return;
        }

        this.config = JsonDocument.newDocument(file.toPath());

    }

    private void generateDefaultConfig(File file) {
        try {
            file.createNewFile();

            IDocument document = new JsonDocument()
                    .append("prefix", "&7[&5UltPerms&7] ")
                    .append("chat_layout", "%group_chat_prefix%%player_name%&7: %message%")
                    .append("sign.layout.first", "%group_color%%player_name%")
                    .append("sign.layout.second", "%group_name%")
                    .append("sign.layout.third", "&6%group_time_day% days")
                    .append("sign.layout.fourth", "&6%group_time_hour% hours, %group_time_min% mins");

            document.write(file.toPath());
            this.config = document;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
