package muCkk.DeathAndRebirth;

import java.io.File;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class DARShrines {
	
	private String shrinesFile, shrinesDir;
	private File yml; 
	Configuration config ;
	
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
		config.setProperty("shrines."+name+".max.x", shrine.getMax()[0]);
		config.setProperty("shrines."+name+".max.y", shrine.getMax()[1]);
		config.setProperty("shrines."+name+".max.z", shrine.getMax()[2]);
		
		config.setProperty("shrines."+name+".min.x", shrine.getMin()[0]);
		config.setProperty("shrines."+name+".min.y", shrine.getMin()[1]);
		config.setProperty("shrines."+name+".min.z", shrine.getMin()[2]);
		
		config.setProperty("shrines."+name+".tb.x", shrine.getTB()[0]);
		config.setProperty("shrines."+name+".tb.y", shrine.getTB()[1]);
		config.setProperty("shrines."+name+".tb.z", shrine.getTB()[2]);
		
		int[] ids = shrine.getIDs();
		for (int i = 0; i< ids.length; i++) {
			config.setProperty("shrines."+name+".originalids."+i, ids[i]);
		}
		config.save();
	}
	
	/**
	 * Shows a list of all shrines
	 * @param player
	 * @param page
	 */
	public void list(Player player, int page) {
		List<String> names = config.getKeys("shrines");
		int pages = names.size()/6;
		if(names.size()%6 != 0) pages += 1;
		
		player.sendMessage("List of shrines "+page+"/"+pages);
		for (int i=page-1; i<page*6 && i< names.size(); i++) {
			player.sendMessage(i+1 +". "+names.get(i));
		}
	}
	
	/**
	 * Checks if a shrine with the given name exists
	 * @param name
	 * @return
	 */
	public boolean exists(String name) {
		List<String> names = config.getKeys("shrines");
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
		int x = config.getInt("shrines."+name+".tb.x", 0),
			y = config.getInt("shrines."+name+".tb.y", 0),
			z = config.getInt("shrines."+name+".tb.z", 0);
		
		int[] ids = new int[12];
		for (int i = 0; i< ids.length; i++) {
			ids[i] = config.getInt("shrines."+name+".originalids."+i, 0);
		}
		Block tb = player.getWorld().getBlockAt(x, y, z);
		
		tb.setTypeId(ids[0]);
		tb.getRelative(BlockFace.UP, 1).setTypeId(ids[1]);
		tb.getRelative(BlockFace.UP, 2).setTypeId(ids[2]);
		tb.getRelative(BlockFace.UP, 3).setTypeId(ids[3]);
		tb.getRelative(BlockFace.NORTH_WEST, 1).setTypeId(ids[4]);
		tb.getRelative(BlockFace.NORTH, 1).setTypeId(ids[5]);
		tb.getRelative(BlockFace.NORTH_EAST, 1).setTypeId(ids[6]);
		tb.getRelative(BlockFace.SOUTH_WEST, 1).setTypeId(ids[7]);
		tb.getRelative(BlockFace.SOUTH, 1).setTypeId(ids[8]);
		tb.getRelative(BlockFace.SOUTH_EAST, 1).setTypeId(ids[9]);
		tb.getRelative(BlockFace.WEST, 1).setTypeId(ids[10]);
		tb.getRelative(BlockFace.EAST, 1).setTypeId(ids[11]);
		
		
		config.removeProperty("shrines."+name);
		config.save();
	}
	
	/**
	 * Returns the shrine near a player if there is one
	 * @param player
	 * @return DARShrine
	 */
	public String getClose(Player player) {
		Block pb = player.getLocation().getBlock();
		int px = pb.getX();
		int py = pb.getY();
		int pz = pb.getZ();
		int bx,by,bz, x,y,z;
		
		List<String> names = config.getKeys("shrines");
		try {
			for (String name : names) {
				bx = config.getInt("shrines."+name+".tb.x", 0);
				by = config.getInt("shrines."+name+".tb.y", 0) -5;
				bz = config.getInt("shrines."+name+".tb.z", 0);
				
				x = bx - px;
				y = by+12;
				z = bz - pz;
				
				if		(	Math.abs(x) < 6 
						&&	Math.abs(z) < 6 
						&&	by < py 
						&&	py < y) {
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
		int 	tx = config.getInt("shrines."+name+".tb.x", 0),
				ty = config.getInt("shrines."+name+".tb.y", 0),
				tz = config.getInt("shrines."+name+".tb.z", 0);
		
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
		int 	tx = config.getInt("shrines."+name+".tb.x", 0),
				ty = config.getInt("shrines."+name+".tb.y", 0),
				tz = config.getInt("shrines."+name+".tb.z", 0);
		
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
		Block pb = player.getLocation().getBlock();
		int px = pb.getX();
		int py = pb.getY();
		int pz = pb.getZ();
		int maxX, maxY, maxZ, minX,minY,minZ;
		
		try {
			List<String> names = config.getKeys("shrines");
			for (String name : names) {
				maxX = config.getInt("shrines."+name+".max.x", 0);
				maxY = config.getInt("shrines."+name+".max.y", 0);
				maxZ = config.getInt("shrines."+name+".max.z", 0);
				minX = config.getInt("shrines."+name+".min.x", 0);
				minY = config.getInt("shrines."+name+".min.y", 0);
				minZ = config.getInt("shrines."+name+".min.z", 0);
				
				if (	px <= maxX && px >= minX
					&&	py <= maxY && py >= minY
					&&	pz <= maxZ && pz >= minZ) {
						return true;
					}
			}
		}catch (NullPointerException e) {
			// TODO NullPointer: no shrines (isOnShrine)
		}
		return false;
	}
}
