package muCkk.DeathAndRebirth;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.Material;

public class DARPlayerListener extends PlayerListener {

	private DARHandler ghosts;
	private DARGraves graves;
	private DARShrines shrines;
	
	public DARPlayerListener(DARHandler ghosts, DARGraves graves, DARShrines shrines) {
		this.ghosts = ghosts;
		this.graves = graves;
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
	 * Resurrects a player when a shrine is clicked. 
	 * Prevents dead players from interacting with chests and furnaces.
	 * Protects graves.
	 */
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		Material type = event.getClickedBlock().getType();
		if(type.equals(Material.FURNACE) || type.equals(Material.CHEST)) {
			if(ghosts.isGhost(player)) {
				DARMessages.cantUseThat(player);
				event.setUseInteractedBlock(Result.DENY);
				event.setCancelled(true);
			}
		}
		if(type.equals(Material.SIGN_POST)) {
			Block block = event.getClickedBlock();
			if (graves.isProtected(player.getName(), block.getX(), block.getY(), block.getZ())) {
				DARMessages.graveProtected(player);
				event.setUseInteractedBlock(Result.DENY);
				event.setCancelled(true);
			}
		}
		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			String shrine = shrines.getClose(player);
			if (shrine != null) {
				if(shrines.isShrine(shrine, event.getClickedBlock(), player)) {
					DARMessages.shrineCantBeDestroyed(player);
					event.setUseInteractedBlock(Result.DENY);
					event.setCancelled(true);
				}
			}
		}
	}
}
