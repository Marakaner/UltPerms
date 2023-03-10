package net.marakaner.ultperms.listener;

import com.google.gson.annotations.Expose;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.document.IDocument;
import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final IDocument generalConfig = UltPerms.getInstance().getGeneralConfig();

    private final PlayerManager playerManager;
    private final LanguageManager languageManager;

    public JoinListener(PlayerManager playerManager, LanguageManager languageManager) {
        this.playerManager = playerManager;
        this.languageManager = languageManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        playerManager.registerPlayer(event.getPlayer(), permissionPlayer -> {
            playerManager.getHighestPermissionGroup(event.getPlayer().getUniqueId(), group -> {
                languageManager.getAutoReplacement(event.getPlayer().getUniqueId(), replacements -> {

                    String message = generalConfig.getString("join_message");

                    for(String replace : replacements.keySet()) {
                        message = message.replaceAll(replace, replacements.get(replace));
                    }

                    message = ChatColor.translateAlternateColorCodes('&', message);

                    Bukkit.broadcastMessage(message);

                });
            });
        });
    }

}
