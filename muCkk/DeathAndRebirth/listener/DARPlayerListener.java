package muCkk.DeathAndRebirth.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.config.DARProperties;
import muCkk.DeathAndRebirth.ghost.DARGhosts;
import muCkk.DeathAndRebirth.ghost.DARShrines;
import muCkk.DeathAndRebirth.messages.DARErrors;
import muCkk.DeathAndRebirth.messages.DARMessages;
import muCkk.DeathAndRebirth.messages.Messages;
import muCkk.DeathAndRebirth.otherPlugins.Spout;

//import org.bukkit.Location;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DARPlayerListener extends PlayerListener {

	private DARGhosts ghosts;
	private DARShrines shrines;
	private DARProperties config;
	private DAR plugin;
	private Spout spout;
	private DARMessages message;
	
	public DARPlayerListener(DAR plugin, DARProperties config, DARGhosts ghosts, DARShrines shrines, Spout spout, DARMessages message) {
		this.plugin = plugin;
		this.config = config;
		this.ghosts = ghosts;
		this.shrines = shrines;
		this.spout = spout;
		this.message = message;
	}
	/**
	 * Looks for new Players and adds them to the list
	 */
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if(!ghosts.existsPlayer(player)) {
			ghosts.newPlayer(player);
		}
		
	// version checking
	// in own thread because it takes some time and would stop the rest of the server to load
		if(player.isOp()) {
			new Thread() {
				public void run() {
					try {
						URL versionURL = new URL("http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/version.txt");
						BufferedReader reader = new BufferedReader(new InputStreamReader(versionURL.openStream()));
						
						String line = reader.readLine();
						if (!plugin.getDescription().getVersion().equalsIgnoreCase(line)) {
							message.sendChat(player, Messages.newVersion, ": " + line);
						}
						reader.close();
					} catch (MalformedURLException e) {
						// versionURL
						DARErrors.readingURL();
					} catch (IOException e) {
						// versionURL.openstream()
						DARErrors.openingURL();
					}
				}
			}.start();
		}
	}
	
	/**
	 * Checks if ghosts are allowed to chat
	 */
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		if(ghosts.isGhost(player) && !config.isGhostChatEnabled()) {
			message.send(player, Messages.ghostNoChat);
			event.setCancelled(true);
		}
	}
	
	/**
	 * Sets the players respawn location to their location of death
	 */
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		// check if the world is enabled
		if(!config.isEnabled(player.getWorld().getName())) {
			return;
		}
		if(ghosts.isGhost(player)) {
			if (config.isReverseSpawningEnabled()) {
				Location loc = ghosts.getBoundShrine(player);
				if (loc != null) event.setRespawnLocation(loc);
			}
			else event.setRespawnLocation(ghosts.getLocation(player));
			message.send(player, Messages.playerDied);	
		//  spout related
			if (config.isSpoutEnabled()) {
				spout.setDeathOptions(player, config.getGhostSkin());
			}
			player.setDisplayName(ghosts.getGhostDisplayName(player));
		}
	}
	
	/**
	 * Prevents dead players from picking up items
	 */
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		// check if the world is enabled
		if(!config.isEnabled(event.getPlayer().getWorld().getName())) {
			return;
		}
		if(ghosts.isGhost(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	/**
	 * Flying for ghosts
	 */
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!config.isFlyingEnabled()) return;
		
		Player player = event.getPlayer();			
		
	// flying for ghosts
		if(!player.isSneaking() || !ghosts.isGhost(player)) {
			return;
		}		
		player.setVelocity(player.getLocation().getDirection().multiply(1));		
	}
	
	/**
	 * Players try to bind their soul to a shrine
	 */
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		
		// check if the world is enabled
		if(!config.isEnabled(player.getWorld().getName())) {
			return;
		}
		

	// *** ghost interactions ***
		if (ghosts.isGhost(player)) {
			Block block = event.getClickedBlock();
			
			// resurrection
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(config.isReverseSpawningEnabled()) {
					Location locDeath = ghosts.getLocation(player);
					if (event.getClickedBlock().getLocation().distance(locDeath) < 3) ghosts.resurrect(player);
					else return;
				}
				else {
					String shrine = shrines.getClose(player.getLocation());
					if (shrine != null) {
						ghosts.resurrect(player);
					}
				}
			return;
			}
			
			// normal interactions		
			
			if (config.isBlockGhostInteractionEnabled()) {
				event.setCancelled(true);
				return;
			}
			try {
				Material type = block.getType(); 			
				if(			type.equals(Material.FURNACE)
						||	type.equals(Material.CHEST)) {
					message.send(player, Messages.cantDoThat);
					event.setCancelled(true);
					return;
				}
			}catch (NullPointerException e) {
				// Material = null
			}
		}
	// *** selection mode for creating shrines ****************
		if(player.getName().equalsIgnoreCase(shrines.getSelPlayer()) && shrines.isSelModeEnable() && player.getItemInHand().getTypeId() == 280) {
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				shrines.setSel1(event.getClickedBlock().getLocation());
				player.sendMessage("Position 1 set");
			}
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				shrines.setSel2(event.getClickedBlock().getLocation());
				player.sendMessage("Position 2 set");
			}
		}
		
	// *** shrine is clicked ***
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			String shrine = shrines.getClose(player.getLocation());
			if (shrine != null) {
				Block clickedBlock = event.getClickedBlock();
				if(shrines.isShrineArea(shrine, clickedBlock)) {
					ghosts.bindSoul(player);
					message.send(player, Messages.soulNowBound);
				}
			}
			return;
		}
		
	// *** getting the name of a shrine ******************************
		if (player.isOp() && event.getAction() == Action.LEFT_CLICK_BLOCK && player.getItemInHand().getTypeId() == 280) {
			String shrine = shrines.getClose(player.getLocation());
			if (shrine != null) {
				player.sendMessage("Shrine: "+shrine);
			}
			return;
		}
	}
	
	/**
	 * checks if the player enters a new world
	 */
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		ghosts.worldChange(player);
	}
	
	/**
	 * checks if the player enters a new world
	 */
	public void onPlayerPortal(PlayerPortalEvent event) {
		Player player = event.getPlayer();
		ghosts.worldChange(player);
	}
}
