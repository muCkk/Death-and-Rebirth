package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.util.List;

import muCkk.DeathAndRebirth.config.CFG;
import muCkk.DeathAndRebirth.config.Config;
import muCkk.DeathAndRebirth.messages.Errors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.util.config.Configuration;

public class Graves {
	
	private String dir;
	private File graveFile;
	private Configuration yml;
	private Config config;
	private Ghosts ghosts;
	
	public Graves(String dir, Config config) {
		this.dir = dir;
		graveFile = new File(dir+"/graves");
		this.config = config;
		load();
	}
	
	public void load() {
		if(!graveFile.exists()){
            try {
            	new File(dir).mkdir();
                graveFile.createNewFile(); 
            } catch (Exception ex) {
            	Errors.couldNotReadSignsFile();
            	ex.printStackTrace();
            }
        } else {
        	Errors.gravesLoaded();
        }
		try {
            yml = new Configuration(graveFile);
            yml.load();
        } catch (Exception e) {
        	Errors.couldNotReadSignsFile();
        	e.printStackTrace();
        }
	}
	
	public void save() {
		yml.save();
	}
	public void setGhosts(Ghosts ghosts) {
		this.ghosts = ghosts;
	}
	public void addGrave(String name, Block block, String l1, String world) {		
//		if (config.getBoolean(CFG.GRAVE_SIGNS)) placeSign(block, l1, name);
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		Block otherBlock;
		boolean placed = false;
		// water
		if (block.getTypeId() == 8 || block.getTypeId() == 9) {
			otherBlock = block.getRelative(BlockFace.UP);
			while (otherBlock.getTypeId() != 0) {
				otherBlock = otherBlock.getRelative(BlockFace.UP);
			}
			otherBlock.getRelative(BlockFace.DOWN).setTypeId(3);
			x = otherBlock.getX();
			y = otherBlock.getY();
			z = otherBlock.getZ();
			if (config.getBoolean(CFG.GRAVE_SIGNS)) {
				placeSign(otherBlock, l1, name);
				ghosts.setLocationOfDeath(otherBlock, name);
				placed = true;
			}
		}
//		else if (config.getBoolean(CFG.GRAVE_SIGNS)) placeSign(block, l1, name);
		
		// explosion
		if (block.getRelative(BlockFace.DOWN).getTypeId() == 0) {
			otherBlock = block.getRelative(BlockFace.DOWN);
			while (otherBlock.getRelative(BlockFace.DOWN).getTypeId() == 0) {
				otherBlock = otherBlock.getRelative(BlockFace.DOWN);
			}
			x = otherBlock.getX();
			y = otherBlock.getY();
			z = otherBlock.getZ();
			if (config.getBoolean(CFG.GRAVE_SIGNS)) {
				placeSign(otherBlock, l1, name);
				ghosts.setLocationOfDeath(otherBlock, name);
				placed= true;
			}
		}
		
		if (config.getBoolean(CFG.GRAVE_SIGNS) && !placed) {
			placeSign(block, l1, name);
			ghosts.setLocationOfDeath(block, name);
		}
			
		yml.setProperty("graves." +world +"." +name+".x", x);
		yml.setProperty("graves." +world +"." +name+".y", y);
		yml.setProperty("graves." +world +"." +name+".z", z);
		yml.setProperty("graves." +world +"." +name+".l1", l1);
		yml.setProperty("graves." +world +"." +name+".l2", name);
		
		yml.save();
	}
	
	public void deleteGrave(Block block, String name, String worldName) {
		// check for lava
		if (yml.getInt("graves."+block.getWorld().getName()+"."+name+".blockid", 0) != 10 && yml.getInt("graves."+block.getWorld().getName()+"."+name+".blockid", 0) != 11) {
			if (config.getBoolean(CFG.GRAVE_SIGNS)) removeSign(block, name, worldName);
		}
		yml.removeProperty("graves." +worldName +"." +name);
		yml.save();		
	}
	
	private void placeSign(Block block, String l1, String name) {
		int id = block.getTypeId();
		yml.setProperty("graves."+block.getWorld().getName()+"."+name+".blockid", id);
		if (id == 43 || id == 44 || id == 35) {
			int data = (int) block.getData();
			yml.setProperty("graves."+block.getWorld().getName()+"."+name+".blockdata", data);
		}
		//avoid placing signs in lava
		if(id == 10 || id == 11) return;
		
		block.setType(Material.SIGN_POST);
		Sign sign = (Sign) block.getState();
		sign.setLine(1, l1);
		sign.setLine(2, name);
		sign.update(true);
	}
	
	private void removeSign(Block block, String name, String worldName) {
		int id = yml.getInt("graves."+worldName+"."+name+".blockid", 0);
		int x =  yml.getInt("graves."+worldName+"."+name+".x", block.getX());
		int y =  yml.getInt("graves."+worldName+"."+name+".y", block.getY());
		int z =  yml.getInt("graves."+worldName+"."+name+".z", block.getZ());
//		block.setTypeId(id);
		block.getWorld().getBlockAt(x, y, z).setTypeId(id);
		if (id == 43 || id == 44 || id == 35) block.setData((byte)yml.getInt("graves."+worldName+"."+name+".blockdata", 0));
	}
	
	public boolean isProtected(String name, String world, int x, int y, int z) {
		List<String> graves = yml.getKeys("graves." +world);
		try {
			for (String grave : graves) {
				if(grave.equalsIgnoreCase(name)
						&& yml.getInt("graves." +world +"." +grave+".x", 0) == x
						&&	yml.getInt("graves." +world +"." +grave+".y", 0) == y
						&&	yml.getInt("graves." +world +"." +grave+".z", 0) == z)
					
						return true;
			}
		}catch (NullPointerException e) {
			return false;
		}
		return false;		
	}
}
