package com.sketchtown.bukkit.tcdriver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TCHangRail extends PluginBase {
    private final List<Driver> driversList = new ArrayList<Driver>();
    private final List<DriveableTrain> trainList = new ArrayList<DriveableTrain>();
	
	private TCCoastersCommands commands;
	private final TCDriverListener listener = new TCDriverListener(this);
    private final TCDriverInteractionListener interactionListener = new TCDriverInteractionListener(this);

    @Override
    public void onLoad() {
        this.getLogger().log(Level.INFO, "Loading TrainCartsDriver");
        FileConfiguration config = new FileConfiguration(this);
        config.load();
        config.save();
    }

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Enabling TrainCartsDriver");
        this.listener.enable();
		this.interactionListener.enable();
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "Disabling TrainCartsDriver");
        this.driversList.clear();
    }
}
