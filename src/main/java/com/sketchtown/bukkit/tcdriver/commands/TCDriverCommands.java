package com.sketchtown.bukkit.tcdriver.commands;

import java.util.Collections;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.sketchtown.bukkit.tcdriver.TCDriver;

public class TCDriverCommands {
	private final CloudSimpleHandler cloud = new CloudSimpleHandler();

    public CloudSimpleHandler getHandler() {
        return cloud;
    }

    public void enable(TCDriver plugin) {
        cloud.enable(plugin);
        
        cloud.getParser().registerBuilderModifier(CommandRequiresDrivePermission.class,
                (perm, builder) -> builder.permission(plugin::hasUsePermission));
        
        // All commands that require a Player
        cloud.annotations(new PlayerCommands());

        // Help menu
        cloud.helpCommand(Collections.singletonList("tcdriver"), "Shows information about all of TC-Coasters commands");
    }
}