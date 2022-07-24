package com.sketchtown.bukkit.tcdriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.sketchtown.bukkit.tcdriver.commands.TCDriverCommands;

public class TCDriver extends PluginBase {
    private final Map<Player, Driver> driversList = new HashMap<Player, Driver>();
    private final List<DriveableTrain> trainList = new ArrayList<DriveableTrain>();
    private Task updateDriveableTrainTask;
	
	private TCDriverCommands commands;
	private final TCDriverListener listener = new TCDriverListener(this);

    @Override
    public void onLoad() {
        this.getLogger().log(Level.INFO, "Loading TrainCartsDriver");
        FileConfiguration config = new FileConfiguration(this, "");
        config.load();
        config.save();
    }

	@Override
	public int getMinimumLibVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void enable() {
        this.listener.enable();
		this.commands = new TCDriverCommands();
        this.commands.enable(this);
		
        this.getLogger().log(Level.INFO, "Enabling TrainCartsDriver");
	}

	@Override
	public void disable() {
        this.driversList.clear();
		this.listener.disable();
        Task.stop(this.updateDriveableTrainTask);
        
        this.getLogger().log(Level.INFO, "Disabling TrainCartsDriver");
	}

	@Override
	public boolean command(CommandSender sender, String command, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
