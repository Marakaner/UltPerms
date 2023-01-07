package net.marakaner.ultperms.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.group.Group;
import net.marakaner.ultperms.group.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

@Getter
public class PermissionPlayer {

    private static final GroupManager groupManager = UltPerms.getInstance().getGroupManager();

    private UUID uniqueId;
    private List<String> permission = new ArrayList<>();
    private Map<String, Long> groups = new HashMap<>();

    @Setter
    private String language;

    private PermissionAttachment attachment;

    private Group highestPermissionGroup;

    public PermissionPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.attachment = Bukkit.getPlayer(uniqueId).addAttachment(UltPerms.getInstance());
        identifyHighestPermissionGroup();
    }

    public void setGroups(Map<String, Long> groups) {
        this.groups = groups;

        for(String perms : permission) {
            attachment.setPermission(perms, true);
        }

        for(String group : groups.keySet()) {
            Group currentGroup = groupManager.getGroupByIdentifier(group);

            for(String perms : currentGroup.getPermission()) {
                attachment.setPermission(perms, true);
            }
        }
    }

    public void addPermission(List<String> permission) {
        permission.forEach(perm -> attachment.setPermission(perm, true));
    }
    public void addPermission(String permission) {
        attachment.setPermission(permission, true);
    }

    public void removePermission(String permission) {
        attachment.unsetPermission(permission);
    }

    public void identifyHighestPermissionGroup() {
        Group highestGroup = groupManager.getGroupByIdentifier(groups.get(0));

        if(groups.size() > 1) {
            for(String group : groups) {

                Group currentGroup = groupManager.getGroupByIdentifier(group);

                if(highestGroup.getPriority() < currentGroup.getPriority()) {
                    highestGroup = currentGroup;
                }
            }
        }

        this.highestPermissionGroup = highestGroup;
    }


}
