package com.sketchtown.bukkit.tcdriver;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

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
}