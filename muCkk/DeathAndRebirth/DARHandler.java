package muCkk.DeathAndRebirth;

import java.io.File;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

public class DARHandler {

	private String dir;
	private File ghostsFile;
	private DARProperties config;
	private DARGraves graves;
	private Configuration yml;
	
	public DARHandler(String dir, String fileName, DARProperties config, DARGraves graves) {
		this.dir = dir;
		this.ghostsFile = new File(fileName);
		this.config = config;
		this.graves = graves;
	}
	
	/**
	 * Loads saved data from a file
	 */
	public void load() {
		if(!ghostsFile.exists()){
            try {
            	new File(dir).mkdir();
                ghostsFile.createNewFile(); 
            } catch (Exception e) {
            	//TODO: exception
            	e.printStackTrace();
            }
        } else {
        	DARErrors.gravesLoaded();
        }
		try {
            yml = new Configuration(ghostsFile);
            yml.load();
        } catch (Exception e) {
        	//TODO exception
        	e.printStackTrace();
        }
	}

	/**
	 * Saves the current information to a file
	 */
	public void save() {
		yml.save();
	}
	
	public void newPlayer(Player player) {
		if(existsPlayer(player)) {
			return;
		}
		String pname = player.getName();
		
		yml.setProperty("players." +pname +".dead", false);
		yml.setProperty("players." +pname +".location.x", player.getLocation().getBlockX());
		yml.setProperty("players." +pname +".location.y", player.getLocation().getBlockY());
		yml.setProperty("players." +pname +".location.z", player.getLocation().getBlockZ());
		yml.setProperty("players." +pname +".world", player.getWorld().getName());
		
		yml.save();
	}
	
	
	
	public boolean existsPlayer(Player player) {
		List<String> names = yml.getKeys("players");
		String pname = player.getName();
		
		try {
			for (String name : names) {
				if(name.equalsIgnoreCase(pname)) {
					return true;
				}
			}
		}catch (NullPointerException e) {
			// TODO NullPointer: no ghosts (existsPlayer)
			return false;
		}
		return false;
	}
	
	/**
	 * Checks if a player is dead
	 * @param player which is checked
	 * @return The state of the player (dead/alive).
	 */
	public boolean isGhost(Player player) {
		String pname = player.getName();		
		try {
			return yml.getBoolean("players."+pname+".dead", false);	
		}catch (NullPointerException e) {
			return false;
		}		
	}
	
	public void died(Player player) {
		String pname = player.getName();
		yml.setProperty("players."+pname+".dead", true);
		Block block = player.getWorld().getBlockAt(player.getLocation());
		yml.setProperty("players."+pname+".location.x", block.getX());
		yml.setProperty("players."+pname+".location.y", block.getY());
		yml.setProperty("players."+pname+".location.z", block.getZ());		
		player.setDisplayName("Ghost of "+player.getName());
				
		Location location = player.getLocation();
		location.getBlock().setType(Material.SIGN_POST);
		Sign sign = (Sign) location.getBlock().getState();
		String l1 = "R.I.P";
		sign.setLine(1, l1);
		sign.setLine(2, pname);
		sign.update(true);
		graves.addGrave(pname,block.getX(), block.getY(), block.getZ(), l1, pname);
	}
	
	public void resurrect(Player player) {
		String pname = player.getName();
		yml.setProperty("players."+pname+".dead", false);
		player.getWorld().getBlockAt(getLocation(player)).setType(Material.AIR);
		
		graves.deleteGrave(pname);
		DARMessages.youWereReborn(player);
		player.setDisplayName(pname);
	}
	
	public void resurrect(Player player, Player target) {
		// *** check distance ***
		Double distance = player.getLocation().distance(target.getLocation());
		if(distance > config.getInteger("distance")) {
			DARMessages.tooFarAway(player);
			return;
		}
		
		// *** check items ***
		if (config.getBoolean("needItem")) {
			int itemID = config.getInteger("itemID");
			int amount = config.getInteger("amount");
			
			ItemStack costStack = new ItemStack(itemID);
			costStack.setAmount(amount);
			
			if(!ConsumeItems(player, costStack)) {
				player.sendMessage("You need "+amount +" "+Material.getMaterial(itemID).name() +" to resurrect someone.");
				return;
			}
		}		
		resurrect(target);
		DARMessages.youResurrected(player, target);
		target.teleport(getLocation(player));
		
	}
	public Location getLocation(Player player) {
		String pname = player.getName();
		World world = player.getWorld();
		double x = yml.getDouble("players."+pname+".location.x", 0);
		double y = yml.getDouble("players."+pname+".location.y", 64);
		double z = yml.getDouble("players."+pname+".location.z", 0);
		Location loc = new Location(world, x, y, z);
		return loc;
	}

	// **************************************************************
	// *** code from DwarfCraft, found on the bukkit forum - THX! ***
	// **************************************************************	
	private boolean CheckItems(Player player, ItemStack costStack)
    {
        //make sure we have enough
        int cost = costStack.getAmount();
        boolean hasEnough=false;
        for (ItemStack invStack : player.getInventory().getContents())
        {
            if(invStack == null)
                continue;
            if (invStack.getTypeId() == costStack.getTypeId()) {

                int inv = invStack.getAmount();
                if (cost - inv >= 0) {
                    cost = cost - inv;
                } else {
                    hasEnough=true;
                    break;
                }
            }
        }
        return hasEnough;
    }
    private boolean ConsumeItems(Player player, ItemStack costStack)
    {
        if (!CheckItems(player,costStack)) return false;
        //Loop though each item and consume as needed. We should of already
        //checked to make sure we had enough with CheckItems.
        for (ItemStack invStack : player.getInventory().getContents())
        {
            if(invStack == null)
                continue;

            if (invStack.getTypeId() == costStack.getTypeId()) {
                int inv = invStack.getAmount();
                int cost = costStack.getAmount();
                if (cost - inv >= 0) {
                    costStack.setAmount(cost - inv);
                    player.getInventory().remove(invStack);
                } else {
                    costStack.setAmount(0);
                    invStack.setAmount(inv - cost);
                    break;
                }
            }
        }
        return true;
    }

    /**
     * binds the players soul to a shrine
     * @param player
     */
	public void bindSoul(Player player) {
		String pname = player.getName();
		Location loc = player.getLocation();
		yml.setProperty("players." +pname +".shrine.x", loc.getX());
		yml.setProperty("players." +pname +".shrine.y", loc.getY());
		yml.setProperty("players." +pname +".shrine.z", loc.getZ());
	}
	
	public Location getBoundShrine(Player player) {
		String pname = player.getName();
		World world = player.getWorld();
		
		// *** check if a shrine is saved ***
		Object doesShrineExists = yml.getProperty("players."+pname +".shrine.x");
		if (doesShrineExists == null) {
			return null;
		}
		
		double x = yml.getDouble("players."+pname+".shrine.x", 0);
		double y = yml.getDouble("players."+pname+".shrine.y", 64);
		double z = yml.getDouble("players."+pname+".shrine.z", 0);
		Location loc = new Location(world, x, y, z);	
		return loc;
	}
}
