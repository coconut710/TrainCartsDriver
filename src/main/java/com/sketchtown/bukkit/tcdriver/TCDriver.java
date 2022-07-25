package com.sketchtown.bukkit.tcdriver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.sketchtown.bukkit.tcdriver.commands.TCDriverCommands;

public class TCDriver extends PluginBase {
    private Map<Player, Driver> driversList = new HashMap<Player, Driver>();
    private Map<MinecartGroup, DriveableTrain> trainList = new HashMap<MinecartGroup, DriveableTrain>();
    private Task updateDriveableTrainTask;
	
	private TCDriverCommands commands;
	private final TCDriverListener listener = new TCDriverListener(this);

    @Override
    public void onLoad() {
        FileConfiguration config = new FileConfiguration(this, "driveabletrain");
        config.load();
        config.save();
    }

	@Override
	public int getMinimumLibVersion() {
		return Common.VERSION;
	}

	@Override
	public void enable() {
        this.listener.enable();
		this.commands = new TCDriverCommands();
        this.commands.enable(this);
        this.updateDriveableTrainTask = (new UpdateDriveableTrainTask()).start(1, 1);
	}

	@Override
	public void disable() {
        synchronized (TCDriver.this) {
	        Iterator<DriveableTrain> iter = trainList.values().iterator();
	        while (iter.hasNext()) {
	        	DriveableTrain driveable = iter.next();
	        	driveable.clearBossBars();
	        }
        }
		
        this.driversList.clear();
        this.trainList.clear();
		this.listener.disable();
        Task.stop(this.updateDriveableTrainTask);
	}

	@Override
	public boolean command(CommandSender sender, String command, String[] args) {
		return false;
	}

	public boolean isDriveable(MinecartGroup group) {
		return trainList.containsKey(group);
	}

	public boolean isDriveable(MinecartMember<?> member) {
		return trainList.containsKey(member.getGroup());
	}

	public DriveableTrain getDriveable(MinecartGroup group) {
		return trainList.get(group);
	}

	public DriveableTrain getDriveable(MinecartMember<?> member) {
		return trainList.get(member.getGroup());
	}
	
	private class UpdateDriveableTrainTask extends Task {
        public UpdateDriveableTrainTask() {
            super(TCDriver.this);
        }
        public int tick = 0;
        @Override
        public void run() {
        	tick++;
        	synchronized (TCDriver.this) {
                Iterator<DriveableTrain> iter = trainList.values().iterator();
                while (iter.hasNext()) {
                	DriveableTrain driveable = iter.next();
                    if (driveable.getGroup().isRemoved() || driveable.getGroup() == null || driveable == null) {
                    	driveable.clearMember();
                        iter.remove();
                    } else {
                    	driveable.update(tick);
                    }
                }
            }
        }
    }

	public DriveableTrain getDriveableTrain(MinecartGroup group) {
		return trainList.get(group);
	}

	public boolean isDriver(Player player) {
		return driversList.containsKey(player);
	}

	public Driver getDriver(Player Player) {
		return driversList.getOrDefault(Player, null);
	}

	public void removeDriver(Player player) {
		if (isDriver(player)) {
			getDriver(player).clearMember();
			driversList.remove(player);
		}
	}

	public DriveableTrain addDriveableTrain(MinecartGroup group) {
		if (isDriveable(group)) {
			return getDriveable(group);
		}
		DriveableTrain driveable = new DriveableTrain(this, group);
		trainList.put(group, driveable);
		return driveable;
	}

	public Driver addDriver(Player player) {
		if (isDriver(player)) {
			return getDriver(player);
		}
		Driver driver = new Driver(player);
		driversList.put(player, driver);
		return driver;
	}
	
	public boolean hasUsePermission(CommandSender sender) {
        if (!TCDriverPermissions.DRIVE.has(sender)) {
            return false;
        }
        return true;
    }
}
