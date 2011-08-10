package muCkk.DeathAndRebirth;

import org.bukkit.Location;

public class DARPlayer {

	private boolean state;			// true: dead, false: alive
	private String name,world;
	private Location location;
	
	public DARPlayer(String name, Location location, String world) {
		state = false;
		this.name = name;
		this.location = location;
		this.world = world;
	}
	
	public DARPlayer(String name, Location location, String world, Boolean state) {
		this.state = state;
		this.name = name;
		this.location = location;
		this.world = world;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getState() {
		return state;
	}
	public String getStateAsString() {
		if (state == true) {
			return "true";
		}
		return "false";
	}
	public void setState(Boolean state) {
		this.state = state;
	}
	public Location getlocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public void setWorld(String world) {
		this.world = world;
	}
	public String getWorld() {
		return world;
	}
}
