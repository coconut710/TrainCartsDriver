package com.sketchtown.bukkit.tcdriver.commands;

import java.util.Collections;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.sketchtown.bukkit.tcdriver.TCDriver;
import com.sketchtown.bukkit.tcdriver.commands.annotations.CommandRequiresTCCPermission;

public class TCDriverCommands {
	private final CloudSimpleHandler cloud = new CloudSimpleHandler();

    public CloudSimpleHandler getHandler() {
        return cloud;
    }

    public void enable(TCDriver plugin) {
        cloud.enable(plugin);
        
        // All commands that require a Player
        cloud.annotations(new PlayerCommands());

        // Help menu
        cloud.helpCommand(Collections.singletonList("tcdriver"), "Shows information about all of TC-Coasters commands");
    }
}