package net.marakaner.ultperms.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.List;

@AllArgsConstructor
@Getter
public class Group {

    private String identifier;
    private String display;
    private String tabPrefix;
    private String chatPrefix;
    private ChatColor displayColor;
    private List<String> permission;
    private int priority;
}
