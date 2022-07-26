package com.sketchtown.bukkit.tcdriver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;

public class DriveableTrain {
	private TCDriver plugin;
	private MinecartGroup group;
	
	private Driver driver;
	private int notch;
	private EnumDriveState driveState;
	private EnumControlType controlType;

	private double speedLimit;
	private double acceleration;
	private double power = 0.005d;
	
    private boolean ldoor;
    private boolean rdoor;
    
    private BossBar bossbar;
    private BossBar signalbar;
    private BossBar stationbar;

    private boolean hasTargetStation;
    private Location targetStation;
    
	public DriveableTrain(TCDriver plugin, MinecartGroup group) {
		//setting about drive
		this.plugin = plugin;
		this.group = group;
		
	    this.driver = null;//about player
	    this.notch = 0;
		this.driveState = EnumDriveState.STOP;
		this.controlType = EnumControlType.AUTO;

		this.speedLimit = group.getProperties().getSpeedLimit();
		this.acceleration = 0;
		
		this.ldoor = false;
	    this.rdoor = false;
	    
	    this.bossbar = plugin.getServer().createBossBar("", BarColor.WHITE, BarStyle.SEGMENTED_20);
	    this.signalbar = plugin.getServer().createBossBar("", BarColor.WHITE, BarStyle.SOLID);
	    this.stationbar = plugin.getServer().createBossBar("", BarColor.WHITE, BarStyle.SOLID);

	    this.hasTargetStation = false;
	    this.targetStation = new Location(group.getWorld(), 0, 0, 0);
	    
	    group.setForwardForce(0);
	   	group.getProperties().setSoundEnabled(false);
	}
	public MinecartGroup getGroup() {
		return group;
	}
	public Driver getDriver() {
		return this.driver;
	}
	public void update(int tick) {
		//calculate player input
		if (driver != null && driver.getMember() != null) {
			Vector inputVector = driver.getInputVector();
			if (!driver.isPressing() && inputVector.lengthSquared() != 0) {
				switch (this.controlType) {
				case MANU:
					if (inputVector.getZ() <= -0.5) {
						if (driveState == EnumDriveState.STOP) {
							group.reverse();
							driver.updateMember(this);
							sendTitle(driver.getMember() == group.tail() ? "[역방향]" : "[순방향]");
						} else {
							this.addNotch(-1);
						}
					} else if (inputVector.getZ() >= 0.5) {
						this.addNotch(1);
					}
					break;
				case SEMI:
					if (inputVector.getY() >= 0.5) {
						//this.launch();
					}
					break;
				case AUTO:
				default:
					break;
				}
				if (driveState == EnumDriveState.STOP) {
					if (inputVector.getX() >= 0.5) {//A
						toggleDoor(true, false);
					} else if (inputVector.getX() <= -0.5) {//D
						toggleDoor(false, true);
					}
				}
				driver.setPressing(true);
			}
			if (inputVector.lengthSquared() == 0 && driver.isPressing()) {
				driver.setPressing(false);
			}
		}
		//calculate movement
		double c = group.getProperties().getSpeedLimit();
		double v = Math.abs(group.getAverageForce());
		double speedRatio = 0;
		if (c != 0) {
			speedRatio = ((2*c)-v)/(2*c);
		}
		if (notch > 0) {
			group.setForwardForce(v + (acceleration * speedRatio));
		}
		if (notch <= 0 && driveState != EnumDriveState.STOP) {
			if (v + acceleration < 0) {
				notch = 0;
				group.setForwardForce(0);
				driveState = EnumDriveState.STOP;
		    	for (MinecartMember<?> member : group) {
		    		group.getWorld().playSound(member.getEntity().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2.0f, 1.5f);
		    	}
		    	
			} else {
				group.setForwardForce(v + acceleration);
			}
		}
		
		String doorString = "";
		String notchString = "";
		String stationString = "";
		if (ldoor) {
			doorString += ChatColor.GREEN + "[개방";
		} else {
			doorString += ChatColor.GOLD + "[폐쇄";
		}
		doorString += ChatColor.RESET + "/";
		if (rdoor) {
			doorString += ChatColor.GREEN + "개방]";
		} else {
			doorString += ChatColor.GOLD + "폐쇄]";
		}
		if (controlType == EnumControlType.SEMI) {
			if (driveState == EnumDriveState.STOP) {
				notchString = ChatColor.GRAY + "[반자동]";
			} else {
				notchString = ChatColor.AQUA + "[반자동]";
			}
		} else if (notch > 0) {
			notchString = ChatColor.DARK_AQUA + "[역행" + notch + "]";
		} else if (notch < 0) {
			notchString = ChatColor.GOLD + "[제동" + Math.abs(notch) + "]";
		} else {
			notchString = ChatColor.GRAY + "[N]";
		}
		if (group.size() > 0 && hasTargetStation) {
			Location locA = group.get((int) Math.round((group.size() - 1) / 2)).getEntity().getLocation();
			if (group.size() % 2 == 0) {
				locA = group.get((group.size() / 2) - 1).getEntity().getLocation();
				locA.add(group.get((group.size() / 2)).getEntity().getLocation());
				locA.multiply(0.5);
			}
			double distance = targetStation.distance(locA);
			stationString = "정차 위치 || " + String.format("%.2f", distance) + " 블럭";
			float distanceF = (float) (distance / 10d);
			if (distanceF > 1) {
				distanceF = 1;
			}
			if (distanceF < 0) {
				distanceF = 0;
			}
			setStationBar(stationString, distanceF);
		}
		String bossbarString = "출입문 : " + doorString + ChatColor.RESET + " || " + notchString + ChatColor.RESET + " || 속력 : " + ChatColor.BOLD + Math.round(group.getAverageForce() * 100) + ChatColor.RESET +  "/" + Math.round(group.getProperties().getSpeedLimit() * 100) + "km/h";
		setBossBar(bossbarString, (float) Math.max(0, Math.min(1.0d, group.getAverageForce() / group.getProperties().getSpeedLimit())));
		if (tick % 2 == 0) {
	    	for (MinecartMember<?> member : group) {
	    		group.getWorld().playSound(member.getEntity().getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.5f, (float) (0.5f + (c!=0 ? (v/c) : 0)));
	    	}
		}
		if (tick % (v!=0 ? Math.round(20/v) : 10000) == 0 || (tick + 5) % (v!=0 ? Math.round(20/v) : 10000) == 0) {
	    	for (MinecartMember<?> member : group) {
	    		group.getWorld().playSound(member.getEntity().getLocation(), Sound.BLOCK_METAL_STEP, 1.0f, 1.5f);
	    	}
		}
	}

	private void setBossBar(String string, float f) {
		if (f>1) f=1;
		if (f<0) f=0;
		bossbar.setTitle(string);
		bossbar.setProgress(f);
	}
	private void setSignalBar(String string, float f) {
		if (f>1) f=1;
		if (f<0) f=0;
		signalbar.setTitle(string);
		signalbar.setProgress(f);
	}
	private void setStationBar(String string, float f) {
		if (f>1) f=1;
		if (f<0) f=0;
		stationbar.setTitle(string);
		stationbar.setProgress(f);
	}
	public void showBossBars(Player player) {
		if (!bossbar.getPlayers().contains(player)) {
			bossbar.addPlayer(player);
		}
		if (!signalbar.getPlayers().contains(player)) {
			signalbar.addPlayer(player);
		}
		if (!stationbar.getPlayers().contains(player)) {
			stationbar.addPlayer(player);
		}
	}
	public void clearBossBars() {
		bossbar.removeAll();
		signalbar.removeAll();
		stationbar.removeAll();
	}
	
	private void sendTitle(String string) {
		if (driver == null) {
			return;
		} else {
			driver.getPlayer().sendTitle(" ", string, 0, 70, 20);
		}
	}
	
	public void toggleDoor(boolean left, boolean right) {
		if (ldoor) {
			closeDoor(left, false);
		} else {
			openDoor(left, false);
		}
		if (rdoor) {
			closeDoor(false, right);
		} else {
			openDoor(false, right);
		}
	}
	
	public void closeDoor(boolean left, boolean right) {
		if (!left && !right) {
			return;
		}
		String directionText = "";
		if (left) {
			ldoor = false;
			group.playNamedAnimation("ldclose");
			directionText = "왼쪽";
		}
		if (right) {
			rdoor = false;
			group.playNamedAnimation("rdclose");
			directionText = "오른쪽";
		}
		if (!this.ldoor && !this.rdoor) {
			group.getProperties().setPlayersExit(false);
		}
		sendTitle(ChatColor.GOLD + String.format("[%s 폐쇄]", directionText));
    	for (MinecartMember<?> member : group) {
    		for (Player player : member.getEntity().getPlayerPassengers()) {
    			if (!isDriver(player)) {
    				player.sendMessage(ChatColor.GREEN + "출입문이 닫힙니다.");
    			}
    		}
    		group.getWorld().playSound(member.getEntity().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2.0f, 1.5f);
    	}
	}
	
	public void openDoor(boolean left, boolean right) {
		if (!left && !right) {
			return;
		}
		String directionText = "";
		if (left) {
			ldoor = true;
			group.playNamedAnimation("ldopen");
			directionText = "왼쪽";
		}
		if (right) {
			rdoor = true;
			group.playNamedAnimation("rdopen");
			directionText = "오른쪽";
		}
		group.getProperties().setPlayersEnter(true);
		group.getProperties().setPlayersExit(true);
		sendTitle(ChatColor.GREEN + String.format("[%s 개방]", directionText));
    	for (MinecartMember<?> member : group) {
    		for (Player player : member.getEntity().getPlayerPassengers()) {
    			if (!isDriver(player)) {
    				player.sendMessage(ChatColor.GREEN + "출입문이 열립니다.");
    			}
    		}
    		group.getWorld().playSound(member.getEntity().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2.0f, 1.5f);
    	}
	}
	
	public EnumControlType getControlType() {
		return this.controlType;
	}
	public void setTargetStation(Location location) {
		this.hasTargetStation = true;
		this.targetStation = location;
	}
	public Location getTargetStation() {
		if (hasTargetStation) {
			return this.targetStation;
		}
		return null;
	}
	public void clearTargetStation() {
		this.hasTargetStation = false;
	}
	public boolean isDriver(Player player) {
		if (plugin.getDriver(player) == driver && driver.getPlayer() == player) {
			return true;
		}
		return false;
	}
	private void addNotch(int i) {
		if (-9 < i && i < 4) {
			notch += i;
			if (notch > 4) notch = 4;
			if (notch < -9) notch = -9;
		}
		if (notch == 0) {
			acceleration = 0;
		} else {
			acceleration = notch * power;
		}
		group.getWorld().playSound(driver.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 100.0f, 2.0f);
		if (notch > 0) {
			sendTitle(String.format("[역행%d]", notch));
		}
		if (notch == 0) {
			sendTitle("[N]");
		}
		if (notch < 0) {
			sendTitle(String.format("[제동%d]", -notch));
		}
	}
	public void setProperties(DriveableTrain driveableTrain) {
		
	}
	public void removeDriver() {
		driver = null;
	}
	public void clearMember() {
		if (getDriver() != null) {
			getDriver().clearMember();
		}
	}
	public TCDriver getPlugin() {
		return plugin;
	}
	public void setDriver(Driver driver) {
		this.driver=driver;
	}
	public void setControlType(EnumControlType controlType) {
		if (controlType == null) {
			return;
		}
		this.controlType = controlType;
	}
}
