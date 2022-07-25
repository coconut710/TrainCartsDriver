package com.sketchtown.bukkit.tcdriver.commands;

import java.util.Collections;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.tc.commands.Commands;
import com.sketchtown.bukkit.tcdriver.TCDriver;

public class TCDriverCommands extends Commands {
	private final CloudSimpleHandler cloud = new CloudSimpleHandler();

    public CloudSimpleHandler getHandler() {
        return cloud;
    }

    public void enable(TCDriver plugin) {
    	cloud.enable(plugin);

        // Override syntax formatter to hide excess flags for targeting a train or cart

        // Target a cart or train using added flags at the end of the command

        // Handle train not found exception
        // All commands that require a Player
        cloud.annotations(new PlayerCommands());

        // Help menu
        cloud.helpCommand(Collections.singletonList("tcdriver"), "Shows information about all of TCDriver commands");
    }
}