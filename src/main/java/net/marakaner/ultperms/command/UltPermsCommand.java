package net.marakaner.ultperms.command;

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
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (args.length == 0) {

            return true;
        }

        if (args[0].equalsIgnoreCase("user")) {

            playerManager.getPermissionPlayer(args[1], permissionPlayer -> {

                if (permissionPlayer == null) {
                    languageManager.sendMessage(player, "utils.player_not_found");
                    return;
                }


                if (args.length == 2) {
                    printPlayerInfo(player, permissionPlayer);
                } else if (args.length == 3) {
                    if (args[2].equalsIgnoreCase("groups")) {
                        printPlayerGroups(player, permissionPlayer);
                    } else if (args[2].equalsIgnoreCase("permission")) {
                        printPlayerPermission(player, permissionPlayer);
                    } else {
                        languageManager.sendMessage(player, "utils.wrong_usage");
                    }

                } else if (args.length == 5) {
                    if (args[3].equalsIgnoreCase("group")) {

                        Group group = groupManager.getGroup(args[4]);

                        if (group == null) {
                            languageManager.sendMessage(player, "utils.group_not_found");
                            return;
                        }

                        if (args[2].equalsIgnoreCase("add")) {

                            if (permissionPlayer.getGroups().containsKey(group.getIdentifier())) {
                                languageManager.sendMessage(player, "command.ultperms.player_have_group");
                                return;
                            }

                            playerManager.addGroup(permissionPlayer.getUniqueId(), group.getIdentifier(), (long) -1, finish -> {
                                if (finish) {
                                    languageManager.sendMessage(player, "command.ultperms.player_set_group");
                                }
                            });

                        } else if (args[2].equalsIgnoreCase("remove")) {

                            if (!permissionPlayer.getGroups().containsKey(group.getIdentifier())) {
                                languageManager.sendMessage(player, "command.ultperms.player_not_have_group");
                                return;
                            }

                            playerManager.removeGroup(permissionPlayer.getUniqueId(), group.getIdentifier(), finish -> {
                                if (finish) {
                                    languageManager.sendMessage(player, "command.ultperms.player_unset_group");
                                }
                            });

                        } else {
                            languageManager.sendMessage(player, "utils.wrong_usage");
                        }


                    } else if (args[3].equalsIgnoreCase("permission")) {

                        String permission = args[4].toLowerCase();

                        if (args[2].equalsIgnoreCase("add")) {

                            if (permissionPlayer.hasPermission(permission)) {
                                languageManager.sendMessage(player, "command.ultperms.player_have_permission");
                                return;
                            }

                            playerManager.addPermission(permissionPlayer.getUniqueId(), permission, finish -> {
                                if (finish)
                                    languageManager.sendMessage(player, "command.ultperms.player_set_permission");
                            });

                        } else if (args[2].equalsIgnoreCase("remove")) {

                            if (!permissionPlayer.hasPermission(permission)) {
                                languageManager.sendMessage(player, "command.ultperms.player_not_have_permission");
                                return;
                            }

                            playerManager.removePermission(permissionPlayer.getUniqueId(), permission, finish -> {
                                if (finish)
                                    languageManager.sendMessage(player, "command.ultperms.player_unset_permission");
                            });

                        } else {
                            languageManager.sendMessage(player, "utils.wrong_usage");
                        }

                    }
                } else if (args.length >= 6) {

                    Group group = groupManager.getGroup(args[4]);

                    if (group == null) {
                        languageManager.sendMessage(player, "utils.group_not_found");
                        return;
                    }

                    if (args[2].equalsIgnoreCase("add")) {

                        long timestamp = System.currentTimeMillis();

                        for(int i = 5; i < args.length; i++) {

                            TimeUnit unit;
                            int time;

                            if(args[i].contains("d")) {
                                unit = TimeUnit.DAYS;
                            } else if(args[i].contains("h")) {
                                unit = TimeUnit.HOURS;
                            } else if(args[i].contains("m")) {
                                unit = TimeUnit.MINUTES;
                            } else if (args[i].contains("s")) {
                                unit = TimeUnit.SECONDS;
                            } else {
                                languageManager.sendMessage(player, "utils.wrong_usage");
                                return;
                            }

                            try {
                                args[i] = args[i].substring(0, args[i].length()-1);
                                time = Integer.parseInt(args[i]);
                            } catch (NumberFormatException e) {
                                languageManager.sendMessage(player, "utils.wrong_number");
                                return;
                            }

                            timestamp += unit.toMillis(time);

                        }

                        if (permissionPlayer.getGroups().containsKey(group.getIdentifier())) {
                            languageManager.sendMessage(player, "command.ultperms.player_have_group");
                            return;
                        }

                        playerManager.addGroup(permissionPlayer.getUniqueId(), group.getIdentifier(), timestamp, finish -> {
                            if (finish) {
                                languageManager.sendMessage(player, "command.ultperms.player_set_group");
                            }
                        });

                    } else {
                        languageManager.sendMessage(player, "utils.wrong_usage");
                    }
                } else {
                    languageManager.sendMessage(player, "utils.wrong_usage");
                }

            });


        }
        else if (args[0].equalsIgnoreCase("group")) {

            if (args.length == 5) {

                Group group = groupManager.getGroup(args[1]);

                if (group == null) {
                    languageManager.sendMessage(player, "utils.group_not_found");
                    return false;
                }

                if (!args[4].equalsIgnoreCase("permission")) {
                    languageManager.sendMessage(player, "utils.wrong_usage");
                    return false;
                }


                String permission = args[4].toLowerCase();

                if (args[2].equalsIgnoreCase("add")) {

                    if (group.hasPermission(permission)) {
                        languageManager.sendMessage(player, "command.ultperms.group_have_permission");
                        return false;
                    }

                    groupManager.addPermission(group.getIdentifier(), permission);
                    playerManager.groupUpdated(group);
                    languageManager.sendMessage(player, "command.ultperms.group_set_permission");

                } else if (args[2].equalsIgnoreCase("remove")) {
                    if (!group.hasPermission(permission)) {
                        languageManager.sendMessage(player, "command.ultperms.group_not_have_permission");
                        return false;
                    }

                    groupManager.removePermission(group.getIdentifier(), permission);
                    playerManager.groupUpdated(group);
                    languageManager.sendMessage(player, "command.ultperms.group_unset_permission");
                } else {
                    languageManager.sendMessage(player, "utils.wrong_usage");
                }
            } else if (args.length == 3) {

                String group = args[2];

                if(args[1].equalsIgnoreCase("create")) {

                    if(groupManager.getGroup(group) != null) {
                        languageManager.sendMessage(player, "command.ultperms.group_exist");
                        return false;
                    }

                    Group createdGroup = new Group(group);
                    groupManager.createGroup(createdGroup);
                    languageManager.sendMessage(player, "command.ultperms.group_create");
                } else if(args[1].equalsIgnoreCase("remove")) {

                    Group removeGroup = groupManager.getGroup(group);

                    if(removeGroup == null) {
                        languageManager.sendMessage(player, "command.ultperms.group_not_exist");
                        return false;
                    }

                    groupManager.deleteGroup(removeGroup.getIdentifier());
                    playerManager.groupDeleted(removeGroup);
                    languageManager.sendMessage(player, "command.ultperms.group_remove");
                } else {
                    languageManager.sendMessage(player, "utils.wrong_usage");
                }
            } else {
                languageManager.sendMessage(player, "utils.wrong_usage");
            }

        } else {
            languageManager.sendMessage(player, "utils.wrong_usage");
        }

        return false;
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
                        long remainingTime = permissionPlayer.getGroups().get(group);
                        Group currentGroup = groupManager.getGroup(group);

                        if(remainingTime == -1) {
                            player.sendMessage("§7- " + currentGroup.getColor() + currentGroup.getDisplayName() + " §7: §ePermanent");
                        } else {
                            player.sendMessage("§7- §e" + group + " §7: §e" + TimeUnit.MILLISECONDS.toDays(permissionPlayer.getGroups().get(group)));
                        }
                    }

                    player.sendMessage(third);
                    for(String permission : permissionPlayer.getPermissions()) {
                        player.sendMessage("§7- §e" + permission);
                    }
                });
            });
        });

    }
}
