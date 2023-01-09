package net.marakaner.ultperms.commands;

import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.player.PermissionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
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

        Block block = player.getTargetBlockExact(5);
        BlockState blockState = block.getState();

        if(blockState instanceof Sign) {
            Sign sign = (Sign) blockState;
            UltPerms.getInstance().getSignManager().createSign(sign, player.getUniqueId());
        }

        return true;
    }
}
