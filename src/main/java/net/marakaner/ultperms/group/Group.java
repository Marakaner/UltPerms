package net.marakaner.ultperms.group;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import lombok.*;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
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

    public Group(String name) {
        this.identifier = name.toLowerCase();
        this.displayName = name;
        this.color = ChatColor.GRAY;
        this.tabPrefix = "&7" + name + " | ";
        this.chatPrefix = "&7" + name + " | ";
        this.defaultGroup = false;
        this.tabPriority = 99;
        this.priority = 0;
        this.permission = Lists.newArrayList();
    }

    public boolean hasPermission(String permission) {
        for(String perm : this.permission) {
            if(perm.equalsIgnoreCase(permission)) {
                return true;
            }
        }
        return false;
    }
}
