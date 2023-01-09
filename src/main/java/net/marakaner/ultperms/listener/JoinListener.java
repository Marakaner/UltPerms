package net.marakaner.ultperms.listener;

import com.google.gson.annotations.Expose;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final PlayerManager playerManager;

    public JoinListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        playerManager.registerPlayer(event.getPlayer(), permissionPlayer -> {
            playerManager.getHighestPermissionGroup(event.getPlayer().getUniqueId(), group -> {
                Bukkit.broadcastMessage("§eThe player " + group.getColor() + event.getPlayer().getName() + " §ejoined!");
            });
        });
    }

}
