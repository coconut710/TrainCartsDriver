package com.sketchtown.bukkit.tcdriver;

import org.bukkit.permissions.PermissionDefault;

import com.bergerkiller.bukkit.common.permissions.PermissionEnum;

public class TCDriverPermissions extends PermissionEnum {
    public static final TCDriverPermissions DRIVE = new TCDriverPermissions("train.drive", PermissionDefault.OP, "The player can drive minecart");
    
    private TCDriverPermissions(final String node, final PermissionDefault permdefault, final String desc) {
        super(node, permdefault, desc);
    }

    private TCDriverPermissions(final String node, final PermissionDefault permdefault, final String desc, int argCount) {
        super(node, permdefault, desc, argCount);
    }
}