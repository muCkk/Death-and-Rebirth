package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.config.Blacklist;

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
		this.blacklist = new Blacklist(dir2);
	}
	
	public void reloadCustomConfig() {
	    if (dropsFile == null) {
	    	dropsFile = new File(plugin.getDataFolder(), "customConfig.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(dropsFile);
	 
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
		
		// normal inventory
		for(int slot=0; slot<36; slot++) {
			ItemStack item = inv.getItem(slot);
			if (item == null || item.getTypeId() == 0) continue;
			
			Short dura = item.getDurability();			
			getCustomConfig().set("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".id", item.getTypeId());
			getCustomConfig().set("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".amount", item.getAmount());
			getCustomConfig().set("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".durability", dura.intValue());			
		}
		// armor
		ItemStack [] armorParts = { inv.getHelmet(), inv.getChestplate(), inv.getLeggings(), inv.getBoots() };
		String slot = "If you see this contact the author of the plugin. Thanks.";
		for (int i=0; i< armorParts.length; i++) {
			if (armorParts[i] == null || armorParts[i].getTypeId() == 0) {
				continue;
			}
			
			if (i == 0) slot = "helmet";
			if (i == 1) slot = "chest";
			if (i == 2) slot = "leggings";
			if (i == 3) slot = "boots";
			Short dura = armorParts[i].getDurability();			
			getCustomConfig().set("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".id", armorParts[i].getTypeId());
			getCustomConfig().set("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".durability", dura.intValue());			
		}
		saveCustomConfig();
	}
	
	/**
	 * Gives the player his inventory he had on death
	 * @param player
	 */
	public void givePlayerInv(Player player) {
		PlayerInventory inv = player.getInventory();
		String playerName = player.getName();
		String worldName = player.getWorld().getName();
		Set<String> slots = getCustomConfig().getConfigurationSection("drops."+playerName+"."+worldName).getKeys(false);
		if (slots == null) return;
		Integer amount, durability;
		
		for (String slot : slots) {
			if (slot.equalsIgnoreCase("helmet") || slot.equalsIgnoreCase("chest") || slot.equalsIgnoreCase("leggings") || slot.equalsIgnoreCase("boots")) continue;
			amount = getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".amount", 1);
			if (amount == 0) continue;
			ItemStack item = new ItemStack(getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".id", 1));
			durability = getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".durability", 1);
			
			item.setAmount(amount);
			item.setDurability(durability.shortValue());
			inv.setItem(Integer.parseInt(slot), item);
		}
		
		// armor
		if(getCustomConfig().getConfigurationSection("drops."+playerName+"."+player.getWorld().getName()+".helmet") != null) {
			ItemStack helmet = new ItemStack(getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+".helmet.id", 298));
			Integer dura = getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+".helmet.durability", 1);
			helmet.setDurability(dura.shortValue());
			inv.setHelmet(helmet);
		}
		if(getCustomConfig().getConfigurationSection("drops."+playerName+"."+player.getWorld().getName()+".chest") != null) {
			ItemStack chest = new ItemStack(getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+".chest.id", 298));
			Integer dura = getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+".chest.durability", 1);
			chest.setDurability(dura.shortValue());
			inv.setChestplate(chest);
		}
		if(getCustomConfig().getConfigurationSection("drops."+playerName+"."+player.getWorld().getName()+".leggings") != null) {
			ItemStack leggings = new ItemStack(getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+".leggings.id", 298));
			Integer dura = getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+".leggings.durability", 1);
			leggings.setDurability(dura.shortValue());
			inv.setLeggings(leggings);
		}
		if(getCustomConfig().getConfigurationSection("drops."+playerName+"."+player.getWorld().getName()+".boots") != null) {
			ItemStack boots = new ItemStack(getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+".boots.id", 298));
			Integer dura = getCustomConfig().getInt("drops."+playerName+"."+player.getWorld().getName()+".boots.durability", 1);
			boots.setDurability(dura.shortValue());
			inv.setBoots(boots);
		}
		remove(player);
		saveCustomConfig();
	}
	
	public void selfResPunish(Player player) {
		int percent = plugin.getConfig().getInt("PERCENT");
		if(percent == 0) return;
		
		String playerName = player.getName();
		String worldName = player.getWorld().getName();
		Set<String> slots1 = getCustomConfig().getConfigurationSection("drops."+playerName+"."+worldName).getKeys(false);
		if (slots1 == null) return;
		List<Object> slots = Arrays.asList(slots1.toArray());
		int size = slots1.size();
		int r, stopper;
		int counter = (size/100)*percent;
		if (counter < 1) counter = 1;
		Random rand = new Random();
		
		while (counter >0) {
			r = rand.nextInt(size);
			stopper = 0;
			
			while (stopper < 20 || blacklist.contains(getCustomConfig().getInt("drops."+playerName+"."+worldName+"."+slots.get(r)+".id", 0))) {
				r = rand.nextInt(size);
				stopper++;
			}
			if(!blacklist.contains(getCustomConfig().getInt("drops."+playerName+"."+worldName+"."+slots.get(r)+".id", 0))) {
				getCustomConfig().set("drops."+playerName+"."+worldName+"."+slots.get(r), null);
			}
			counter--;
		}
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
