package net.marakaner.ultperms.player;

import net.marakaner.ultperms.group.GroupManager;
import net.marakaner.ultperms.permission.PermissionManager;

public class PlayerManager {

    private final GroupManager groupManager;
    private final PermissionManager permissionManager;

    public PlayerManager(GroupManager groupManager, PermissionManager permissionManager) {
        this.groupManager = groupManager;
        this.permissionManager = permissionManager;
    }


}
