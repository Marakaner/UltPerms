package net.marakaner.ultperms.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

@AllArgsConstructor
@Setter(AccessLevel.PROTECTED)
@Getter
public class PermissionPlayer {

    private UUID uniqueId;
    private String name;
    private List<String> permissions;
    private Map<String, Long> groups;
    private String language = "en-US";
    private PermissionAttachment attachment;

    public PermissionPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<String> getPermissions() {
        return new ArrayList<>(permissions);
    }

    public Map<String, Long> getGroups() {
        return new HashMap<>(groups);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getLanguage() {
        return language;
    }
}
