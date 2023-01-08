package net.marakaner.ultperms.commands;

import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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



        if(args.length == 0) {

        } else if(args.length == 1) {

        } else if(args.length == 2) {

        } else if(args.length == 3) {

        } else if(args.length == 4) {

        } else if(args.length == 5) {

        }


        return true;
    }
}
