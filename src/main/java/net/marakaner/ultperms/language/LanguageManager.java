package net.marakaner.ultperms.language;

import com.google.common.reflect.TypeToken;
import lombok.Getter;
import net.marakaner.ultperms.document.IDocument;
import net.marakaner.ultperms.document.gson.JsonDocument;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LanguageManager {

    private final Type languageType = new TypeToken<List<Language>>() {}.getType();
    private final PlayerManager playerManager;

    private IDocument languageConfig;
    private Map<String, IDocument> messages = new HashMap<>();

    @Getter
    private String prefix;

    public LanguageManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        loadConfig();
        loadMessages();
    }

    private void loadMessages() {

        List<Language> languageList = (List<Language>) languageConfig.get("languages", languageType);

        for(Language all :languageList) {
            File file = new File("plugins/UltPerms/languages/" + all.getUnicode() + ".json");

            if(file.exists()) {
                this.messages.put(all.getUnicode(), JsonDocument.newDocument(file));
            } else {
                if(all.getUnicode().equals("de-DE")) {
                    generateDefaultGermanMessagesConfig(file);
                } else {
                    generateDefaultMessagesConfig(file);
                }
            }

        }
    }

    private void generateDefaultMessagesConfig(File file) {

    }

    private void generateDefaultGermanMessagesConfig(File file) {
        IDocument<JsonDocument> document = new JsonDocument();

        document.append("utils.wrong_usage", "&cDu hast den Befehl falsch eingegeben!");
        document.append("utils.no_permission", "&cDu hast dafür keine Berechtigungen!");
        document.append("utils.player_not_found", "§cDieser Spieler konnte nicht gefunden werden!");

        document.append("command.rank.output", "&7Du hast den Rang %group_color%%group_name% noch für &e%group_days% Tage");


        document.append("command.ultperms.helpmap.first", "&5UltPerms &8- &7Hilfe");
        document.append("command.ultperms.helpmap.second", "&a/ultperms user [Name] &7- &7Zeige dir Informationen über einen Spieler an.");
        document.append("command.ultperms.helpmap.third", "&a/ultperms user [Name]");

        document.write(file);
    }

    public void getMessage(UUID uuid, String code, Consumer<String> translatedMessage) {
        playerManager.getPermissionPlayer(uuid, permissionPlayer -> {
            getAutoReplacement(uuid, replacements -> {
                String message = messages.get(permissionPlayer.getLanguage()).getString(code);

                for(String replace : replacements.keySet()) {
                    message = message.replaceAll(replace, replacements.get(replace));
                }

                message = ChatColor.translateAlternateColorCodes('&', message);

                translatedMessage.accept(message);
            });
        });
    }

    public void sendMessage(Player player, String code) {
        playerManager.getPermissionPlayer(player.getUniqueId(), permissionPlayer -> {
            getAutoReplacement(player.getUniqueId(), replacements -> {
                String message = messages.get(permissionPlayer.getLanguage()).getString(code);

                for(String replace : replacements.keySet()) {
                    message = message.replaceAll(replace, replacements.get(replace));
                }

                message = ChatColor.translateAlternateColorCodes('&', message);

                player.sendMessage(message);
            });
        });
    }

    public void getAutoReplacement(UUID uniqueId, Consumer<HashMap<String, String>> replacements) {
        playerManager.getPermissionPlayer(uniqueId, permissionPlayer -> {
            playerManager.getHighestPermissionGroup(uniqueId, group -> {
                playerManager.getRemainingGroupTime(uniqueId, group.getIdentifier(), remainingTime -> {

                    long days = TimeUnit.MILLISECONDS.toDays(remainingTime);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime - TimeUnit.DAYS.toMillis(days));
                    long seconds = TimeUnit.MINUTES.toSeconds(remainingTime - TimeUnit.DAYS.toMillis(days) - TimeUnit.MINUTES.toMillis(minutes));

                    replacements.accept(new ReplacementBuilder()
                            .setGroupName(group.getDisplayName())
                            .setGroupColor(group.getColor())
                            .setGroupTime(remainingTime)
                            .setGroupTimeDays(days)
                            .setGroupTimeMinutes(minutes)
                            .setGroupTimeSeconds(seconds)
                            .setGroupTabPrefix(group.getTabPrefix())
                            .setGroupChatPrefix(group.getChatPrefix())
                            .setPlayerName(permissionPlayer.getName())
                            .build());

                });
            });
        });
    }

    private void loadConfig() {

        File file = new File("plugins/UltPerms/Language.json");

        if(!file.exists()) {
            generateDefaultConfig(file);
        } else {
            this.languageConfig = JsonDocument.newDocument(file.toPath());
        }

        this.prefix = ChatColor.translateAlternateColorCodes('&', this.languageConfig.getString("prefix"));

    }

    private void generateDefaultConfig(File file) {

        IDocument document = new JsonDocument();

        document.append("prefix", "&7[&5UltPerms&5]");

        document.append("languages", Arrays.asList(new Language("en-US", "American English"), new Language("de-DE", "German")));

        document.write(file.toPath());

        this.languageConfig = document;

    }


}
