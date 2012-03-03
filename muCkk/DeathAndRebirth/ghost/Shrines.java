package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.messages.Messages;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Shrines {
	
	private FileConfiguration customConfig = null;
	private File shrinesFile; 
	private DAR plugin;
	private Location selection1, selection2;
	private boolean selectionMode;
	private String selectionPlayer;
	
	public Shrines(DAR plugin, String dir) {
		this.plugin = plugin;
		shrinesFile = new File(dir+"/shrines.yml");
		selectionMode = false;
	}
	
	public void reloadCustomConfig() {
	    if (shrinesFile == null) {
	    	shrinesFile = new File(plugin.getDataFolder(), "customConfig.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(shrinesFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("customConfig.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getCustomConfig() {
	    if (customConfig == null) {
	        reloadCustomConfig();
	    }
	    return customConfig;
	}
	
	public void saveCustomConfig() {
	    if (customConfig == null || shrinesFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(shrinesFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + shrinesFile, ex);
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
		getCustomConfig().set("shrines." +worldName +"." +name+".max.x", maxX);
		getCustomConfig().set("shrines." +worldName +"." +name+".max.y", maxY);
		getCustomConfig().set("shrines." +worldName +"." +name+".max.z", maxZ);
		getCustomConfig().set("shrines." +worldName +"." +name+".min.x", minX);
		getCustomConfig().set("shrines." +worldName +"." +name+".min.y", minY);
		getCustomConfig().set("shrines." +worldName +"." +name+".min.z", minZ);
		getCustomConfig().set("shrines." +worldName +"." +name+".binding", "true");
		
		selection1 = null;
		selection2 = null;
		saveCustomConfig();
		return true;
	}
	
	public void setSpawn(String shrineName, Player player) {
		String worldName = player.getWorld().getName();
		int x = player.getLocation().getBlockX(),
			y = player.getLocation().getBlockY(),
			z = player.getLocation().getBlockZ();
		
		getCustomConfig().set("shrines." +worldName +"." +shrineName+".spawn.z", z);
		getCustomConfig().set("shrines." +worldName +"." +shrineName+".spawn.y", y);
		getCustomConfig().set("shrines." +worldName +"." +shrineName+".spawn.x", x);
	}
	
	/**
	 * Removes a shrine from the world
	 * @param name
	 * @param player
	 */
	public void removeShrine(String name, Player player) {
		String worldName = player.getWorld().getName();
		
		getCustomConfig().set("shrines." +worldName +"." +name, null);
		saveCustomConfig();
	}
	
	public void update(Player player, String name) {
		World world = player.getWorld();
		String worldName = world.getName();
		if (getCustomConfig().getConfigurationSection("shrines." + worldName +"." + name +"." + "tb") == null) return;
		
		double	minX = getCustomConfig().getInt("shrines." + worldName +"." + name +"." + "tb.x", 0) - 1,
				minY = getCustomConfig().getInt("shrines." + worldName +"." + name +"." + "tb.y", 0) - 1,
				minZ = getCustomConfig().getInt("shrines." + worldName +"." + name +"." + "tb.z", 0) - 1,
				maxX = getCustomConfig().getInt("shrines." + worldName +"." + name +"." + "tb.x", 0) + 1,
				maxY = getCustomConfig().getInt("shrines." + worldName +"." + name +"." + "tb.y", 0) + 3,
				maxZ = getCustomConfig().getInt("shrines." + worldName +"." + name +"." + "tb.z", 0) + 1;
		selection1 = new Location(world, minX, minY, minZ);
		selection2 = new Location(world, maxX, maxY, maxZ);
		addShrine(name);
		getCustomConfig().set("shrines." + worldName +"." + name +".originalids", null);
		getCustomConfig().set("shrines." + worldName +"." + name +".tb", null);
		selection1 = null;
		selection2 = null;
		plugin.message.sendChat(player, Messages.update);
		saveCustomConfig();
	}
	
	/**
	 * Shows a list of all shrines
	 * @param player
	 * @param page
	 */
	public void list(Player player, int page) {
		String world = player.getWorld().getName();
		try {
			Set<String> names1 = getCustomConfig().getConfigurationSection("shrines." +world).getKeys(false);
			List<Object> names = Arrays.asList(names1.toArray());
			int pages = names1.size()/6;
			if(names1.size()%6 != 0) pages += 1;
			
			player.sendMessage("List of shrines in world "+world +" (Page "+page+"/"+pages+")");
			for (int i=page-1; i<page*6 && i< names.size(); i++) {
				if (getCustomConfig().getBoolean("shrines."+world+"."+names.get(i)+".binding", true)) player.sendMessage(i+1 +". "+names.get(i) +" (Soulbinding)");
				else player.sendMessage(i+1 +". "+names.get(i));
			}
		}catch (NullPointerException e) {
			plugin.message.sendChat(player, Messages.noShrinesFound);
		}
	}
	
	/**
	 * Checks if a shrine with the given name exists
	 * @param name
	 * @return
	 */
	public boolean exists(String name, String world) {
		Set<String> names = getCustomConfig().getConfigurationSection("shrines." +world).getKeys(false);
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
		Set<String> names = getCustomConfig().getConfigurationSection("shrines." +worldName).getKeys(false);
		int radius = 6;
		try {
			for (String name : names) {
				if (		locX < getCustomConfig().getInt("shrines." +worldName +"." +name+".max.x", 0)+radius
						&&	locX > getCustomConfig().getInt("shrines." +worldName +"." +name+".min.x", 0)-radius
						&&	locY < getCustomConfig().getInt("shrines." +worldName +"." +name+".max.y", 0)+radius
						&&	locY > getCustomConfig().getInt("shrines." +worldName +"." +name+".min.y", 0)-radius
						&&	locZ < getCustomConfig().getInt("shrines." +worldName +"." +name+".max.z", 0)+radius
						&&	locZ > getCustomConfig().getInt("shrines." +worldName +"." +name+".min.z", 0)-radius) {
					return name;
				}
			}
		}catch (NullPointerException e) {
			return null;
		}		
		return null;
	}
	
	/**
	 * Returns the nearest shrine or null if there is no shrine
	 * @param loc Location to check
	 * @return Location of the shrine
	 */
	public Location getNearestShrine(Location loc) {
		int x,y,z;
		double distance = Double.MAX_VALUE;
		String worldName = loc.getWorld().getName();
		Set<String> shrines = getCustomConfig().getConfigurationSection("shrines."+worldName).getKeys(false);
		Location shrineLoc, returnLoc = null;
		
		if (shrines == null) return null;
		
		for (String shrine : shrines) {
			x = getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"min.x", 0) + 
					( (getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"max.x", 0) - getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"min.x", 0)) / 2);
			y = getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"min.y", 0)+2;
			z = getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"min.z", 0) + 
					( (getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"max.z", 0) - getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"min.z", 0)) / 2);
			shrineLoc = new Location(loc.getWorld(), x, y, z);
			if(loc.distance(shrineLoc) < distance) {
				returnLoc = shrineLoc.clone();
				distance = loc.distance(shrineLoc);
			}
		}
		return returnLoc;
	}
	
	public Location getNearestShrineSpawn(Location loc) {
		int x,y,z;
		double distance = Double.MAX_VALUE;
		String worldName = loc.getWorld().getName();
		Set<String> shrines = getCustomConfig().getConfigurationSection("shrines."+worldName).getKeys(false);
		Location shrineLoc, returnLoc = null;
		String shrineName = "";
		if (shrines == null) return null;
		
		for (String shrine : shrines) {
			x = getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"min.x", 0) - 1;
			y = getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"min.y", 0) + 2;
			z = getCustomConfig().getInt("shrines."+worldName+"."+shrine+"."+"min.z", 0) - 1;
			shrineLoc = new Location(loc.getWorld(), x, y, z);
			if(loc.distance(shrineLoc) < distance) {
				shrineName = shrine; 
				returnLoc = shrineLoc.clone();
				distance = loc.distance(shrineLoc);
			}
		}
		String test = getCustomConfig().getString("shrines." +worldName +"." +shrineName+".spawn.x");
		if(test != null) {
			return new Location(loc.getWorld(),
					getCustomConfig().getInt("shrines."+worldName+"."+shrineName+".spawn.x", 0), 
					getCustomConfig().getInt("shrines."+worldName+"."+shrineName+".spawn.y", 0),
					getCustomConfig().getInt("shrines."+worldName+"."+shrineName+".spawn.z", 0));
		}
		else return returnLoc;
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
		if (		locX <= getCustomConfig().getInt("shrines." +worldName +"." +name+".max.x", 0)
				&&	locX >= getCustomConfig().getInt("shrines." +worldName +"." +name+".min.x", 0)
				&&	locY <= getCustomConfig().getInt("shrines." +worldName +"." +name+".max.y", 0)
				&&	locY >= getCustomConfig().getInt("shrines." +worldName +"." +name+".min.y", 0)
				&&	locZ <= getCustomConfig().getInt("shrines." +worldName +"." +name+".max.z", 0)
				&&	locZ >= getCustomConfig().getInt("shrines." +worldName +"." +name+".min.z", 0)) {
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
		Set<String> names = getCustomConfig().getConfigurationSection("shrines." +worldName).getKeys(false);
		int radius = plugin.getConfig().getInt("SHRINE_RADIUS");
		try {
			for (String name : names) {
				if (		locX < getCustomConfig().getInt("shrines." +worldName +"." +name+".max.x", 0)+radius
						&&	locX > getCustomConfig().getInt("shrines." +worldName +"." +name+".min.x", 0)-radius
						&&	locY < getCustomConfig().getInt("shrines." +worldName +"." +name+".max.y", 0)+radius
						&&	locY > getCustomConfig().getInt("shrines." +worldName +"." +name+".min.y", 0)-radius
						&&	locZ < getCustomConfig().getInt("shrines." +worldName +"." +name+".max.z", 0)+radius
						&&	locZ > getCustomConfig().getInt("shrines." +worldName +"." +name+".min.z", 0)-radius) {
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
	
	public boolean checkBinding(String name, String worldName) {
		return getCustomConfig().getBoolean("shrines."+worldName+"."+name+".binding", true);
	}
	
	public String setBinding(String name, String world) {
		if (getCustomConfig().getBoolean("shrines."+world+"."+name+".binding", true)) {
			getCustomConfig().set("shrines."+world+"."+name+".binding", false);
			return "disabled";
		}
		else {
			getCustomConfig().set("shrines."+world+"."+name+".binding", true);
			return "enabled";
		}
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
