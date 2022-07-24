package com.sketchtown.bukkit.tcdriver.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.sketchtown.bukkit.tcdriver.TCDriver;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;

@CommandMethod("tcdriver|tcd")
public class PlayerCommands {
	@CommandTargetTrain
	@CommandRequiresDrivePermission
    @CommandMethod("drive")
    @CommandDescription("Make train driveable")
    public void commandGiveEditorMap(
            final Player player,
            final TrainProperties properties,
            final TCDriver plugin
    ) {
		MinecartGroup group = properties.getHolder();
		if (group == null) {
			player.sendMessage(ChatColor.YELLOW + "the train is a lie");
		} else {
	        plugin.addDriveableTrain(group).setDriver(player);
		}
    }
}
