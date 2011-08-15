package muCkk.DeathAndRebirth;

import java.io.File;
import java.util.List;

import muCkk.DeathAndRebirth.Messages.DARErrors;
import muCkk.DeathAndRebirth.Messages.DARMessages;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class DARShrines {
	
	private String shrinesFile, shrinesDir;
	private File yml; 
	private Configuration config ;
	
	public DARShrines(String dir, String fileName) {
		this.shrinesDir = dir;
		this.shrinesFile = fileName;
		yml = new File(shrinesFile);
		load();
	}
	
	public void save() {
		config.save();
	}
	
	public void load() {
		if(!yml.exists()){
            try {
            	new File(shrinesDir).mkdir();
                yml.createNewFile(); 
            } catch (Exception ex) {
            }
        } else {
        	DARErrors.shrinesLoaded();
        }
		try {
            config = new Configuration(yml);
            config.load();
        } catch (Exception e) {
        }
	}
	
	/**
	 * Adds a new shrine to the world
	 * @param shrine
	 * @param name
	 */
	public void addShrine(DARShrine shrine, String name) {
		String world = shrine.getWorld();
				
		config.setProperty("shrines." +world +"." +name+".tb.x", shrine.getTB()[0]);
		config.setProperty("shrines." +world +"." +name+".tb.y", shrine.getTB()[1]);
		config.setProperty("shrines." +world +"." +name+".tb.z", shrine.getTB()[2]);
		
		// *** original IDs ********
		int[] ids = shrine.getIDs();
		Block [] blocks = shrine.getBlocks();
		Block block;
		for (int i = 0; i< ids.length; i++) {
			config.setProperty("shrines." +world +"." +name+".originalids."+i, ids[i]);
			block = blocks[i];
		// *** saving slabs, double slabs and wool ****
			if(ids[i] == 44 || ids[i] == 43 || ids[i] == 35) {
				config.setProperty("shrines." +world +"." +name+".originalData."+i, block.getData());
			}
		}
		
		config.save();
	}
	
	/**
	 * Shows a list of all shrines
	 * @param player
	 * @param page
	 */
	public void list(Player player, int page) {
		String world = player.getWorld().getName();
		try {
			List<String> names = config.getKeys("shrines." +world);
			int pages = names.size()/6;
			if(names.size()%6 != 0) pages += 1;
			
			player.sendMessage("List of shrines in world "+world +" (Page "+page+"/"+pages+")");
			for (int i=page-1; i<page*6 && i< names.size(); i++) {
				player.sendMessage(i+1 +". "+names.get(i));
			}
		}catch (NullPointerException e) {
			DARMessages.noShrinesFound(player);
		}
	}
	
	/**
	 * Checks if a shrine with the given name exists
	 * @param name
	 * @return
	 */
	public boolean exists(String name, String world) {
		List<String> names = config.getKeys("shrines." +world);
		try {
			for (String currentName : names) {
				if (currentName.equalsIgnoreCase(name)) return true;
			}
		}catch (NullPointerException e) {
			// TODO NullPointer: no shrines (exist)
			return false;
		}
		return false;
	}
	
		// tb, rock1, rock2, rock3
		// NW, N, NE
		// SW, S, SE
		// W, E
	
	/**
	 * Removes a shrine from the world
	 * @param name
	 * @param player
	 */
	public void removeShrine(String name, Player player) {
		String world = player.getWorld().getName();
		int x = config.getInt("shrines." +world +"." +name+".tb.x", 0),
			y = config.getInt("shrines." +world +"." +name+".tb.y", 0),
			z = config.getInt("shrines." +world +"." +name+".tb.z", 0);
		
		Block tb = player.getWorld().getBlockAt(x, y, z);
		Block [] shrineBlocks = { tb, tb.getRelative(BlockFace.UP, 1), tb.getRelative(BlockFace.UP, 2), tb.getRelative(BlockFace.UP, 3),
				tb.getRelative(BlockFace.NORTH_WEST, 1), tb.getRelative(BlockFace.NORTH, 1), tb.getRelative(BlockFace.NORTH_EAST, 1),
				tb.getRelative(BlockFace.SOUTH_WEST, 1), tb.getRelative(BlockFace.SOUTH, 1),tb.getRelative(BlockFace.SOUTH_EAST, 1),
				tb.getRelative(BlockFace.WEST, 1), tb.getRelative(BlockFace.EAST, 1) };
		int id;
		for (int i=0; i<shrineBlocks.length; i++) {
			id = config.getInt("shrines." +world +"." +name+".originalids."+i, 0);
			shrineBlocks[i].setTypeId(id);
			if (id == 44 || id == 43 || id == 35) {
				shrineBlocks[i].setData((byte) config.getInt("shrines." +world +"." +name+".originalData."+i, 0));
			}
		}
		
		config.removeProperty("shrines." +world +"." +name);
		config.save();
	}
	
	/**
	 * Returns the name of the shrine which is near the location
	 * @param loc Location which gets checked
	 * @return Name of the shrine as String
	 */	
	public String getClose(Location loc) {
		String world = loc.getWorld().getName();		
		List<String> names = config.getKeys("shrines." +world);
		try {
			for (String name : names) {
				Location locOfShrine = new Location(loc.getWorld(),
						config.getInt("shrines." +world +"." +name+".tb.x", 0),
						config.getInt("shrines." +world +"." +name+".tb.y", 0),
						config.getInt("shrines." +world +"." +name+".tb.z", 0));
				
				double dist = loc.distance(locOfShrine);
				if (dist < 6) {
					return name;
				}
			}
		}catch (NullPointerException e) {
			// TODO NullPointer: no shrines (getClose)
			return null;
		}
		
		return null;
	}
	
	/**
	 * Checks if the block is around the shrine (3x3x3 area - everything above the glowstones)
	 * @param name of the shrine
	 * @param block which was placed
	 * @param player who placed the block
	 * @return true or false ;)
	 */
	public boolean isShrineArea(String name, Block block, Player player) {
		String world = player.getWorld().getName();
		int 	tx = config.getInt("shrines." +world +"." +name+".tb.x", 0),
				ty = config.getInt("shrines." +world +"." +name+".tb.y", 0),
				tz = config.getInt("shrines." +world +"." +name+".tb.z", 0);
		
		Block tb = player.getWorld().getBlockAt(tx, ty, tz);
		Block nw = tb.getRelative(BlockFace.NORTH_WEST, 1);
		Block n = tb.getRelative(BlockFace.NORTH, 1);
		Block ne = tb.getRelative(BlockFace.NORTH_EAST, 1);
		Block sw = tb.getRelative(BlockFace.SOUTH_WEST, 1);
		Block s = tb.getRelative(BlockFace.SOUTH, 1);
		Block se = tb.getRelative(BlockFace.SOUTH_EAST, 1);
		Block w = tb.getRelative(BlockFace.WEST, 1);
		Block e = tb.getRelative(BlockFace.EAST, 1);
		
		Block[] shrineBlocks = {tb, nw, n, ne, sw, s, se, w, e };
		for (Block sb : shrineBlocks) {
			if (block.getLocation().equals(sb.getLocation())) {
				return true;
			}
			if(block.getLocation().equals(sb.getRelative(BlockFace.UP, 1).getLocation())) {
				return true;
			}
			if(block.getLocation().equals(sb.getRelative(BlockFace.UP, 2).getLocation())) {
				return true;
			}
			if(block.getLocation().equals(sb.getRelative(BlockFace.UP, 3).getLocation())) {
				return true;
			}
		}
	
		return false;
	}
	
	/**
	 * Checks if the given block is part of a shrine
	 * @param name of the shrine
	 * @param block which was involved in the action
	 * @param player who was involved in the action
	 * @return true if the block is part of the shrine
	 */
	public boolean isShrine(String name, Block block, Player player) {
		String world = player.getWorld().getName();
		int 	tx = config.getInt("shrines." +world +"." +name+".tb.x", 0),
				ty = config.getInt("shrines." +world +"." +name+".tb.y", 0),
				tz = config.getInt("shrines." +world +"." +name+".tb.z", 0);
		
		Block tb = player.getWorld().getBlockAt(tx, ty, tz);
		Block rock1 = tb.getRelative(BlockFace.UP, 1);
		Block rock2 = tb.getRelative(BlockFace.UP, 2);
		Block rock3 = tb.getRelative(BlockFace.UP, 3);
		Block nw = tb.getRelative(BlockFace.NORTH_WEST, 1);
		Block n = tb.getRelative(BlockFace.NORTH, 1);
		Block ne = tb.getRelative(BlockFace.NORTH_EAST, 1);
		Block sw = tb.getRelative(BlockFace.SOUTH_WEST, 1);
		Block s = tb.getRelative(BlockFace.SOUTH, 1);
		Block se = tb.getRelative(BlockFace.SOUTH_EAST, 1);
		Block w = tb.getRelative(BlockFace.WEST, 1);
		Block e = tb.getRelative(BlockFace.EAST, 1);
		
		Block[] shrineBlocks = {tb, rock1, rock2, rock3, nw, n, ne, sw, s, se, w, e };
		for (Block sb : shrineBlocks) {
			if (block.getLocation().equals(sb.getLocation())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the player is standing on a shrine
	 * @param player who gets checked
	 * @return true if the player stands on a shrine, else false
	 */
	public boolean isOnShrine(Player player) {
		String world = player.getWorld().getName();		
		List<String> names = config.getKeys("shrines." +world);
		try {
			for (String name : names) {
				Location locOfShrine = new Location(player.getWorld(),
						config.getInt("shrines." +world +"." +name+".tb.x", 0),
						config.getInt("shrines." +world +"." +name+".tb.y", 0),
						config.getInt("shrines." +world +"." +name+".tb.z", 0));
				
				double dist = player.getLocation().distance(locOfShrine);
				if (dist < 2) {
					return true;
				}
			}
		}catch (NullPointerException e) {
			// TODO NullPointer: no shrines (isOnShrine)
			return false;
		}
		return false;	
	}
}
