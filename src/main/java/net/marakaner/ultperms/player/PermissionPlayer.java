package net.marakaner.ultperms.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.group.GroupManager;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

@AllArgsConstructor
@Setter(AccessLevel.PROTECTED)
@Getter
public class PermissionPlayer {

    private final GroupManager groupManager = UltPerms.getInstance().getGroupManager();

    private UUID uniqueId;
    private String name;
    private List<String> permissions;
    private Map<String, Long> groups = new HashMap<>();
    private String language = "de-DE";
    private PermissionAttachment attachment;

    public PermissionPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<String> getPermissions() {
        return new ArrayList<>(permissions);
    }

    public Map<String, Long> getGroups() {
        Map<String, Long> tempGroups = new HashMap<>(groups);
        if(groups.isEmpty()) {
            tempGroups.put(groupManager.getDefaultGroup().getIdentifier(), (long) -1);
        }
        return tempGroups;
    }

    protected Map<String, Long> getDirectGroups() {
        return this.groups;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getLanguage() {
        return language;
    }

    public boolean hasPermission(String permission) {

        for(String perm : this.permissions) {
            if(perm.equalsIgnoreCase(permission)) return true;
        }

        return false;
    }

}
