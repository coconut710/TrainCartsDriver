package com.sketchtown.bukkit.tcdriver;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;

public class Driver {
	private final Player player;
	private MinecartMember<?> member;
	private boolean pressing;
	public Driver(Player player) {
		this.player = player;
	}
	public Player getPlayer() {
		return player;
	}
	public Vector getInputVector() {
		Vector eyeDirection = player.getEyeLocation().getDirection();
		Vector moveDirection = player.getVelocity();
		
		if (moveDirection.lengthSquared() == 0) {
			return new Vector(0,0,0);
		}
		
		double X0 = -eyeDirection.getX();
		double Z0 = -eyeDirection.getZ();
		
		double sinp = -eyeDirection.getY();
		double cosp = Math.sqrt(Math.pow(X0,2) + Math.pow(Z0,2));
		double siny = Z0/cosp;
		double cosy = X0/cosp;
		
		double X1=moveDirection.getX()*cosy	+moveDirection.getZ()*siny;
		double Y1=0							+moveDirection.getY();
		
		double X2=X1*cosp	-Y1*sinp	+0;
		double Y2=X1*sinp	+Y1*cosp	+0;
		double Z2=-moveDirection.getX()*siny+moveDirection.getZ()*cosy;
		
		return new Vector(X2,Y2,Z2).multiply(1/moveDirection.length());
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
	public void updateMember(MinecartGroup group) {
		if (group.head().getEntity().getPlayerPassengers().contains(this.getPlayer())) {
			member = group.head();
		} else if (group.tail().getEntity().getPlayerPassengers().contains(this.getPlayer())) {
			member = group.tail();
		} else {
			member = null;
		}
	}
}