package net.marakaner.ultperms.worker;

import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.player.PermissionPlayer;
import net.marakaner.ultperms.player.PlayerManager;
import net.marakaner.ultperms.sign.SignManager;
import net.marakaner.ultperms.sign.UltSign;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateWorker {

    private final PlayerManager playerManager;
    private final SignManager signManager;

    public UpdateWorker(PlayerManager playerManager, SignManager signManager) {
        this.playerManager = playerManager;
        this.signManager = signManager;
        startTask();
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(PermissionPlayer player : playerManager.getPermissionPlayers().values()) {
                    for(String group : player.getGroups().keySet()) {
                        if(player.getGroups().get(group) < System.currentTimeMillis()) {
                            playerManager.removeGroup(player.getUniqueId(), group, finish -> {
                            });
                        }
                    }
                }

                for(UltSign ultSign : signManager.getCachedSigns()) {
                    signManager.updateSign(ultSign);
                }

            }
        }.runTaskTimer(UltPerms.getInstance(), 0, 20 * 10);
    }

}
