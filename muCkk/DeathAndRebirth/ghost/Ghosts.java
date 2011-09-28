package muCkk.DeathAndRebirth.ghost;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.config.CFG;
import muCkk.DeathAndRebirth.config.Config;
import muCkk.DeathAndRebirth.messages.Errors;
import muCkk.DeathAndRebirth.messages.Messages;
import muCkk.DeathAndRebirth.otherPlugins.Perms;
import net.minecraft.server.Packet201PlayerInfo;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;

public class Ghosts {

	private String dir;
	private File ghostsFile;
	private HashMap<String, Boolean> isRessing;
	
	private DAR plugin;
	private Config config;
	private Graves graves;
	private Drops dardrops;
	
	private Configuration yml;
	
	public Ghosts(DAR plugin, String dir, Config config, Graves graves) {
		this.plugin = plugin;
		this.dir = dir+"/data";
		this.ghostsFile = new File(this.dir+"/ghosts");
		this.config = config;
		this.graves = graves;
		this.isRessing = new HashMap<String, Boolean>();
		this.dardrops = new Drops(this.dir, config);
		load();
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
            	Errors.couldNotReadGhostFile();
            	e.printStackTrace();
            }
        } else {
        	// loaded
        }
		try {
            yml = new Configuration(ghostsFile);
            yml.load();
        } catch (Exception e) {
        	Errors.couldNotReadGhostFile();
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
	public void died(Player player, PlayerInventory inv) {
		String pname = player.getName();
		String world = player.getWorld().getName();
		Block block = player.getWorld().getBlockAt(player.getLocation());
	
		yml.setProperty("players."+pname +"."+world +".dead", true);
	// lightning
		if (config.getBoolean(CFG.LIGHTNING_DEATH)) {
			player.getWorld().strikeLightningEffect(player.getLocation());
		}
	// saving location of death	
		yml.setProperty("players."+pname +"."+world +".location.x", block.getX());
		yml.setProperty("players."+pname +"."+world +".location.y", block.getY());
		yml.setProperty("players."+pname +"."+world +".location.z", block.getZ());		
		save();
		
	// drop-management
		if (!config.getBoolean(CFG.DROPPING) || Perms.hasPermission(player, "dar.nodrop") || config.getBoolean(CFG.PVP_DROP)) {
			dardrops.put(player, inv);  
		}
		
	// invisibility
	//	if (config.getBoolean(CFG.INVISIBILITY)) vanish(player);
		
	// spout related
		if (config.getBoolean(CFG.SPOUT_ENABLED)) {
			plugin.darSpout.playerDied(player, config.getString(CFG.DEATH_SOUND));
		}

	// grave related
		String l1 = "R.I.P";
		graves.addGrave(pname,block, l1, world);
		save();
	}
	
	public String getGhostDisplayName(Player player) {
		return config.getString(CFG.GHOST_NAME).replace("%player%", player.getName()).replace("%displayname%", player.getDisplayName());
	}
	/**
	 * Brings players back to life
	 * @param player to be resurrected
	 */
	public void resurrect(final Player player) {
		String pname = player.getName();
		String world = player.getWorld().getName();
		
		player.setCompassTarget(player.getWorld().getSpawnLocation());
		
		yml.setProperty("players."+pname +"."+world +".dead", false);
		graves.deleteGrave(player.getWorld().getBlockAt(getLocation(player)), pname, world);
		plugin.message.send(player, Messages.reborn);
		
	// check if lightning is enabled
		if (config.getBoolean(CFG.LIGHTNING_REBIRTH)) {
			player.getWorld().strikeLightningEffect(player.getLocation());
		}
		
		setDisplayName(player, false);
		
		if (config.getBoolean(CFG.INVISIBILITY)) unvanish(player);
		
	// spout related
		if (config.getBoolean(CFG.SPOUT_ENABLED)) {
			plugin.darSpout.playerRes(player, config.getString(CFG.REB_SOUND));
		}
		save();
		
			new Thread() {
				@Override
				public void run() {				
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						System.out.println("[Death and Rebirth] Error: Could not sleep while giving drops.");
						e.printStackTrace();
					}
					
					player.getInventory().clear();
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						System.out.println("[Death and Rebirth] Error: Could not sleep while giving drops.");
						e.printStackTrace();
					}
					
					if (!config.getBoolean(CFG.DROPPING) || Perms.hasPermission(player, "dar.nodrop") || config.getBoolean(CFG.PVP_DROP)) dardrops.givePlayerInv(player);
				}
			}.start();
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
		if (!config.getBoolean(CFG.CORPSE_SPAWNING)) 	player.teleport(getLocation(player));
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
		player.setHealth(config.getInt(CFG.HEALTH));
		
		//drops
		dardrops.selfResPunish(player);
		
		//economy
		double money = config.getDouble(CFG.ECONOMY);
		if(money > 0) 	plugin.darConomy.take(player, money);
		
		// mcMMO
		if (config.getBoolean(CFG.MCMMO)) {
			List<String> skills = config.getKeys("SKILLS");
			int amount = config.getInt(CFG.XP);
			for (String skill : skills) {
				if(config.getBoolean("SKILLS."+skill)) plugin.darmcmmo.xpPenality(player, skill, amount);
			}
		}
	}
	
	public void removeItems(Player player) {
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
		if(distance > config.getInt(CFG.DISTANCE)) {
			plugin.message.send(player, Messages.tooFarAway);
			return;
		}
		final int itemID = config.getInt(CFG.ITEM_ID);
		final int amount = config.getInt(CFG.AMOUNT);
	// check for items
		if (config.getBoolean(CFG.NEED_ITEM)) {	
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
				int time = config.getInt(CFG.TIME);
				int x = player.getLocation().getBlockX(),
					z = player.getLocation().getBlockZ();
				if (config.getBoolean(CFG.SPOUT_ENABLED)) {
					plugin.darSpout.playResSound(player, config.getString(CFG.RES_SOUND));
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
				if(config.getBoolean(CFG.NEED_ITEM)) {
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
					if (config.getBoolean(CFG.SPOUT_ENABLED)) {
						plugin.darSpout.playRebirthSound(player, config.getString(CFG.REB_SOUND));
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
	
	public void setDisplayName(Player player, boolean isDead) {
		if ( config.getString(CFG.GHOST_NAME).equalsIgnoreCase("")) return;
		
		String playerName = player.getName();
		String world = player.getWorld().getName();
		
		if(isDead) {
			yml.setProperty("players."+playerName+"."+world+".displayname", player.getDisplayName());
			player.setDisplayName(getGhostDisplayName(player));
		}
		else {
			player.setDisplayName(yml.getString("players."+playerName+"."+world+".displayname"));
		}
		yml.save();
	}
	
	/**
	 * Gives ghosts their drops back
	 * @param plugin plugin which is using the method
	 */
	public void onDisable(DAR plugin) {
		if(!config.getBoolean(CFG.DROPPING)) {
			List<String> names = yml.getKeys("players.");
			for (String name : names) {
				List<String> worlds = yml.getKeys("players."+name);
				Player player = plugin.getServer().getPlayer(name);
				if (player == null) continue;
				if (!player.isOnline()) continue;
				for (String world : worlds) {
					if (yml.getBoolean("players."+name+"."+world+".dead", false)) {
						dardrops.givePlayerInv(player);
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
