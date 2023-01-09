package net.marakaner.ultperms.listener;

import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {


    private final PlayerManager playerManager;

    public ChatListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        playerManager.getHighestPermissionGroup(event.getPlayer().getUniqueId(), group -> {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', group.getChatPrefix()) + event.getPlayer().getName() + "§7: " + event.getMessage());
        });
        event.setCancelled(true);
    }

}
