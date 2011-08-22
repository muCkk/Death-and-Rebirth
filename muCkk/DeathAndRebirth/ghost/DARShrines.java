package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.util.List;

import muCkk.DeathAndRebirth.config.DARProperties;
import muCkk.DeathAndRebirth.messages.DARErrors;
import muCkk.DeathAndRebirth.messages.DARMessages;
import muCkk.DeathAndRebirth.messages.Messages;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class DARShrines {
	
	private String shrinesDir;
	private File file; 
	private Configuration yml ;
	private DARMessages message;
	private DARProperties config;
	
	private Location selection1, selection2;
	private boolean selectionMode;
	private String selectionPlayer;
	
	public DARShrines(String dir, String fileName, DARMessages message, DARProperties config) {
		this.shrinesDir = dir;
		this.message = message;
		this.config = config;
		file = new File(fileName);
		selectionMode = false;
		load();
	}
	
	public void save() {
		yml.save();
	}
	
	public void load() {
		if(!file.exists()){
            try {
            	new File(shrinesDir).mkdir();
                file.createNewFile(); 
            } catch (Exception ex) {
            }
        } else {
        	DARErrors.shrinesLoaded();
        }
		try {
            yml = new Configuration(file);
            yml.load();
        } catch (Exception e) {
        }
	}
	
	/**
	 * Adds a shrine to the world
	 * @param name of the shrine
	 * @param selection1 first location of the cuboid selection
	 * @param selection2 second location of the cuboid selection
	 */
	public boolean addShrine(String name) {
		if (selection1 == null || selection2 == null) {
			return false;
		}
		World world = selection1.getWorld();
		String worldName = world.getName();
		// find out the min and max location
		int x1 = selection1.getBlockX(), x2 = selection2.getBlockX(), y1 = selection1.getBlockY(), y2 = selection2.getBlockY(), z1 = selection1.getBlockZ(), z2 = selection2.getBlockZ();
		int minX, minY,	minZ, maxX, maxY, maxZ;
		
		if (x1<x2)	{ minX = x1; maxX = x2; }
		else		{ minX = x2; maxX = x1;	}
		if (y1<y2)	{ minY = y1; maxY = y2; }
		else		{ minY = y2; maxY = y1;	}
		if (z1<z2)	{ minZ = z1; maxZ = z2; }
		else		{ minZ = z2; maxZ = z1;	}
		
		// writing the information
		yml.setProperty("shrines." +worldName +"." +name+".max.x", maxX);
		yml.setProperty("shrines." +worldName +"." +name+".max.y", maxY);
		yml.setProperty("shrines." +worldName +"." +name+".max.z", maxZ);
		yml.setProperty("shrines." +worldName +"." +name+".min.x", minX);
		yml.setProperty("shrines." +worldName +"." +name+".min.y", minY);
		yml.setProperty("shrines." +worldName +"." +name+".min.z", minZ);
		
		selection1 = null;
		selection2 = null;
		yml.save();
		return true;
	}
	
	/**
	 * Removes a shrine from the world
	 * @param name
	 * @param player
	 */
	public void removeShrine(String name, Player player) {
		String worldName = player.getWorld().getName();
		
		yml.removeProperty("shrines." +worldName +"." +name);
		yml.save();
	}
	
	public void update(Player player, String name) {
		World world = player.getWorld();
		String worldName = world.getName();
		if (yml.getKeys("shrines." + worldName +"." + name +"." + "tb") == null) return;
		
		double	minX = yml.getInt("shrines." + worldName +"." + name +"." + "tb.x", 0) - 1,
				minY = yml.getInt("shrines." + worldName +"." + name +"." + "tb.y", 0) - 1,
				minZ = yml.getInt("shrines." + worldName +"." + name +"." + "tb.z", 0) - 1,
				maxX = yml.getInt("shrines." + worldName +"." + name +"." + "tb.x", 0) + 1,
				maxY = yml.getInt("shrines." + worldName +"." + name +"." + "tb.y", 0) + 3,
				maxZ = yml.getInt("shrines." + worldName +"." + name +"." + "tb.z", 0) + 1;
		selection1 = new Location(world, minX, minY, minZ);
		selection2 = new Location(world, maxX, maxY, maxZ);
		addShrine(name);
		yml.removeProperty("shrines." + worldName +"." + name +".originalids");
		yml.removeProperty("shrines." + worldName +"." + name +".tb");
		selection1 = null;
		selection2 = null;
		message.sendChat(player, Messages.update);
		yml.save();
	}
	
	/**
	 * Shows a list of all shrines
	 * @param player
	 * @param page
	 */
	public void list(Player player, int page) {
		String world = player.getWorld().getName();
		try {
			List<String> names = yml.getKeys("shrines." +world);
			int pages = names.size()/6;
			if(names.size()%6 != 0) pages += 1;
			
			player.sendMessage("List of shrines in world "+world +" (Page "+page+"/"+pages+")");
			for (int i=page-1; i<page*6 && i< names.size(); i++) {
				player.sendMessage(i+1 +". "+names.get(i));
			}
		}catch (NullPointerException e) {
			message.sendChat(player, Messages.noShrinesFound);
		}
	}
	
	/**
	 * Checks if a shrine with the given name exists
	 * @param name
	 * @return
	 */
	public boolean exists(String name, String world) {
		List<String> names = yml.getKeys("shrines." +world);
		try {
			for (String currentName : names) {
				if (currentName.equalsIgnoreCase(name)) return true;
			}
		}catch (NullPointerException e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Returns the name of the shrine which is near the location
	 * @param loc Location which gets checked
	 * @return Name of the shrine as String
	 */	
	public String getClose(Location loc) {
		String worldName = loc.getWorld().getName();
		int locX = loc.getBlockX(),
			locY = loc.getBlockY(),
			locZ = loc.getBlockZ();
		List<String> names = yml.getKeys("shrines." +worldName);
		int radius = 6;
		try {
			for (String name : names) {
				if (		locX < yml.getInt("shrines." +worldName +"." +name+".max.x", 0)+radius
						&&	locX > yml.getInt("shrines." +worldName +"." +name+".min.x", 0)-radius
						&&	locY < yml.getInt("shrines." +worldName +"." +name+".max.y", 0)+radius
						&&	locY > yml.getInt("shrines." +worldName +"." +name+".min.y", 0)-radius
						&&	locZ < yml.getInt("shrines." +worldName +"." +name+".max.z", 0)+radius
						&&	locZ > yml.getInt("shrines." +worldName +"." +name+".min.z", 0)-radius) {
					return name;
				}
			}
		}catch (NullPointerException e) {
			return null;
		}		
		return null;
	}
	
	/**
	 * Checks if the block is part of the given shrine
	 * @param name of the shrine to be checked
	 * @param block which is checked
	 * @return wether the block is part of the shrine
	 */

	public boolean isShrineArea(String name, Block block) {
		Location loc = block.getLocation();
		String worldName = loc.getWorld().getName();
		int locX = loc.getBlockX(),
			locY = loc.getBlockY(),
			locZ = loc.getBlockZ();		
		if (		locX <= yml.getInt("shrines." +worldName +"." +name+".max.x", 0)
				&&	locX >= yml.getInt("shrines." +worldName +"." +name+".min.x", 0)
				&&	locY <= yml.getInt("shrines." +worldName +"." +name+".max.y", 0)
				&&	locY >= yml.getInt("shrines." +worldName +"." +name+".min.y", 0)
				&&	locZ <= yml.getInt("shrines." +worldName +"." +name+".max.z", 0)
				&&	locZ >= yml.getInt("shrines." +worldName +"." +name+".min.z", 0)) {
			return true;
		}		
		return false;
	}
		
	/**
	 * Checks if the player is standing on a shrine
	 * @param player who gets checked
	 * @return true if the player stands on a shrine, else false
	 */
	public boolean isOnShrine(Player player) {
		String worldName = player.getWorld().getName();
		Location loc = player.getLocation();
		int locX = loc.getBlockX(),
			locY = loc.getBlockY(),
			locZ = loc.getBlockZ();
		List<String> names = yml.getKeys("shrines." +worldName);
		int radius = config.getShrineRadius();
		try {
			for (String name : names) {
				if (		locX < yml.getInt("shrines." +worldName +"." +name+".max.x", 0)+radius
						&&	locX > yml.getInt("shrines." +worldName +"." +name+".min.x", 0)-radius
						&&	locY < yml.getInt("shrines." +worldName +"." +name+".max.y", 0)+radius
						&&	locY > yml.getInt("shrines." +worldName +"." +name+".min.y", 0)-radius
						&&	locZ < yml.getInt("shrines." +worldName +"." +name+".max.z", 0)+radius
						&&	locZ > yml.getInt("shrines." +worldName +"." +name+".min.z", 0)-radius) {
					return true;
				}
			}
		}catch (NullPointerException e) {
			return false;
		}		
		return false;
	}
	
	public boolean locationIsAlreadyShrine() {
		if (getClose(selection1) != null || getClose(selection2) != null) {
			return true;
		}
		else return false;
	}
	
	public boolean checkSelection() {
		if (selection1 == null || selection2 == null) return false;
		else return true;
		
	}
	
	public void setSel1(Location loc) {
		this.selection1 = loc;
	}
	public void setSel2(Location loc) {
		this.selection2 = loc;
	}
	public void setSelectionMode(boolean b) {
		this.selectionMode = b;
	}
	public void setPlayer(String player) {
		this.selectionPlayer = player;
	}
	public String getSelPlayer() {
		return selectionPlayer;
	}
	public boolean isSelModeEnable() {
		return selectionMode;
	}
}
