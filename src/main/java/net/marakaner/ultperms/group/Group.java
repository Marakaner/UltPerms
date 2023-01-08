package net.marakaner.ultperms.group;

import com.google.gson.annotations.Expose;
import lombok.*;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Setter(AccessLevel.PROTECTED)
public class Group {
    @Expose
    private String identifier;
    @Expose
    private String displayName;
    @Expose
    private ChatColor color;
    @Expose
    private String tabPrefix;
    @Expose
    private String chatPrefix;
    @Expose
    private boolean defaultGroup;
    @Expose
    private int tabPriority;
    @Expose
    private int priority;

    private List<String> permission;

    public List<String> getPermission() {
        return new ArrayList<>(permission);
    }
}
