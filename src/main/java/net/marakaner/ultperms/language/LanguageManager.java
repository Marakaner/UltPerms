package net.marakaner.ultperms.language;

import jdk.javadoc.internal.doclets.toolkit.util.PreviewAPIListBuilder;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.language.config.MessagesConfig;
import net.marakaner.ultperms.language.config.LanguageConfig;
import net.marakaner.ultperms.player.PermissionPlayer;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LanguageManager {

    private final PlayerManager playerManager;

    private LanguageConfig mainConfig;
    private MessagesConfig defaultConfig;
    private MessagesConfig defaultGermanConfig;

    private Map<String, Language> languages = new HashMap<>();
    private Map<String, MessagesConfig> messages = new HashMap<>();

    public LanguageManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        loadMainConfig();
        loadLanguages();
    }


    private void loadMainConfig() {
        File file = new File("plugins/UltPerms/Languages.json");

        if(file.exists()) {
            try {
                Reader reader = Files.newBufferedReader(file.toPath());
                mainConfig = UltPerms.getInstance().getGson().fromJson(reader, LanguageConfig.class);
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            loadLanguages();

        } else {

            mainConfig = new LanguageConfig();
            mainConfig.prefix = "&7[&5UltPerms&7] ";
            mainConfig.languages = Arrays.asList(new Language("en-US", "American English"), new Language("de-DE", "German"));


            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(UltPerms.getInstance().getGson().toJson(mainConfig));
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            generateDefaultConfig();
        }
    }

    private void generateDefaultConfig() {

        File folder = new File("plugins/UltPerms/language/");

        if(!folder.exists()) folder.mkdir();

        HashMap<String, String> messages = new HashMap<>();
        messages.put("command.helpmap.first", "&3");

        defaultConfig = new MessagesConfig("en-US", messages);

    }

    private void loadLanguages() {

        for(String code : languages.keySet()) {
            messages.put(code, loadMessageConfig(code));
        }

    }

    private MessagesConfig loadMessageConfig(String code) {

        File file = new File("plugins/UltPerms/languages/" + code + ".json");

        Reader reader = null;

        try {
            reader = Files.newBufferedReader(file.toPath());
            return UltPerms.getInstance().getGson().fromJson(reader, MessagesConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public void sendMessage(PermissionPlayer player, String message) {
        Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());

        bukkitPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.get(player.getLanguage()).messages.get(message)));
    }



}
