package net.marakaner.ultperms.listener;

import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerManager playerManager = UltPerms.getInstance().getPlayerManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        playerManager.registerPlayer(event.getPlayer().getUniqueId(), permissionPlayer -> {
            Bukkit.broadcastMessage("§eThe player" + permissionPlayer.getHighestPermissionGroup().getDisplayColor() + event.getPlayer().getName() + " §ejoined.");
        });

    }

}
