package net.marakaner.ultperms.commands;

import net.marakaner.ultperms.group.Group;
import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.language.LanguageManager;
import net.marakaner.ultperms.player.PermissionPlayer;
import net.marakaner.ultperms.player.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

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

            playerManager.getPermissionPlayer(args[1], permissionPlayer -> {

                if(permissionPlayer == null) {
                    languageManager.sendMessage(player, "utils.player_not_found");
                    return;
                }


                if(args.length == 2) {
                    printPlayerInfo(player, permissionPlayer);
                } else if(args.length == 3) {
                    if (args[2].equalsIgnoreCase("groups")) {
                        printPlayerGroups(player, permissionPlayer);
                    } else if (args[1].equalsIgnoreCase("permission")) {
                        printPlayerPermission(player, permissionPlayer);
                    }



                } else if(args.length == 5) {
                    if(args[3].equalsIgnoreCase("group")) {

                        Group group = groupManager.getGroup(args[4]);

                        if(group == null) {
                            languageManager.sendMessage(player, "utils.group_not_found");
                            return;
                        }

                        if(args[2].equalsIgnoreCase("add")) {

                            if(permissionPlayer.getGroups().containsValue(group.getIdentifier())) {
                                languageManager.sendMessage(player, "command.ultperms.player_have_group");
                                return;
                            }

                            playerManager.addGroup(permissionPlayer.getUniqueId(), group.getIdentifier(), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3650), finish -> {
                                if(finish) {
                                    languageManager.sendMessage(player, "command.ultperms.player_set_group");
                                }
                            });

                        } else if(args[2].equalsIgnoreCase("remove")) {

                            if(!permissionPlayer.getGroups().containsValue(group.getIdentifier())) {
                                languageManager.sendMessage(player, "command.ultperms.player_not_have_group");
                                return;
                            }

                            playerManager.removeGroup(permissionPlayer.getUniqueId(), group.getIdentifier(), finish -> {
                                if(finish) {
                                    languageManager.sendMessage(player, "command.ultperms.player_unset_group");
                                }
                            });

                        }



                    } else if(args[3].equalsIgnoreCase("permission")) {

                        String permission = args[4].toLowerCase();

                        if(args[2].equalsIgnoreCase("add")) {

                            if(permissionPlayer.hasPermission(permission)) {
                                languageManager.sendMessage(player, "command.ultperms.player_have_permission");
                                return;
                            }

                            playerManager.addPermission(permissionPlayer.getUniqueId(), permission, finish -> {
                                if(finish) languageManager.sendMessage(player, "command.ultperms.player_set_permission");
                            });

                        } else if(args[2].equalsIgnoreCase("remove")) {

                            if(!permissionPlayer.hasPermission(permission)) {
                                languageManager.sendMessage(player, "command.ultperms.player_not_have_permission");
                                return;
                            }

                            playerManager.removePermission(permissionPlayer.getUniqueId(), permission, finish -> {
                                if(finish) languageManager.sendMessage(player, "command.ultperms.player_unset_permission");
                            });

                        }

                    }
                } else if(args.length == 6) {

                    Group group = groupManager.getGroup(args[4]);

                    if(group == null) {
                        languageManager.sendMessage(player, "utils.group_not_found");
                        return;
                    }

                    if(args[2].equalsIgnoreCase("add")) {

                        int days, minutes;

                        try {
                            days = Integer.parseInt(args[5]);
                            minutes = Integer.parseInt(args[6])
                        } catch (NumberFormatException e) {
                            languageManager.sendMessage(player, "utils.wrong_number");
                            return;
                        }

                        if(permissionPlayer.getGroups().containsValue(group.getIdentifier())) {
                            languageManager.sendMessage(player, "command.ultperms.player_have_group");
                            return;
                        }

                        playerManager.addGroup(permissionPlayer.getUniqueId(), group.getIdentifier(), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days) + TimeUnit.MINUTES.toMillis(minutes), finish -> {
                            if(finish) {
                                languageManager.sendMessage(player, "command.ultperms.player_set_group");
                            }
                        });

                    }

                } else if(args.length == 7) {

                    Group group = groupManager.getGroup(args[4]);

                    if(group == null) {
                        languageManager.sendMessage(player, "utils.group_not_found");
                        return;
                    }


                    if(args[2].equalsIgnoreCase("add")) {

                        int days, minutes, seconds;

                        try {
                            days = Integer.parseInt(args[5]);
                            minutes = Integer.parseInt(args[6]);
                            seconds = Integer.parseInt(args[7])
                        } catch (NumberFormatException e) {
                            languageManager.sendMessage(player, "utils.wrong_number");
                            return;
                        }

                        if(permissionPlayer.getGroups().containsValue(group.getIdentifier())) {
                            languageManager.sendMessage(player, "command.ultperms.player_have_group");
                            return;
                        }

                        playerManager.addGroup(permissionPlayer.getUniqueId(), group.getIdentifier(), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days) + TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds), finish -> {
                            if(finish) {
                                languageManager.sendMessage(player, "command.ultperms.player_set_group");
                            }
                        });

                    }

                } else if(args.length == 8) {

                    Group group = groupManager.getGroup(args[4]);

                    if(group == null) {
                        languageManager.sendMessage(player, "utils.group_not_found");
                        return;
                    }

                    if(args[2].equalsIgnoreCase("add")) {

                        int days;

                        try {
                            days = Integer.parseInt(args[5]);
                        } catch (NumberFormatException e) {
                            languageManager.sendMessage(player, "utils.wrong_number");
                            return;
                        }

                        if(permissionPlayer.getGroups().containsKey(group.getIdentifier())) {
                            languageManager.sendMessage(player, "command.ultperms.player_have_group");
                            return;
                        }

                        playerManager.addGroup(permissionPlayer.getUniqueId(), group.getIdentifier(), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days), finish -> {
                            if(finish) {
                                languageManager.sendMessage(player, "command.ultperms.player_set_group");
                            }
                        });

                    }

                }

            });





        } else if(args[0].equalsIgnoreCase("group")) {

        } else if(args[0].equalsIgnoreCase("sign"))


        return true;
    }

    private void printPlayerPermission(Player player, PermissionPlayer permissionPlayer) {
        languageManager.getMessage(player.getUniqueId(), "command.ultperms.player_info.first", first -> {
            languageManager.getMessage(player.getUniqueId(), "command.ultperms.player_info.third", second -> {

                player.sendMessage(first);
                player.sendMessage(second);

                for(String permission : permissionPlayer.getPermissions()) {
                    player.sendMessage("&7- " + permission);
                }

            });
        });
    }

    private void printPlayerGroups(Player player, PermissionPlayer permissionPlayer) {
        languageManager.getMessage(player.getUniqueId(), "command.ultperms.player_info.first", first -> {
            languageManager.getMessage(player.getUniqueId(), "command.ultperms.player_info.second", second -> {
                player.sendMessage(first);
                player.sendMessage(second);

                for(String group : permissionPlayer.getGroups().keySet()) {
                    player.sendMessage("&7- " + group + " : " + TimeUnit.MILLISECONDS.toDays(permissionPlayer.getGroups().get(group)));
                }
            });
        });
    }

    private void printPlayerInfo(Player player, PermissionPlayer permissionPlayer) {

        languageManager.getMessage(player.getUniqueId(), "command.ultperms.player_info.first", first -> {
            languageManager.getMessage(player.getUniqueId(), "command.ultperms.player_info.second", second -> {
                languageManager.getMessage(player.getUniqueId(), "command.ultperms.player_info.third", third -> {
                    player.sendMessage(first);
                    player.sendMessage(second);

                    for(String group : permissionPlayer.getGroups().keySet()) {
                        player.sendMessage("&7- " + group + " : " + TimeUnit.MILLISECONDS.toDays(permissionPlayer.getGroups().get(group)));
                    }

                    player.sendMessage(third);
                    for(String permission : permissionPlayer.getPermissions()) {
                        player.sendMessage("&7- " + permission);
                    }
                });
            });
        });

    }
}
