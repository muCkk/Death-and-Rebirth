package muCkk.DeathAndRebirth;

import java.io.File;
import java.util.List;

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
            	//TODO exception
            }
        } else {
        	DARErrors.gravesLoaded();
        }
		try {
            yml = new Configuration(graveFile);
            yml.load();
        } catch (Exception e) {
        	//TODO exception
        }
	}
	
	public void save() {
		yml.save();
	}
	
	public void addGrave(String name, int x, int y, int z, String l1, String l2) {
		yml.setProperty("graves."+name+".x", x);
		yml.setProperty("graves."+name+".y", y);
		yml.setProperty("graves."+name+".z", z);
		yml.setProperty("graves."+name+".l1", l1);
		yml.setProperty("graves."+name+".l2", l2);
		
		yml.save();
	}
	
	public void deleteGrave(String name) {
		yml.removeProperty("graves."+name);
		yml.save();
	}
	
	public boolean isProtected(String name, int x, int y, int z) {
		List<String> graves = yml.getKeys("graves");
		try {
			for (String grave : graves) {
				if(grave.equalsIgnoreCase(name)
						&& yml.getInt("graves."+grave+".x", 0) == x
						&&	yml.getInt("graves."+grave+".y", 0) == y
						&&	yml.getInt("graves."+grave+".z", 0) == z)
					
						return true;
			}
		}catch (NullPointerException e) {
			// TODO NullPointer: no graves (isProtected)
			return false;
		}
		return false;		
	}
}
