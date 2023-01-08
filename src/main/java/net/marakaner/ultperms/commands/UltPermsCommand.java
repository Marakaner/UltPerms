package net.marakaner.ultperms.commands;

import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.player.PermissionPlayer;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UltPermsCommand implements CommandExecutor {

    private final PlayerManager playerManager;
    private final GroupManager groupManager;
    private final LanguageManager languageManager;

    public UltPermsCommand(PlayerManager playerManager, GroupManager groupManager, LanguageManager languageManager) {
        this.playerManager = playerManager;
        this.groupManager = groupManager;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(args.length == 0) {

            return true;
        }

        if(args[0].equalsIgnoreCase("user")) {
            if(args.length == 1) {
                playerManager.getPermissionPlayer(args[1], permissionPlayer -> {
                    if(permissionPlayer == null) {
                        languageManager.sendMessage(player, "utils.player_not_found");
                    }

                    printPlayerInfo(player, permissionPlayer);
                });
            } else if(args.length == 2) {
                if(args[1].equalsIgnoreCase("groups")) {

                } else if(args[1].equalsIgnoreCase("permission")) {

                }
            }
        } else if(args[0].equalsIgnoreCase("group")) {

        } else if(args[0].equalsIgnoreCase("sign"))


        return true;
    }

    private void printPlayerInfo(Player player, PermissionPlayer permissionPlayer) {
    }
}
