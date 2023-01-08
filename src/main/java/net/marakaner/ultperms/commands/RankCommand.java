package net.marakaner.ultperms.commands;

import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.language.ReplacementBuilder;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RankCommand implements CommandExecutor {

    private final PlayerManager playerManager;
    private final LanguageManager languageManager;
    private final GroupManager groupManager;

    public RankCommand(PlayerManager playerManager, LanguageManager languageManager, GroupManager groupManager) {
        this.playerManager = playerManager;
        this.languageManager = languageManager;
        this.groupManager = groupManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(args.length == 0) {
            languageManager.sendMessage(player, "command.rank.output");
        } else {
            languageManager.sendMessage(player, "utils.wrong_usage");
        }

        return true;
    }
}
