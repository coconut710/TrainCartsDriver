package com.sketchtown.bukkit.tcdriver.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.sketchtown.bukkit.tcdriver.DriveableTrain;
import com.sketchtown.bukkit.tcdriver.EnumControlType;
import com.sketchtown.bukkit.tcdriver.TCDriver;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;

@CommandMethod("tcdriver|tcd")
public class PlayerCommands {
	@CommandRequiresDrivePermission
    @CommandMethod("drive")
    @CommandDescription("Make train driveable")
    private void commandDrive(
            final Player player,
            final TCDriver plugin
    ) {
		for (MinecartGroup g : MinecartGroupStore.getGroups()) {
            for (MinecartMember<?> m : g) {
            	if (m.getEntity().getPlayerPassengers().contains(player)) {
            		DriveableTrain driveable = plugin.addDriveableTrain(g);
            		plugin.addDriver(player).syncTrainAndDriver(driveable, EnumControlType.MANU);
            		player.sendMessage(ChatColor.GREEN + "Hello, World!");
            		return;
            	}
            }
        }
		player.sendMessage(ChatColor.YELLOW + "the train is a lie");
    }
}
