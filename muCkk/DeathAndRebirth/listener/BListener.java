package muCkk.DeathAndRebirth.listener;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.ghost.Ghosts;
import muCkk.DeathAndRebirth.ghost.Graves;
import muCkk.DeathAndRebirth.ghost.Shrines;
import muCkk.DeathAndRebirth.messages.Messages;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BListener implements Listener {

	private DAR plugin;
	private Shrines shrines;
	private Ghosts ghosts;
	private Graves graves;
	
	public BListener(DAR plugin, Shrines shrines, Ghosts ghosts, Graves graves) {
		this.plugin = plugin;
		this.shrines = shrines;
		this.ghosts = ghosts;
		this.graves = graves;
	}
	
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
	// *** protecting graves ************************************************
		Block blockUp = event.getBlock().getRelative(BlockFace.UP);
		Material type = event.getBlock().getType(); 
		Player player = event.getPlayer();
		if(type.equals(Material.SIGN_POST)) {
			Block block = event.getBlock();
			if (graves.isProtected(player.getName(), player.getWorld().getName(), block.getX(), block.getY(), block.getZ())) {
				Sign sign = (Sign) event.getBlock().getState();
				plugin.message.send(player, Messages.graveProtected);
				event.setCancelled(true);
				sign.update(true);
				return;
			}
		}
		if(blockUp.getType().equals(Material.SIGN_POST)) {
			Block block = blockUp;
			if (graves.isProtected(player.getName(), player.getWorld().getName(), block.getX(), block.getY(), block.getZ())) {
				Sign sign = (Sign) event.getBlock().getState();
				plugin.message.send(player, Messages.graveProtected);
				event.setCancelled(true);
				sign.update(true);
				return;
			}
		}
		
		if(ghosts.isGhost(player)) {
			plugin.message.send(player, Messages.cantDoThat);
			event.setCancelled(true);
			return;
		}
	}
	
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockDamage(BlockDamageEvent event) {
		if(event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		
		// check if the world is enabled
		if(!plugin.getConfig().getBoolean(player.getWorld().getName())) {
			return;
		}
		
		
		// *** shrine is being damaged ***
		String shrine = shrines.getClose(player.getLocation());
		if (shrine != null) {
			if(plugin.getConfig().getBoolean("SHRINE_PROTECTION")) {
				if(!player.isOp()) {
					if(shrines.isShrineArea(shrine, event.getBlock())) {
						plugin.message.send(player, Messages.shrineProtectedDestroy);
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockPlace (BlockPlaceEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		// check if the world is enabled
		if(!plugin.getConfig().getBoolean(player.getWorld().getName())) {
			return;
		}

		// *** preventing ghosts from placing blocks ***
		if(ghosts.isGhost(player)) {
			plugin.message.send(player, Messages.cantDoThat);
			event.setBuild(false);
			event.setCancelled(true);
			return;
		}
		
		
		// *** preventing players from placing blocks on shrines ***
		String shrine = shrines.getClose(player.getLocation());
		if (shrine != null) {
			Block block = event.getBlock();
			if(shrines.isShrineArea(shrine, block)) {
				plugin.message.send(player, Messages.shrineProtectedBuild);
				event.setBuild(false);
				event.setCancelled(true);
			}
		}
	}
}
