package net.marakaner.ultperms.listener;

import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.document.IDocument;
import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener {

    private final IDocument generalConfig = UltPerms.getInstance().getGeneralConfig();

    private final PlayerManager playerManager;
    private final LanguageManager languageManager;

    public QuitListener(PlayerManager playerManager, LanguageManager languageManager) {
        this.playerManager = playerManager;
        this.languageManager = languageManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        playerManager.getPermissionPlayer(event.getPlayer().getUniqueId(), permissionPlayer -> {
            playerManager.getHighestPermissionGroup(event.getPlayer().getUniqueId(), group -> {
                languageManager.getAutoReplacement(event.getPlayer().getUniqueId(), replacements -> {

                    String message = generalConfig.getString("join_message");

                    for(String replace : replacements.keySet()) {
                        message = message.replaceAll(replace, replacements.get(replace));
                    }

                    message = ChatColor.translateAlternateColorCodes('&', message);

                    Bukkit.broadcastMessage(message);

                    playerManager.unregisterPlayer(event.getPlayer());

                });
            });
        });
    }

}
