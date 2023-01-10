package net.marakaner.ultperms.commands;

import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.player.PlayerManager;
import net.marakaner.ultperms.sign.SignManager;
import net.marakaner.ultperms.sign.UltSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UltSignCommand implements CommandExecutor {

    private final PlayerManager playerManager;
    private final LanguageManager languageManager;
    private final SignManager signManager;

    public UltSignCommand(PlayerManager playerManager, LanguageManager languageManager, SignManager signManager) {
        this.playerManager = playerManager;
        this.languageManager = languageManager;
        this.signManager = signManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;


        if (args.length == 0) {

        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {

                return false;
            }


            Block block = player.getTargetBlockExact(10);

            if (block == null || !(block.getState() instanceof Sign)) {
                languageManager.sendMessage(player, "command.ultsign.no_sign");
                return false;
            }

            UltSign sign = signManager.getSignAtLocation(block.getLocation());

            if (args[0].equalsIgnoreCase("remove")) {

                if(sign == null) {
                    languageManager.sendMessage(player, "command.ultsign.sign_not_exist");
                    return false;
                }

                signManager.removeSign(sign);
                languageManager.sendMessage(player, "command.ultsign.sign_removed");

            }
        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("create")) {

                Block block = player.getTargetBlockExact(10);

                if (block == null || !(block.getState() instanceof Sign)) {
                    languageManager.sendMessage(player, "command.ultsign.no_sign");
                    return false;
                }

                UltSign sign = signManager.getSignAtLocation(block.getLocation());

                if (sign != null) {
                    languageManager.sendMessage(player, "command.ultsign.sign_exist");
                    return false;
                }

                playerManager.getPermissionPlayer(args[1], permissionPlayer -> {

                    if(permissionPlayer == null) {
                        languageManager.sendMessage(player, "utils.player_not_found");
                        return;
                    }

                    signManager.createSign(block.getLocation(), permissionPlayer.getUniqueId());
                    languageManager.sendMessage(player, "command.ultsign.sign_created");

                });

            }
        }

        return true;
    }
}
