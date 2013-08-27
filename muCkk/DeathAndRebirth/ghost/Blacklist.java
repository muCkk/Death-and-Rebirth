package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import muCkk.DeathAndRebirth.DAR;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Blacklist {
	
	private DAR plugin;
	private File blacklistFile;
	private FileConfiguration customConfig = null;
	
	public Blacklist(DAR instance) {
		this.plugin = instance;
		this.blacklistFile = new File(plugin.getDataFolder()+"/blacklist.yml");
	}
	
	public void reloadCustomConfig() {
	    if (blacklistFile == null) {
	    	blacklistFile = new File(plugin.getDataFolder(), "blacklist.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(blacklistFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("blacklist.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	        customConfig.options().copyDefaults(true);
	    }
	}
	
	public FileConfiguration getCustomConfig() {
	    if (customConfig == null) {
	        reloadCustomConfig();
	    }
	    return customConfig;
	}
	
	public void saveCustomConfig() {
	    if (customConfig == null || blacklistFile == null) {
	    	return;
	    }
	    try {
	        customConfig.save(blacklistFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + blacklistFile, ex);
	    }
	}
	
	public List<String> getList() {
		ConfigurationSection cfgsel = getCustomConfig().getConfigurationSection("blacklist");
		if (cfgsel == null)
			return null;
		return Arrays.asList(cfgsel.getKeys(false).toArray(new String[0]));
	}
	
	public boolean contains(int i) {
		List<String> list = getList();
		if (list == null) return false;
		return list.contains(Integer.toString(i));//yml.getIntList("blacklist", null).contains(i);
	}
}
