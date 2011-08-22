package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.util.List;

import muCkk.DeathAndRebirth.messages.DARErrors;

import org.bukkit.util.config.Configuration;

public class DARGraves {
	
	private String dir;
	private File graveFile;
	private Configuration yml;
	
	public DARGraves(String dir, String fileName) {
		this.dir = dir;
		graveFile = new File(fileName);
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
	
	public void addGrave(String name, int x, int y, int z, String l1, String l2, String world) {
		yml.setProperty("graves." +world +"." +name+".x", x);
		yml.setProperty("graves." +world +"." +name+".y", y);
		yml.setProperty("graves." +world +"." +name+".z", z);
		yml.setProperty("graves." +world +"." +name+".l1", l1);
		yml.setProperty("graves." +world +"." +name+".l2", l2);
		
		yml.save();
	}
	
	public void deleteGrave(String name, String world) {
		yml.removeProperty("graves." +world +"." +name);
		yml.save();
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
