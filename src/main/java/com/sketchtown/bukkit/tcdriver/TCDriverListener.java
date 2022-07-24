package com.sketchtown.bukkit.tcdriver;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.GroupLinkEvent;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionBlocker;
import com.bergerkiller.bukkit.tc.signactions.SignActionLauncher;
import com.bergerkiller.bukkit.tc.signactions.SignActionStation;
import com.bergerkiller.bukkit.tc.signactions.SignActionWait;

public class TCDriverListener implements Listener {
	private final TCDriver plugin;
	
	public TCDriverListener(TCDriver plugin) {
		this.plugin = plugin;
	}
	
	public void enable() {
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

	public void disable() {
    }
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onGroupLink(GroupLinkEvent event) {
		MinecartGroup g1 = event.getGroup1();
		MinecartGroup g2 = event.getGroup2();
		boolean d1 = this.plugin.trainIsDriving(g1);
		boolean d2 = this.plugin.trainIsDriving(g2);
		if (d1 && !d2) {
        	plugin.addDriveableTrain(g2);
        	plugin.getDriveableTrain(g2).setProperties(plugin.getDriveableTrain(g1));
        } else if (!d1 && d2) {
        	plugin.addDriveableTrain(g1);
        	plugin.getDriveableTrain(g1).setProperties(plugin.getDriveableTrain(g2));
        }
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.isDriver(player)) {
			plugin.removeDriver(player);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignActionEvent(SignActionEvent event) {
		MinecartGroup group = event.getGroup();
		DriveableTrain driveable;
		if (!this.plugin.trainIsDriving(group)) {
			return;
		} else {
			driveable = plugin.getDriveableTrain(group);
			switch (driveable.getControlType()) {
			case SEMI:
			case AUTO:
				return;
			case MANU:
			default:
				break;
			}
		}
		SignAction action = SignAction.getSignAction(event);
		if (action instanceof SignActionStation) {
			driveable.setTargetStation(event.getSign().getLocation());
			event.setCancelled(true);
		} else if (action instanceof SignActionWait) {
			event.setCancelled(true);
		} else if (action instanceof SignActionLauncher) {
			event.setCancelled(true);
		} else if (action instanceof SignActionBlocker) {
			event.setCancelled(true);
		}
	}
}