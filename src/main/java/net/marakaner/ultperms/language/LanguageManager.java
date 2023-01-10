package net.marakaner.ultperms.language;

import com.google.common.reflect.TypeToken;
import lombok.Getter;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.document.IDocument;
import net.marakaner.ultperms.document.gson.JsonDocument;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LanguageManager {

    private final Type languageType = new TypeToken<List<Language>>() {}.getType();
    private final PlayerManager playerManager;

    private IDocument languageConfig;
    private Map<String, IDocument> messages = new HashMap<>();

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

        document.append("utils.wrong_usage", "%prefix%&cDu hast den Befehl falsch eingegeben.");
        document.append("utils.no_permission", "%prefix%&cDu hast den Befehl falsch eingegeben.");
        document.append("utils.player_not_found", "%prefix%&cDu hast den Befehl falsch eingegeben.");
        document.append("utils.group_not_found", "%prefix%&cDu hast den Befehl falsch eingegeben.");
        document.append("utils.wrong_number", "%prefix%&cDu hast den Befehl falsch eingegeben.");

        document.append("command.rank.output", "%prefix%&7Du hast den Rang %group_color%%group_name% &7für &e%group_time_day% verbleibende Tage.");
        document.append("command.rank.output_permanent", "%prefix%&7Du hast den Rang %group_color%%group_name%.");


        document.append("command.ultperms.player_info.first", "&7Spielerinfo von %group_color%%player_name%");
        document.append("command.ultperms.player_info.second", "&7Gruppen:");
        document.append("command.ultperms.player_info.third", "&7Permission:");

        document.append("command.ultperms.player_have_group", "%prefix%&cDieser Spieler hat diese Gruppe bereits.");
        document.append("command.ultperms.player_not_have_group", "%prefix%&c&cDieser Spieler hat diese Gruppe nicht.");

        document.append("command.ultperms.player_set_group", "%prefix%&aDu hast dem Spieler die Gruppe hinzugefügt.");
        document.append("command.ultperms.player_unset_group", "%prefix%&aDu hast dem Spieler die Gruppe entfernt.");

        document.append("command.ultperms.player_have_permission", "%prefix%&cDieser Spieler hat bereits diese Berechtigung.");
        document.append("command.ultperms.player_not_have_permission", "%prefix%&cDieser Spieler hat dieser Berechtigung nicht.");

        document.append("command.ultperms.player_set_permission", "%prefix%&aDu hast dem Spieler diese Berechtigung hinzugefügt.");
        document.append("command.ultperms.player_unset_permission", "%prefix%&aDu hast dem Spieler diese Berechtigung entfernt.");

        document.append("command.ultperms.group_have_permission", "%prefix%&cDieser Gruppe hat diese Berechtigung bereits.");
        document.append("command.ultperms.group_not_have_permission", "%prefix%&cDiese Gruppe hat dieser Berechtigung nicht.");

        document.append("command.ultperms.group_set_permission", "%prefix%&aDu hast der Gruppe die Berechtigung hinzugefügt.");
        document.append("command.ultperms.group_unset_permission", "%prefix%&aDu hast der Gruppe die Berechtigung entfernt.");

        document.append("command.ultperms.group_create", "%prefix%&aDu hast eine neue Gruppe erstellt. Bitte editiere nun die Informationen in der Config.");
        document.append("command.ultperms.group_remove", "%prefix%&aDu hast diese Gruppe gelöscht!");

        document.append("command.ultperms.group_exist", "%prefix%&cDiese Gruppe existiert bereits.");
        document.append("command.ultperms.group_not_exist", "%prefix%&cDiese Gruppe existiert nicht.");

        document.append("command.ultsign.no_sign", "&cDu musst auf ein Schild gucken!");
        document.append("command.ultsign.sign_exist", "&cDieses Schild wurde bereits zugeordnet!");
        document.append("command.ultsign.sign_not_exist", "&cDieser Schild existier nicht.");
        document.append("command.ultsign.sign_created", "&aDu hast erfolgreich das Schild erstellt.");
        document.append("command.ultsign.sign_removed", "&aDu hast erfolgreich das Schild entfernt.");

        messages.put("de-DE", document);

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

                long remainingTime = permissionPlayer.getGroups().get(group.getIdentifier());

                ReplacementBuilder replacementBuilder = new ReplacementBuilder()
                        .setGroupName(group.getDisplayName())
                        .setGroupColor(group.getColor())
                        .setGroupTime(remainingTime)
                        .setGroupTabPrefix(group.getTabPrefix())
                        .setGroupChatPrefix(group.getChatPrefix())
                        .setPlayerName(permissionPlayer.getName())
                        .setPrefix(UltPerms.getInstance().getPrefix());

                if(remainingTime == -1) {
                    replacementBuilder.setGroupTimeDays("Infinite")
                            .setGroupTimeHours("Infinite")
                            .setGroupTimeMinutes("Infinite")
                            .setGroupTimeSeconds("Infinite");
                } else {

                    remainingTime = remainingTime - System.currentTimeMillis();

                    long days = TimeUnit.MILLISECONDS.toDays(remainingTime);
                    long hours = TimeUnit.MILLISECONDS.toHours(remainingTime - TimeUnit.DAYS.toMillis(days));
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours));
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTime - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));

                    replacementBuilder.setGroupTimeDays(String.valueOf(days))
                            .setGroupTimeHours(String.valueOf(hours))
                            .setGroupTimeMinutes(String.valueOf(minutes))
                            .setGroupTimeSeconds(String.valueOf(seconds));
                }

                replacements.accept(replacementBuilder.build());

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

    }

    private void generateDefaultConfig(File file) {

        IDocument document = new JsonDocument();

        document.append("languages", Arrays.asList(new Language("en-US", "American English"), new Language("de-DE", "German")));

        document.write(file.toPath());

        this.languageConfig = document;

    }


}
