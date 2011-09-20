package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.util.List;
import java.util.Random;

import muCkk.DeathAndRebirth.config.Blacklist;
import muCkk.DeathAndRebirth.config.CFG;
import muCkk.DeathAndRebirth.config.Config;
import muCkk.DeathAndRebirth.messages.Errors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;

public class Drops {

	private String dir;
	private File dropsFile;
	private Configuration yml;
	private Config config;
	private Blacklist blacklist;
	
	public Drops(String dir, Config config) {
		this.dir = dir;
		this.config = config;
		this.dropsFile = new File(dir+"/drops");
		this.blacklist = new Blacklist(dir);
		load();
	}
	
	public void load() {
		blacklist.load();
		if(!dropsFile.exists()){
            try {
            	new File(dir).mkdir();
                dropsFile.createNewFile(); 
            } catch (Exception e) {
            	Errors.couldNotReadDropsFile();
            	e.printStackTrace();
            }
        } else {
        	// loaded
        }
		try {
            yml = new Configuration(dropsFile);
            yml.load();
        } catch (Exception e) {
        	Errors.couldNotReadDropsFile();
        	e.printStackTrace();
        }
	}

	/**
	 * Saves the current information to a file
	 */
	public void save() {
		yml.save();
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
			yml.setProperty("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".id", item.getTypeId());
			yml.setProperty("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".amount", item.getAmount());
			yml.setProperty("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".durability", dura.intValue());			
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
			yml.setProperty("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".id", armorParts[i].getTypeId());
			yml.setProperty("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".durability", dura.intValue());			
		}
		yml.save();
	}
	
	/**
	 * Gives the player his inventory he had on death
	 * @param player
	 */
	public void givePlayerInv(Player player) {
		PlayerInventory inv = player.getInventory();
		String playerName = player.getName();
		String worldName = player.getWorld().getName();
		List<String> slots = yml.getKeys("drops."+playerName+"."+worldName);
		if (slots == null) return;
		Integer amount, durability;
		
		for (String slot : slots) {
			if (slot.equalsIgnoreCase("helmet") || slot.equalsIgnoreCase("chest") || slot.equalsIgnoreCase("leggings") || slot.equalsIgnoreCase("boots")) continue;
			amount = yml.getInt("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".amount", 1);
			if (amount == 0) continue;
			ItemStack item = new ItemStack(yml.getInt("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".id", 1));
			durability = yml.getInt("drops."+playerName+"."+player.getWorld().getName()+"."+slot+".durability", 1);
			
			item.setAmount(amount);
			item.setDurability(durability.shortValue());
			inv.setItem(Integer.parseInt(slot), item);
		}
		
		// armor
		if(yml.getProperty("drops."+playerName+"."+player.getWorld().getName()+".helmet") != null) {
			ItemStack helmet = new ItemStack(yml.getInt("drops."+playerName+"."+player.getWorld().getName()+".helmet.id", 298));
			Integer dura = yml.getInt("drops."+playerName+"."+player.getWorld().getName()+".helmet.durability", 1);
			helmet.setDurability(dura.shortValue());
			inv.setHelmet(helmet);
		}
		if(yml.getProperty("drops."+playerName+"."+player.getWorld().getName()+".chest") != null) {
			ItemStack chest = new ItemStack(yml.getInt("drops."+playerName+"."+player.getWorld().getName()+".chest.id", 298));
			Integer dura = yml.getInt("drops."+playerName+"."+player.getWorld().getName()+".chest.durability", 1);
			chest.setDurability(dura.shortValue());
			inv.setChestplate(chest);
		}
		if(yml.getProperty("drops."+playerName+"."+player.getWorld().getName()+".leggings") != null) {
			ItemStack leggings = new ItemStack(yml.getInt("drops."+playerName+"."+player.getWorld().getName()+".leggings.id", 298));
			Integer dura = yml.getInt("drops."+playerName+"."+player.getWorld().getName()+".leggings.durability", 1);
			leggings.setDurability(dura.shortValue());
			inv.setLeggings(leggings);
		}
		if(yml.getProperty("drops."+playerName+"."+player.getWorld().getName()+".boots") != null) {
			ItemStack boots = new ItemStack(yml.getInt("drops."+playerName+"."+player.getWorld().getName()+".boots.id", 298));
			Integer dura = yml.getInt("drops."+playerName+"."+player.getWorld().getName()+".boots.durability", 1);
			boots.setDurability(dura.shortValue());
			inv.setBoots(boots);
		}
		remove(player);
		yml.save();
	}
	
	public void selfResPunish(Player player) {
		int percent = config.getInt(CFG.PERCENT);
		if(percent == 0) return;
		
		String playerName = player.getName();
		String worldName = player.getWorld().getName();
		List<String> slots = yml.getKeys("drops."+playerName+"."+worldName);
		if (slots == null) return;
		int size = slots.size();
		int r, stopper;
		int counter = (size/100)*percent;
		if (counter < 1) counter = 1;
		Random rand = new Random();
		
		while (counter >0) {
			r = rand.nextInt(size);
			stopper = 0;
			
			while (stopper < 20 || blacklist.contains(yml.getInt("drops."+playerName+"."+worldName+"."+slots.get(r)+".id", 0))) {
				r = rand.nextInt(size);
				stopper++;
			}
			if(!blacklist.contains(yml.getInt("drops."+playerName+"."+worldName+"."+slots.get(r)+".id", 0))) {
				yml.removeProperty("drops."+playerName+"."+worldName+"."+slots.get(r));
			}
			counter--;
		}
	}
	

	/**
	 * Deletes all drops from a player
	 * @param player
	 */
	public void remove(Player player) {
		yml.removeProperty("drops."+player.getName()+"."+player.getWorld().getName());
		yml.save();
	}
}
