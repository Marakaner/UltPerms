package net.marakaner.ultperms.commands;

import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.player.PermissionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        if(args.length == 0) {

            UltPerms.getInstance().getPlayerManager().addGroup(player.getUniqueId(), "admin", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(300), aBoolean -> {
                Bukkit.broadcastMessage("FINISHED");
            });
        } else if(args.length == 1) {

            PermissionPlayer permissionPlayer = UltPerms.getInstance().getPlayerManager().getCachedPlayer(player.getUniqueId());

            for(String all : permissionPlayer.getAttachment().getPermissions().keySet()) {
                player.sendMessage(all);
            }

        }

        return true;
    }
}
