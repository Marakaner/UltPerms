package net.marakaner.ultperms.listener;

import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.document.IDocument;
import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final IDocument generalConfig = UltPerms.getInstance().getGeneralConfig();
    private final PlayerManager playerManager;
    private final LanguageManager languageManager;

    public ChatListener(PlayerManager playerManager, LanguageManager languageManager) {
        this.playerManager = playerManager;
        this.languageManager = languageManager;
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        playerManager.getHighestPermissionGroup(event.getPlayer().getUniqueId(), group -> {
            languageManager.getAutoReplacement(event.getPlayer().getUniqueId(), replacements -> {
                replacements.put("%message%", event.getMessage());

                String message = generalConfig.getString("chat_layout");

                for(String replace : replacements.keySet()) {
                    message = message.replaceAll(replace, replacements.get(replace));
                }

                message = ChatColor.translateAlternateColorCodes('&', message);

                Bukkit.broadcastMessage(message);
            });
        });
        event.setCancelled(true);
    }

}
