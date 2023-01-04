package net.marakaner.ultperms.player;

import lombok.Getter;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.group.Group;
import net.marakaner.ultperms.group.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PermissionPlayer {

    private static final GroupManager groupManager = UltPerms.getInstance().getGroupManager();

    private UUID uniqueId;
    private List<String> permission = new ArrayList<>();
    private List<String> groups = new ArrayList<>();

    private PermissionAttachment attachment;

    private Group highestPermissionGroup;

    public PermissionPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.attachment = Bukkit.getPlayer(uniqueId).addAttachment(UltPerms.getInstance());
        setPermission();
    }

    private void setPermission() {
        for(String perms : permission) {
            attachment.setPermission(perms, true);
        }

        for(String group : groups) {
            Group currentGroup = groupManager.getGroupByIdentifier(group);

            for(String perms : currentGroup.getPermission()) {
                attachment.setPermission(perms, true);
            }
        }
    }

    public Group getHighestGroup() {
        Group highestGroup = groupManager.getGroupByIdentifier(groups.get(0));

        if(groups.size() > 1) {
            for(String group : groups) {

                Group currentGroup = groupManager.getGroupByIdentifier(group);

                if(highestGroup.getPriority() < currentGroup.getPriority()) {
                    highestGroup = currentGroup;
                }
            }
        }

        return highestGroup;
    }


}
