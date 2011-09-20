package muCkk.DeathAndRebirth.config;

import java.io.File;
import java.util.List;

import muCkk.DeathAndRebirth.messages.Errors;

import org.bukkit.util.config.Configuration;

public class Blacklist {
	
	private String dir;
	private File blacklistFile;
	private Configuration yml;
	
	public Blacklist(String dir) {
		this.dir = dir;
		this.blacklistFile = new File(dir+"/blacklist.yml");
		load();
	}
	public void load() {
		if(!blacklistFile.exists()){
            try {
            	new File(dir).mkdir();
                blacklistFile.createNewFile(); 
            } catch (Exception e) {
            	Errors.couldNotReadFile("Blacklist");
            	e.printStackTrace();
            }
        } else {
        	// loaded
        }
		try {
            yml = new Configuration(blacklistFile);
            yml.load();
        } catch (Exception e) {
        	Errors.couldNotReadFile("Blacklist");
        	e.printStackTrace();
        }
	}

	/**
	 * Saves the current information to a file
	 */
	public void save() {
		yml.save();
	}
	
	public List<String> getList() {
		return yml.getKeys("blacklist");
	}
	
	public boolean contains(int i) {
		return yml.getIntList("blacklist", null).contains(i);
	}
}
