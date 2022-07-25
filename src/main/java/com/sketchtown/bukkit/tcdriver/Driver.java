package com.sketchtown.bukkit.tcdriver;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;

public class Driver {
	private final Player player;
	private MinecartMember<?> member;
	private DriveableTrain driveable;
	private boolean pressing;
	public Driver(Player player) {
		this.player = player;
		this.driveable = null;
	}
	public Player getPlayer() {
		return player;
	}
	public DriveableTrain getHandler() {
		return driveable;
	}
	public Vector getInputVector() {
		double eyeX = 	Math.toRadians(player.getEyeLocation().getX());
		double eyeY = 	Math.toRadians(player.getEyeLocation().getY());
		double eyeZ = 	Math.toRadians(player.getEyeLocation().getZ());
		double yaw = 	Math.toRadians(player.getEyeLocation().getYaw());
		double pitch = 	Math.toRadians(player.getEyeLocation().getPitch());
		Vector moveDirection = player.getVelocity();
		moveDirection.subtract(new Vector(0,-0.0784000015258789,0));
		if (moveDirection.lengthSquared() == 0) {
			return new Vector(0,0,0);
		}
		
		double X0=moveDirection.getX();
		double Y0=moveDirection.getY();
		double Z0=moveDirection.getZ();
		
		double siny = -Math.sin(yaw);
		double cosy = -Math.cos(yaw);
		double sinp = -Math.sin(pitch);
		double cosp = -Math.cos(pitch);
		
		double X1=X0*cosy			+Z0*siny;
		double Y1=			Y0;
		double Z1=-X0*siny			+Z0*cosy;
		
		double X2=X1*cosp	-Y1*sinp	+0;
		double Y2=X1*sinp	+Y1*cosp	+0;
		double Z2=						+Z1;
		
		return new Vector(X2,Y2,Z2).normalize();
	}
	public boolean isPressing() {
		return pressing;
	}
	public void setPressing(boolean pressing) {
		this.pressing = pressing;
	}
	public MinecartMember<?> getMember() {
		return member;
	}
	public void updateMember(Entity cartEntity, DriveableTrain driveable) {
		MinecartGroup group = driveable.getGroup();
		MinecartMember<?> head = group.head();
		MinecartMember<?> tail = group.tail();
		if (head.getEntity().getEntity() == cartEntity) {
			member = head;
		} else if (tail.getEntity().getEntity() == cartEntity) {
			member = tail;
		} else {
			clearMember();
			return;
		}
		syncTrainAndDriver(driveable, null);
	}
	public void updateMember(DriveableTrain driveable) {
		MinecartGroup group = driveable.getGroup();
		if (group.head().getEntity().getPlayerPassengers().contains(this.getPlayer())) {
			member = group.head();
		} else if (group.tail().getEntity().getPlayerPassengers().contains(this.getPlayer())) {
			member = group.tail();
		} else {
			clearMember();
			return;
		}
		syncTrainAndDriver(driveable, null);
	}
	public void syncTrainAndDriver(DriveableTrain driveable, EnumControlType controlType) {
		Driver driver = driveable.getPlugin().addDriver(player);
		driveable.showBossBars(player);
		this.driveable = driveable;
		driveable.setDriver(driver);
		driveable.setControlType(controlType);
	}
	public void clearMember() {
		member = null;
		if (driveable != null) {
			driveable.clearBossBars();
			driveable.removeDriver();
			this.driveable = null;
		}
	}
}