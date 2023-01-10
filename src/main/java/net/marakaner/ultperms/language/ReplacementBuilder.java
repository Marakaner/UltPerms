package net.marakaner.ultperms.language;

import jdk.javadoc.internal.doclets.toolkit.taglets.snippet.Replace;
import lombok.NoArgsConstructor;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.group.Group;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
public class ReplacementBuilder {

    private HashMap<String, String> replacements = new HashMap<>();

    public ReplacementBuilder setGroupReplacements(Group group) {
        setGroupChatPrefix(group.getChatPrefix());
        setGroupTabPrefix(group.getTabPrefix());
        setGroupName(group.getDisplayName());
        setGroupColor(group.getColor());
        return this;
    }

    public ReplacementBuilder setPlayerName(String playerName) {
        this.replacements.put("%player_name%", playerName);
        return this;
    }

    public ReplacementBuilder setGroupName(String groupName) {
        this.replacements.put("%group_name%", groupName);
        return this;
    }

    public ReplacementBuilder setGroupTabPrefix(String groupTabPrefix) {
        this.replacements.put("%group_tab_prefix%", groupTabPrefix);
        return this;
    }

    public ReplacementBuilder setGroupChatPrefix(String groupChatPrefix) {
        this.replacements.put("%group_chat_prefix%", groupChatPrefix);
        return this;
    }

    public ReplacementBuilder setGroupColor(ChatColor color) {
        this.replacements.put("%group_color%", "&" + color.getChar());
        return this;
    }

    public ReplacementBuilder setGroupTime(long time) {
        this.replacements.put("%group_time%", String.valueOf(time));
        return this;
    }

    public ReplacementBuilder setGroupTimeDays(String timeDays) {
        this.replacements.put("%group_time_day%", timeDays);
        return this;
    }

    public ReplacementBuilder setGroupTimeHours(String timeHours) {
        this.replacements.put("%group_time_hour%", timeHours);
        return this;
    }

    public ReplacementBuilder setGroupTimeMinutes(String timeMinutes) {
        this.replacements.put("%group_time_min%", timeMinutes);
        return this;
    }

    public ReplacementBuilder setGroupTimeSeconds(String timeSeconds) {
        this.replacements.put("%group_time_sec%", timeSeconds);
        return this;
    }

    public ReplacementBuilder setPrefix(String prefix) {
        this.replacements.put("%prefix%", prefix);
        return this;
    }

    @Override
    public String toString() {
        return replacements.get("%prefix%");
    }

    public HashMap<String, String> build() {
        return this.replacements;
    }

}
