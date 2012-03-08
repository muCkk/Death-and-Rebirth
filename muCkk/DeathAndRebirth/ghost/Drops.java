package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.tools.Blacklist;
import muCkk.DeathAndRebirth.tools.DARArmor;
import muCkk.DeathAndRebirth.tools.DARInventory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Drops {

	private FileConfiguration customConfig = null;
	private File dropsFile;
	private DAR plugin;
	private Blacklist blacklist;
	
	public Drops(DAR instance, String dir2) {
		this.plugin = instance;
		this.dropsFile = new File(dir2+"/drops");
		this.blacklist = new Blacklist(instance);
	}
	
	public void reloadCustomConfig() {
	    if (dropsFile == null) {
	    	dropsFile = new File(plugin.getDataFolder(), "drops");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(dropsFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("drops");
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
	    if (customConfig == null || dropsFile == null) {
	    	return;
	    }
	    try {
	        customConfig.save(dropsFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + dropsFile, ex);
	    }
	}
	
	/**
	 * Saves the drops of a player
	 * @param player
	 * @param drops
	 */
	public void put(Player player, PlayerInventory inv) {
		String playerName = player.getName();
		
		DARInventory darinv = new DARInventory(inv);
		getCustomConfig().set("drops."+playerName+"."+player.getWorld().getName()+".inventory", darinv);
		
		DARArmor dararmor = new DARArmor(inv);
		getCustomConfig().set("drops."+playerName+"."+player.getWorld().getName()+".armor", dararmor);
		
		saveCustomConfig();
	}
	
	/**
	 * Gives the player his inventory he had on death
	 * @param player
	 */
	public void givePlayerInv(Player player) {
		String playerName = player.getName();
		String worldName = player.getWorld().getName();
		ConfigurationSection cfgsel = getCustomConfig().getConfigurationSection("drops."+playerName+"."+worldName);
		if(cfgsel == null) return;
		
		DARInventory darinv = (DARInventory) getCustomConfig().get("drops."+playerName+"."+player.getWorld().getName()+".inventory");
		player.getInventory().setContents(darinv.getContents());
		
		DARArmor dararmor = (DARArmor) getCustomConfig().get("drops."+playerName+"."+player.getWorld().getName()+".armor");
		player.getInventory().setArmorContents(dararmor.getArmor());
		
		remove(player);
		saveCustomConfig();
	}
	
	//TODO 
	public void selfResPunish(Player player) {
		int percent = plugin.getConfig().getInt("PERCENT");
		if(percent == 0) return;
		
		String playerName = player.getName();
		
		DARInventory darinv = (DARInventory) getCustomConfig().get("drops."+playerName+"."+player.getWorld().getName()+".inventory");
		ItemStack [] daritems = darinv.getContents();
		
		int size = 0;
		
		for(int i=0; i<daritems.length; i++) {
			if (daritems[i] == null) 
				continue;
			size++;
			
		}
		ItemStack [] items = new ItemStack[size];
		int cnt = 0;
		for(int i=0; i<daritems.length; i++) {
			if (daritems[i] == null) 
				continue;
			items[cnt++] = daritems[i];
		}
		
		int r, stopper;
		int counter = (size/100)*percent;
		if (counter < 1) counter = 1;
		Random rand = new Random();
		
		while (counter >0) {
			r = rand.nextInt(size);
			stopper = 0;
			
			while (stopper < 20 || blacklist.contains(new Integer(items[r].getTypeId()))) {
				r = rand.nextInt(size);
				stopper++;
			}
			if(!blacklist.contains(new Integer(items[r].getTypeId()))) {
				items[r] = null;
			}
			counter--;
		}
		getCustomConfig().set("drops."+playerName+"."+player.getWorld().getName()+".inventory", new DARInventory(items));
	}
	

	/**
	 * Deletes all drops from a player
	 * @param player
	 */
	public void remove(Player player) {
		if(player == null) {
			System.out.println("[Death and Rebirth] Error - Remove Players Inventory From Database");
			return;
		}
		getCustomConfig().set("drops."+player.getName()+"."+player.getWorld().getName(), null);
		saveCustomConfig();
	}
}
