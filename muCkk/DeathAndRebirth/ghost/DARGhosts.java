package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.config.DARProperties;
import muCkk.DeathAndRebirth.messages.DARErrors;
import muCkk.DeathAndRebirth.messages.DARMessages;
import muCkk.DeathAndRebirth.messages.Messages;
import muCkk.DeathAndRebirth.otherPlugins.Perms;
import muCkk.DeathAndRebirth.otherPlugins.Spout;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;

public class DARGhosts {

	private String dir;
	private File ghostsFile;
	private HashMap<String, ItemStack[]> dropManager;
	
	private DARProperties config;
	private DARGraves graves;
	private Spout spout;
	private DARMessages message;
	private DARDrops dardrops;
	
	private Configuration yml;
	
	public DARGhosts(String dir, String fileName, DARProperties config, DARGraves graves, Spout spout, DARMessages message) {
		this.dir = dir;
		this.ghostsFile = new File(fileName);
		this.config = config;
		this.graves = graves;
		this.spout = spout;
		this.message = message;
		this.dropManager = new HashMap<String, ItemStack[]>();
		this.dardrops = new DARDrops(dir);
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
            	DARErrors.couldNotReadGhostFile();
            	e.printStackTrace();
            }
        } else {
        	DARErrors.gravesLoaded();
        }
		try {
            yml = new Configuration(ghostsFile);
            yml.load();
        } catch (Exception e) {
        	DARErrors.couldNotReadGhostFile();
        	e.printStackTrace();
        }
	}

	/**
	 * Saves the current information to a file
	 */
	public void save() {
		yml.save();
		dardrops.save();
	}
	
	/**
	 * Adds a new player to the ghost file
	 * @param player that will be added
	 */
	public void newPlayer(Player player) {
		if(existsPlayer(player)) {
			return;
		}
		String pname = player.getName();
		String world = player.getWorld().getName();
				
		yml.setProperty("players." +pname +"."+world +".dead", false);
		yml.setProperty("players." +pname +"."+world +".location.x", player.getLocation().getBlockX());
		yml.setProperty("players." +pname +"."+world +".location.y", player.getLocation().getBlockY());
		yml.setProperty("players." +pname +"."+world +".location.z", player.getLocation().getBlockZ());
		yml.setProperty("players." +pname +"."+world +".world", world);
		
		yml.save();
	}
	
	/**
	 * Checks if the player is already listed in the ghosts file	
	 * @param player to be checked
	 * @return boolean
	 */
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
		if(player == null) {
			return false;
		}
		String pname = player.getName();
		String world = player.getWorld().getName();
		
		try {
			return yml.getBoolean("players."+pname +"."+world +".dead", false);	
		}catch (NullPointerException e) {
			return false;
		}		
	}
	
	/**
	 * Manages the death of players.
	 * @param player which died
	 */
	public void died(Player player, ItemStack [] drops) {
		String pname = player.getName();
		String world = player.getWorld().getName();
		
		yml.setProperty("players."+pname +"."+world +".dead", true);
		
		Block block = player.getWorld().getBlockAt(player.getLocation());
		
		if (config.isLightningDEnabled()) {
			player.getWorld().strikeLightningEffect(player.getLocation());
		}
		
		yml.setProperty("players."+pname +"."+world +".location.x", block.getX());
		yml.setProperty("players."+pname +"."+world +".location.y", block.getY());
		yml.setProperty("players."+pname +"."+world +".location.z", block.getZ());		
		save();
		
	// drop-management
		
		if (!config.isDroppingEnabled()) {
			dropManager.put(pname, drops);
			dardrops.add(player, drops);  
		}
		else if(Perms.hasPermission(player, "dar.nodrop")) {
			dropManager.put(pname, drops);
			dardrops.add(player, drops);
		}
		
		
	// change the displayname
		setDisplayName(player);
		
	// spout related
		if (config.isSpoutEnabled()) {
			spout.playerDied(player, config.getDeathSound());
		}

	// grave related
		String l1 = "R.I.P";
//		if (config.isSignsEnabled()) {
//			Location location = player.getLocation();
//			location.getBlock().setType(Material.SIGN_POST);
//			Sign sign = (Sign) location.getBlock().getState();
//			sign.setLine(1, l1);
//			sign.setLine(2, pname);
//			sign.update(true);
//		}
		graves.addGrave(pname,block, l1, world);
//		graves.addGrave(pname,block.getX(), block.getY(), block.getZ(), l1, pname, world);
	}
	
	public String getGhostDisplayName(Player player) {
		String newName = config.getGhostName().replace("%player", player.getName());
		return newName;
	}
	/**
	 * Brings players back to life
	 * @param player to be resurrected
	 */
	public void resurrect(final Player player) {
		String pname = player.getName();
		String world = player.getWorld().getName();
		
		yml.setProperty("players."+pname +"."+world +".dead", false);
//		if (config.isSignsEnabled())	player.getWorld().getBlockAt(getLocation(player)).setType(Material.AIR);
		graves.deleteGrave(player.getWorld().getBlockAt(getLocation(player)), pname, world);
		message.send(player, Messages.reborn);
		
	// check if lightning is enabled
		if (config.isLightningREnabled()) {
			player.getWorld().strikeLightningEffect(player.getLocation());
		}
		
		player.setDisplayName(pname);
		
	// spout related
		if (config.isSpoutEnabled()) {
			spout.playerRes(player, config.getResSound());
		}
		save();
		
			new Thread() {
				@Override
				public void run() {				
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.out.println("[Death and Rebirth] Error: Could not sleep while giving drops.");
						e.printStackTrace();
					}
					if (!config.isDroppingEnabled()) giveDrops(player);
					else if (Perms.hasPermission(player, "dar.nodrop"))	giveDrops(player);
				}
			}.start();
		
	}
	
	
	/**
	 * Called when a player tries to resurrect someone.
	 * @param player who tries to resurrect someone
	 * @param target player to be resurrected
	 */
	public void resurrect(Player player, Player target) {
		// *** check distance ***
		double distance = player.getLocation().distance(target.getLocation());
		if(distance > config.getInteger("distance")) {
			message.send(player, Messages.tooFarAway);
			return;
		}
		
		// check for items
		if (config.getBoolean("needItem")) {
			int itemID = config.getInteger("itemID");
			int amount = config.getInteger("amount");
			
			ItemStack costStack = new ItemStack(itemID);
			costStack.setAmount(amount);
			
			if(!ConsumeItems(player, costStack)) {
				message.sendChat(player, Messages.notEnoughItems, " "+amount +" "+Material.getMaterial(itemID).name());
				return;
			}
		}		
		resurrect(target);
		message.send(player, Messages.resurrected);
		target.teleport(getLocation(target));
		
		// spout related
		if (config.isSpoutEnabled()) {
			spout.playResSound(player, config.getResSound());
		}		
	}
	
	/**
	 * The location of death
	 * @param player which is checked
	 * @return Location of death
	 */
	public Location getLocation(Player player) {
		String pname = player.getName();
		World world = player.getWorld();
		String worldName = world.getName();
		
		double x = yml.getDouble("players."+pname +"."+worldName +".location.x", 0);
		double y = yml.getDouble("players."+pname +"."+worldName +".location.y", 64);
		double z = yml.getDouble("players."+pname +"."+worldName +".location.z", 0);
		Location loc = new Location(world, x, y, z);
		return loc;
	}

	

    /**
     * binds the players soul to a shrine
     * @param player
     */
	public void bindSoul(Player player) {
		String pname = player.getName();
		String world = player.getWorld().getName();
		Location loc = player.getLocation();
		
		yml.setProperty("players." +pname +"."+world +".shrine.x", loc.getX());
		yml.setProperty("players." +pname +"."+world +".shrine.y", loc.getY());
		yml.setProperty("players." +pname +"."+world +".shrine.z", loc.getZ());
		save();
	}
	
	public void unbind(Player player) {
		String pname = player.getName();
		String world = player.getWorld().getName();
		yml.removeProperty("players." +pname +"."+world +".shrine.x");
		yml.removeProperty("players." +pname +"."+world +".shrine.y");
		yml.removeProperty("players." +pname +"."+world +".shrine.z");
	}
	
	/**
	 * Gets the shrine the player clicked
	 * @param player to be checked
	 * @return Location of the place the player was standing when he clicked the shrine
	 */
	public Location getBoundShrine(Player player) {
		String pname = player.getName();
		World world = player.getWorld();
		String worldName = world.getName();
		
		// check if a shrine is saved
		Object doesShrineExists = yml.getProperty("players."+pname +"."+worldName +".shrine.x");
		if (doesShrineExists == null) {
			return null;
		}
		
		double x = yml.getDouble("players."+pname +"."+worldName +".shrine.x", 0);
		double y = yml.getDouble("players."+pname +"."+worldName +".shrine.y", 64);
		double z = yml.getDouble("players."+pname +"."+worldName +".shrine.z", 0);
		Location loc = new Location(world, x, y, z);	
		return loc;
	}

	/**
	 * Called when a player teleports or enters a portal.
	 * Checks if the player enters a world where he has never been before.
	 * @param player who teleported or used a portal
	 */
	public void worldChange(Player player) {
		String worldName = player.getWorld().getName();
		String name = player.getName();
		List<String> worlds = yml.getKeys("players."+name);
		
		try {
			for (String world : worlds) {
				if(world.equalsIgnoreCase(worldName)) {
					return;
				}
			}
		}catch (NullPointerException e) {
			worldChangeHelper(name, worldName, player.getLocation());
			return;
		}
		worldChangeHelper(name, worldName, player.getLocation());
	}
	
	public String getGrave(Player player) {
		String name = player.getName();
		String world = player.getWorld().getName();
		String x,y,z;
		try {
			x = yml.getString("players." +name +"."+world +".location.x");
			y = yml.getString("players." +name +"."+world +".location.y");
			z = yml.getString("players." +name +"."+world +".location.z");
		}catch(NullPointerException e) {
			return Messages.youHaveNoGrave.msg();
		}
		return Messages.yourGraveIsHere +": "+x +", "+y+", "+z;
	}
	
	/**
	 * Gives a player his drops back
	 * @param player which gets his items
	 */
	public void giveDrops(Player player) {
		//TODO testen: leeres inventory
		PlayerInventory inv = player.getInventory();
		ItemStack [] stack = dropManager.get(player.getName());
		if (stack != null) {
			try {
				for (ItemStack item : stack) {
					if (item == null) continue;
					inv.addItem(item);
				}
			}catch(NullPointerException e) {
				// empty inventory on death
			}
		}
		else {
			for (ItemStack item : dardrops.get(player)) {
				inv.addItem(item);
			}
		}
	}
	
	public void setDisplayName(Player player) {
		String ghostName = config.getGhostName();
		if (ghostName != "") {
			player.setDisplayName(ghostName.replace("%player%", player.getName()));
		}
	}
	
	/**
	 * Gives ghosts their drops back
	 * @param plugin plugin which is using the method
	 */
	public void onDisable(DAR plugin) {
		if(!config.isDroppingEnabled()) {
			List<String> names = yml.getKeys("players.");
			for (String name : names) {
				List<String> worlds = yml.getKeys("players."+name);
				Player player = plugin.getServer().getPlayer(name);
				if (player == null) continue;
				if (!player.isOnline()) continue;
				for (String world : worlds) {
					if (yml.getBoolean("players."+name+"."+world+".dead", false)) {
						giveDrops(player);
					}
				}
			}
		}
	}

//  private methods ************************************************************************************************************
	private void worldChangeHelper(String playerName, String worldName, Location location) {
		yml.setProperty("players." +playerName +"."+worldName +".dead", false);
		yml.setProperty("players." +playerName +"."+worldName +".location.x", location.getBlockX());
		yml.setProperty("players." +playerName +"."+worldName +".location.y", location.getBlockY());
		yml.setProperty("players." +playerName +"."+worldName +".location.z", location.getBlockZ());
		yml.setProperty("players." +playerName +"."+worldName +".world", worldName);
		
		yml.save();
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
	            if (cost == inv) return true;		// added that cause it was causing problems (if 5 items are needed you need 6 and it consumes 5)
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
}
