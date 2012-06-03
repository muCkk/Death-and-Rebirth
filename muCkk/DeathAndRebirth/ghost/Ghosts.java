package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.listener.PListener;
import muCkk.DeathAndRebirth.messages.Errors;
import muCkk.DeathAndRebirth.messages.Messages;
import net.minecraft.server.Packet201PlayerInfo;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Ghosts {

	private static final Logger log = Logger.getLogger("Minecraft");
	private String dir;
	private HashMap<String, Boolean> isRessing;
	
	private DAR plugin;
	private Graves graves;
	private Drops dardrops;
	private Shrines shrines;
	private PListener plistener;
	
	private FileConfiguration customConfig = null;
	private File ghostsFile;
	
	public Ghosts(DAR plugin, String dir, Graves graves) {
		this.plugin = plugin;
		this.dir = dir+"/data";
		this.ghostsFile = new File(this.dir+"/ghosts");
		this.graves = graves;
		graves.setGhosts(this);
		this.isRessing = new HashMap<String, Boolean>();
		this.dardrops = new Drops(plugin, this.dir);
	}
	
	public void reloadCustomConfig() {
	    if (ghostsFile == null) {
	    	ghostsFile = new File(plugin.getDataFolder(), "ghosts");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(ghostsFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("ghosts");
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
		dardrops.saveCustomConfig();
	    if (customConfig == null || ghostsFile == null) {
	    	return;
	    }
	    try {
	        customConfig.save(ghostsFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + ghostsFile, ex);
	    }
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
				
		getCustomConfig().set("players." +pname +"."+world +".dead", false);
		getCustomConfig().set("players." +pname +"."+world +".location.x", player.getLocation().getBlockX());
		getCustomConfig().set("players." +pname +"."+world +".location.y", player.getLocation().getBlockY());
		getCustomConfig().set("players." +pname +"."+world +".location.z", player.getLocation().getBlockZ());
		getCustomConfig().set("players." +pname +"."+world +".world", world);
		
		saveCustomConfig();
	}
	
	/**
	 * Checks if the player is already listed in the ghosts file	
	 * @param player to be checked
	 * @return boolean
	 */
	public boolean existsPlayer(Player player) {
		ConfigurationSection cfgsel = getCustomConfig().getConfigurationSection("players");
		if (cfgsel == null) return false;
		Set<String> names = cfgsel.getKeys(false);
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
			return getCustomConfig().getBoolean("players."+pname +"."+world +".dead", false);	
		}catch (NullPointerException e) {
			return false;
		}		
	}
	
	/**
	 * Manages the death of players.
	 * @param player which died
	 */
	public void died(Player player, PlayerInventory inv, boolean pvp_death) { 
		final String pname = player.getName();
		final String world = player.getWorld().getName();
		final Block block = player.getWorld().getBlockAt(player.getLocation());
	
		getCustomConfig().set("players."+pname +"."+world +".dead", true);
		saveCustomConfig();
		
	// lightning
		if (plugin.getConfig().getBoolean("LIGHTNING_DEATH")) {
			player.getWorld().strikeLightningEffect(player.getLocation());
			player.getLocation().getBlock().getState().update(); //TODO update block
		}
	// saving location of death	
		// moved
		
	// drop-management
		if (!plugin.getConfig().getBoolean("DROPPING") || DAR.perms.has(player, "dar.nodrop") || pvp_death) {
			dardrops.put(player, inv);  
		}
		else {
			dardrops.remove(player);
		}
		
	// invisibility (WAS UNUSED)
	   if (plugin.getConfig().getBoolean("INVISIBILITY")) vanish(player);
		
	// spout related
		if (plugin.getConfig().getBoolean("SPOUT_ENABLED")) {
			plugin.darSpout.playerDied(player, plugin.getConfig().getString("DEATH_SOUND"));
		}

	// grave related
		final String l1 = "R.I.P";
		new Thread() {
			@Override
			public void run() {				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.info("[Death and Rebirth] Error: Could not sleep: explosion.");
					e.printStackTrace();
				}
				graves.addGrave(pname,block, l1, world);
			}
		}.start();
		saveCustomConfig();
	}
	public void setLocationOfDeath(Block block, String pname) {
		getCustomConfig().set("players."+pname +"."+block.getWorld().getName() +".location.x", block.getX());
		getCustomConfig().set("players."+pname +"."+block.getWorld().getName() +".location.y", block.getY());
		getCustomConfig().set("players."+pname +"."+block.getWorld().getName() +".location.z", block.getZ());		
		saveCustomConfig();
	}
	public String getGhostDisplayName(Player player) {
		return plugin.getConfig().getString("GHOST_NAME").replace("%player%", player.getDisplayName()).replace("%displayname%", player.getDisplayName());
	}
	/**
	 * Brings players back to life
	 * @param player to be resurrected
	 */
	public void resurrect(final Player player) {
		String pname = player.getName();
		String world = player.getWorld().getName();
		
		player.setCompassTarget(player.getWorld().getSpawnLocation());
		
	// check if lightning is enabled
		if (plugin.getConfig().getBoolean("LIGHTNING_REBIRTH")) {
			player.getWorld().strikeLightningEffect(player.getLocation());
		}
		
		setDisplayName(player, false);
		
		if (plugin.getConfig().getBoolean("INVISIBILITY")) unvanish(player);
		
	// spout related
		if (plugin.getConfig().getBoolean("SPOUT_ENABLED")) {
			plugin.darSpout.playerRes(player, plugin.getConfig().getString("REB_SOUND"));
		}
		saveCustomConfig();
		
		new Thread() {
			@Override
			public void run() {				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.info("[Death and Rebirth] Error: Could not sleep while giving drops.");
					e.printStackTrace();
				}
				
				player.getInventory().clear();
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.info("[Death and Rebirth] Error: Could not sleep while giving drops.");
					e.printStackTrace();
				}
				
				if (!plugin.getConfig().getBoolean("DROPPING") || DAR.perms.has(player, "dar.nodrop") || plugin.getConfig().getBoolean("PVP_DROP")) dardrops.givePlayerInv(player);
			}
		}.start();
		
		getCustomConfig().set("players."+pname +"."+world +".dead", false);
		saveCustomConfig();
		graves.deleteGrave(player.getWorld().getBlockAt(getLocation(player)), pname, world);
		plugin.message.send(player, Messages.reborn);
	}
	
	public void vanish(final Player vanishingPlayer) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player[] onlinePlayers = vanishingPlayer.getServer().getOnlinePlayers();
				for (Player otherPlayer : onlinePlayers) {
					if (otherPlayer == vanishingPlayer) continue;
					// reveal other ghosts
					if (isGhost(otherPlayer)) {
						((CraftPlayer) vanishingPlayer).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(( (CraftPlayer) otherPlayer).getHandle()));
						((CraftPlayer) vanishingPlayer).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(otherPlayer.getName(), true, 1));
						continue;
					}
					// hide ghost from living players
					((CraftPlayer) otherPlayer).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(((CraftPlayer) vanishingPlayer).getEntityId()));
					((CraftPlayer) otherPlayer).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(vanishingPlayer.getName(), false, 0));
				}
			}
		}, 20L);	
	}
	
	private void unvanish(final Player appearingPlayer) {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					Player[] onlinePlayers = appearingPlayer.getServer().getOnlinePlayers();
					for (Player otherPlayer : onlinePlayers) {
						if (otherPlayer == appearingPlayer) continue;
						// hide other ghosts
						if (isGhost(otherPlayer)) {
							((CraftPlayer) appearingPlayer).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(((CraftPlayer) otherPlayer).getEntityId()));
							((CraftPlayer) appearingPlayer).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(otherPlayer.getName(), false, 0));
							continue;
						}
						// reveal player for others
						((CraftPlayer) otherPlayer).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(( (CraftPlayer) appearingPlayer).getHandle()));
						((CraftPlayer) otherPlayer).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(appearingPlayer.getName(), true, 1));
					}
				}
			}, 20L);		
	}
	
	public void showGhosts(final Player admin) {
		Player[] onlinePlayers = admin.getServer().getOnlinePlayers();
		for (Player ghost : onlinePlayers) {
			if (!isGhost(ghost)) continue;
			((CraftPlayer) admin).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(( (CraftPlayer) ghost).getHandle()));
			((CraftPlayer) admin).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(ghost.getName(), true, 1));
		}
	}
	public void hideGhosts(final Player admin) {
		Player[] onlinePlayers = admin.getServer().getOnlinePlayers();
		for (Player ghost : onlinePlayers) {
			if (!isGhost(ghost)) continue;
			((CraftPlayer) admin).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(((CraftPlayer) ghost).getEntityId()));
			((CraftPlayer) admin).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(ghost.getName(), false, 0));
		}
	}
	
	
	public void selfRebirth(Player player, Shrines shrines) {
		// rebirth at location of death
		if (!plugin.getConfig().getBoolean("CORPSE_SPAWNING")) 	player.teleport(getLocation(player));
		// rebirth at next shrine
		else {
			Location loc = getBoundShrine(player);
			if (loc != null) player.teleport(loc);
			else player.teleport(shrines.getNearestShrine(player.getLocation()));
		}
		resurrect(player);
		selfResPunish(player);
	}
	
	private void selfResPunish(Player player) {
		// health
		player.setHealth(plugin.getConfig().getInt("HEALTH"));
		
		//drops
        dardrops.selfResPunish(player); //selfres
		
		//economy
		double money = plugin.getConfig().getDouble("ECONOMY");
		if(money > 0) 	DAR.econ.withdrawPlayer(player.getName(), money);
		
		// mcMMO
		if (plugin.getConfig().getBoolean("MCMMO")) {
			ConfigurationSection cfgsel = plugin.getConfig().getConfigurationSection("SKILLS");
			if(cfgsel == null) return;
			Set<String> skills = cfgsel.getKeys(false);
			int amount = plugin.getConfig().getInt("XP");
			for (String skill : skills) {
				if(plugin.getConfig().getBoolean("SKILLS."+skill)) plugin.darmcmmo.xpPenality(player, skill, amount);
			}
		}
	}
	
	
    public void removeItems(Player player) {  //selfres
		dardrops.selfResPunish(player);
	} 
	
	/**
	 * Called when a player tries to resurrect someone.
	 * @param player who tries to resurrect someone
	 * @param target player to be resurrected
	 */
	public void resurrect(final Player player, final Player target) {
		// *** check distance ***
		double distance = player.getLocation().distance(target.getLocation());
		if(distance > plugin.getConfig().getInt("DISTANCE")) {
			plugin.message.send(player, Messages.tooFarAway);
			return;
		}
		final int itemID = plugin.getConfig().getInt("ITEM_ID");
		final int amount = plugin.getConfig().getInt("AMOUNT");
	// check for items
		if (plugin.getConfig().getBoolean("NEED_ITEM")) {	
			ItemStack costStack = new ItemStack(itemID);
			costStack.setAmount(amount);
			
			if(!CheckItems(player, costStack)) {
				plugin.message.sendChat(player, Messages.notEnoughItems, " "+amount +" "+Material.getMaterial(itemID).name());
				return;
			}
			
			if(!ConsumeItems(player, costStack)) {
				plugin.message.sendChat(player, Messages.notEnoughItems, " "+amount +" "+Material.getMaterial(itemID).name());
				return;
			}
		}
		
		final String name = player.getName();
		new Thread() {
			public void run() {
				int counter = 0;
				int time = plugin.getConfig().getInt("TIME");
				int x = player.getLocation().getBlockX(),
					z = player.getLocation().getBlockZ();
				isRessing.put(name, true);
				if (plugin.getConfig().getBoolean("SPOUT_ENABLED")) {
					plugin.darSpout.playResSound(player, plugin.getConfig().getString("RES_SOUND"));
				}
				while (counter < time && isRessing.get(name)) {
					if (x != player.getLocation().getBlockX() || z != player.getLocation().getBlockZ()) {
						isRessing.put(name, false);
						continue;
					}
					plugin.message.send(player, Messages.resurrecting, String.valueOf(counter));
					try {
						sleep(1000);
					}catch (InterruptedException e) {
						Errors.whileRessing();
						e.printStackTrace();
					}
					counter++;
				}
				isRessing.remove(name);
				if(plugin.getConfig().getBoolean("NEED_ITEM")) {
					ItemStack costStack = new ItemStack(itemID);
					costStack.setAmount(amount);
					if(!ConsumeItems(player, costStack)) {
						plugin.message.sendChat(player, Messages.notEnoughItems, " "+amount +" "+Material.getMaterial(itemID).name());
						counter = time-1;
					}
				}
				if(counter == time) {					
					resurrect(target);
					plugin.message.send(player, Messages.resurrected, " "+target.getName());
					target.teleport(getLocation(target));
				// spout related
					if (plugin.getConfig().getBoolean("SPOUT_ENABLED")) {
						plugin.darSpout.playRebirthSound(player, plugin.getConfig().getString("REB_SOUND"));
					}
				}
			}
		}.start();		
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
		
		double x = getCustomConfig().getDouble("players."+pname +"."+worldName +".location.x", 0);
		double y = getCustomConfig().getDouble("players."+pname +"."+worldName +".location.y", 64);
		double z = getCustomConfig().getDouble("players."+pname +"."+worldName +".location.z", 0);
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
		
		getCustomConfig().set("players." +pname +"."+world +".shrine.x", loc.getX());
		getCustomConfig().set("players." +pname +"."+world +".shrine.y", loc.getY());
		getCustomConfig().set("players." +pname +"."+world +".shrine.z", loc.getZ());
		saveCustomConfig();
	}
	
	public void unbind(Player player) {
		String pname = player.getName();
		String world = player.getWorld().getName();
		getCustomConfig().set("players." +pname +"."+world +".shrine.x", null);
		getCustomConfig().set("players." +pname +"."+world +".shrine.y", null);
		getCustomConfig().set("players." +pname +"."+world +".shrine.z", null);
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
		Object doesShrineExists = getCustomConfig().get("players."+pname +"."+worldName +".shrine.x");
		if (doesShrineExists == null) {
			return null;
		}
		
		double x = getCustomConfig().getDouble("players."+pname +"."+worldName +".shrine.x", 0);
		double y = getCustomConfig().getDouble("players."+pname +"."+worldName +".shrine.y", 64);
		double z = getCustomConfig().getDouble("players."+pname +"."+worldName +".shrine.z", 0);
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
		ConfigurationSection cfgsel = getCustomConfig().getConfigurationSection("players."+name);
		if(cfgsel != null) { 
			Set<String> worlds = cfgsel.getKeys(false);
			
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
			Location nearestShrine = shrines.getNearestShrineSpawn(player.getLocation());
			plistener.giveGhostCompass(player, nearestShrine);
			worldChangeHelper(name, worldName, player.getLocation());
		}
	}
	
	public String getGrave(Player player) {
		String name = player.getName();
		String world = player.getWorld().getName();
		String x,y,z;
		try {
			x = getCustomConfig().getString("players." +name +"."+world +".location.x");
			y = getCustomConfig().getString("players." +name +"."+world +".location.y");
			z = getCustomConfig().getString("players." +name +"."+world +".location.z");
		}catch(NullPointerException e) {
			return Messages.youHaveNoGrave.msg();
		}
		return Messages.yourGraveIsHere +": "+x +", "+y+", "+z;
	}
	
	public void setDisplayName(Player player, boolean isDead) {
		if ( plugin.getConfig().getString("GHOST_NAME").equalsIgnoreCase("")) return;
		
		String playerName = player.getName();
		String world = player.getWorld().getName();
		
		if(isDead) {
			getCustomConfig().set("players."+playerName+"."+world+".displayname", player.getDisplayName());
			player.setDisplayName(getGhostDisplayName(player));
		}
		else {
			player.setDisplayName(getCustomConfig().getString("players."+playerName+"."+world+".displayname"));
		}
		saveCustomConfig();
	}
	
//  private methods ************************************************************************************************************
	private void worldChangeHelper(String playerName, String worldName, Location location) {
		getCustomConfig().set("players." +playerName +"."+worldName +".dead", false);
		getCustomConfig().set("players." +playerName +"."+worldName +".location.x", location.getBlockX());
		getCustomConfig().set("players." +playerName +"."+worldName +".location.y", location.getBlockY());
		getCustomConfig().set("players." +playerName +"."+worldName +".location.z", location.getBlockZ());
		getCustomConfig().set("players." +playerName +"."+worldName +".world", worldName);
		
		saveCustomConfig();
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
