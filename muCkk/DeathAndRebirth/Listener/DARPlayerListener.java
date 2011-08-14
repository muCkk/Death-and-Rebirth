package muCkk.DeathAndRebirth.Listener;

import muCkk.DeathAndRebirth.DARHandler;
import muCkk.DeathAndRebirth.DARShrines;
import muCkk.DeathAndRebirth.DARSpout;
import muCkk.DeathAndRebirth.Config.DARProperties;
import muCkk.DeathAndRebirth.Messages.DARMessages;

import org.bukkit.Material;
import org.bukkit.block.Block;
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

	private DARHandler ghosts;
	private DARShrines shrines;
	private DARProperties config;
	private DARMessages msg;

	
	public DARPlayerListener(DARProperties config, DARMessages msg, DARHandler ghosts, DARShrines shrines) {
		this.config = config;
		this.msg = msg;
		this.ghosts = ghosts;
		this.shrines = shrines;
	}
	/**
	 * Looks for new Players and adds them to the list
	 */
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if(!ghosts.existsPlayer(event.getPlayer())) {
			ghosts.newPlayer(event.getPlayer());
		}
	}
	
	/**
	 * Checks if ghosts are allowed to chat
	 */
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		if(ghosts.isGhost(player) && !config.isGhostChatEnabled()) {
			DARMessages.ghostsCantChat(player);
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
			event.setRespawnLocation(ghosts.getLocation(player));
			DARMessages.playerDied(player);
			// *** spout stuff ***
			if (config.isSpoutEnabled()) {
				DARSpout.setGhostSkin(player);
				player.setDisplayName("Ghost of "+player.getName());
			}
		}
	}
	
	/**
	 * Prevents dead players from picking up items
	 */
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(event.isCancelled()) {
			return;
		}
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
		Player player = event.getPlayer();
		
		if(!player.isSneaking() || !config.isFlyingEnabled() || !ghosts.isGhost(player)) {
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
			if (config.isBlockGhostInteractionEnabled()) {
				event.setCancelled(true);
				return;
			}
			try {
				Material type = event.getClickedBlock().getType(); 			
				if(			!type.equals(Material.WOOD_DOOR)
						&&	!type.equals(Material.WOODEN_DOOR)
						&&	!type.equals(Material.STONE_BUTTON)
						&&	!type.equals(Material.LEVER)) {
	//				TODO !!! removed message temporarily because of spam
	//				msg.cantDoThat(player);
					event.setCancelled(true);
					return;
				}
			}catch (NullPointerException e) {
				// TODO NullPointer PlayerInteract
			}
		}
		
		
		// *** shrine is clicked ***
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			String shrine = shrines.getClose(player.getLocation());
			if (shrine != null) {
				Block clickedBlock = event.getClickedBlock();
				if(shrines.isShrine(shrine, clickedBlock, player)) {
					ghosts.bindSoul(player);
					msg.boundShrine(player);
				}
			}
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
