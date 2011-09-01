package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import muCkk.DeathAndRebirth.messages.DARErrors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

public class DARDrops {

	private String dir;
	private File dropsFile;
	private Configuration yml;
	
	
	public DARDrops(String dir) {
		this.dir = dir;
		this.dropsFile = new File(dir+"/drops");
		load();
	}
	
	public void load() {
		if(!dropsFile.exists()){
            try {
            	new File(dir).mkdir();
                dropsFile.createNewFile(); 
            } catch (Exception e) {
            	DARErrors.couldNotReadDropsFile();
            	e.printStackTrace();
            }
        } else {
        	DARErrors.gravesLoaded();
        }
		try {
            yml = new Configuration(dropsFile);
            yml.load();
        } catch (Exception e) {
        	DARErrors.couldNotReadDropsFile();
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
	public void put(Player player, ItemStack [] drops) {
		String playerName = player.getName();
		for (ItemStack item : drops) {
			if (item == null) continue;
			yml.setProperty("drops."+playerName+"."+player.getWorld().getName()+"."+item.getTypeId(), item.getAmount());
		}
		yml.save();
	}
	
	/**
	 * Returns all drops on a player as an array of the type ItemStack
	 * @param player
	 * @return ItemStack []
	 */
	public ItemStack[] get(Player player) {
		String playerName = player.getName();
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		
		List<String> ids = yml.getKeys("drops."+playerName+"."+player.getWorld().getName());
		int id, amount;
		for (String sid : ids) {
			id = Integer.parseInt(sid);
			amount = yml.getInt("drops."+playerName+"."+player.getWorld().getName()+"."+sid, 1);
			list.add(new ItemStack(id, amount));
		}
		ItemStack [] array = new ItemStack[list.size()];
		list.toArray(array);
		return array;
	}
	
	/**
	 * Deletes all drops from a player
	 * @param player
	 */
	public void remove(Player player) {
		yml.removeProperty("drops."+player.getName()+"."+player.getWorld().getName());
		yml.save();
	}
	
	public void removeID(Player player, int id) {
		yml.removeProperty("drops."+player.getName()+"."+player.getWorld().getName()+"."+id);
	}
}
