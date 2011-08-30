package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.util.List;

import muCkk.DeathAndRebirth.config.DARProperties;
import muCkk.DeathAndRebirth.messages.DARErrors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.util.config.Configuration;

public class DARGraves {
	
	private String dir;
	private File graveFile;
	private Configuration yml;
	private DARProperties config;
	
	public DARGraves(String dir, String fileName, DARProperties config) {
		this.dir = dir;
		graveFile = new File(fileName);
		this.config = config;
	}
	
	public void load() {
		if(!graveFile.exists()){
            try {
            	new File(dir).mkdir();
                graveFile.createNewFile(); 
            } catch (Exception ex) {
            	DARErrors.couldNotReadSignsFile();
            	ex.printStackTrace();
            }
        } else {
        	DARErrors.gravesLoaded();
        }
		try {
            yml = new Configuration(graveFile);
            yml.load();
        } catch (Exception e) {
        	DARErrors.couldNotReadSignsFile();
        	e.printStackTrace();
        }
	}
	
	public void save() {
		yml.save();
	}
	
//	public void addGrave(String name, int x, int y, int z, String l1, String l2, String world) {
//		
//		yml.setProperty("graves." +world +"." +name+".x", x);
//		yml.setProperty("graves." +world +"." +name+".y", y);
//		yml.setProperty("graves." +world +"." +name+".z", z);
//		yml.setProperty("graves." +world +"." +name+".l1", l1);
//		yml.setProperty("graves." +world +"." +name+".l2", l2);
//		
//		yml.save();
//	}
	public void addGrave(String name, Block block, String l1, String world) {
		if (config.isSignsEnabled()) placeSign(block, l1, name);
		
		yml.setProperty("graves." +world +"." +name+".x", block.getX());
		yml.setProperty("graves." +world +"." +name+".y", block.getY());
		yml.setProperty("graves." +world +"." +name+".z", block.getZ());
		yml.setProperty("graves." +world +"." +name+".l1", l1);
		yml.setProperty("graves." +world +"." +name+".l2", name);
		
		yml.save();
	}
	
	public void deleteGrave(Block block, String name, String worldName) {
		if (config.isSignsEnabled()) removeSign(block, name, worldName);
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
		block.setType(Material.SIGN_POST);
		Sign sign = (Sign) block.getState();
		sign.setLine(1, l1);
		sign.setLine(2, name);
		sign.update(true);
	}
	
	private void removeSign(Block block, String name, String worldName) {
		int id = yml.getInt("graves."+worldName+"."+name+".blockid", 0);
		block.setTypeId(id);
		if (id == 43 || id == 44 || id == 35) block.setData((byte)yml.getInt("graves."+worldName+"."+name+".blockdata", 0));
	}
	
//	public void deleteGrave(String name, String world) {
//		yml.removeProperty("graves." +world +"." +name);
//		yml.save();
//	}
	
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
