package muCkk.DeathAndRebirth;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DARPlayerListener extends PlayerListener {

	private DARHandler ghosts;
	private DARShrines shrines;
	
	public DARPlayerListener(DARHandler ghosts, DARShrines shrines) {
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
	 * Sets the players respawn location to their location of death
	 */
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if(ghosts.isGhost(player)) {
			event.setRespawnLocation(ghosts.getLocation(player));
		}
	}
	
	/**
	 * Prevents dead players from picking up items
	 */
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(event.isCancelled()) {
			return;
		}
		if(ghosts.isGhost(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	/**
	 * Players try to bind their soul to a shrine
	 */
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		// *** Ghosts can't interact with everything ***
		if(ghosts.isGhost(player)) {
			Material type = event.getClickedBlock().getType(); 
			if(			!type.equals(Material.WOOD_DOOR)
					||	!type.equals(Material.WOODEN_DOOR)
					||	!type.equals(Material.STONE_BUTTON)
					||	!type.equals(Material.LEVER)) {
				
				DARMessages.cantDoThat(player);
				event.setCancelled(true);
				return;
			}
		}
		
		// *** shrine is clicked ***
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			String shrine = shrines.getClose(player);
			if (shrine != null) {
				Block clickedBlock = event.getClickedBlock();
				if(shrines.isShrine(shrine, clickedBlock, player)) {
					ghosts.bindSoul(player);
					DARMessages.boundShrine(player);
				}
			}
		}
	}
}
