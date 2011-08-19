package muCkk.DeathAndRebirth.listener;

import muCkk.DeathAndRebirth.DARGraves;
import muCkk.DeathAndRebirth.DARHandler;
import muCkk.DeathAndRebirth.config.DARProperties;
import muCkk.DeathAndRebirth.messages.DARMessages;
import muCkk.DeathAndRebirth.messages.Messages;
import muCkk.DeathAndRebirth.shrines.DARShrines;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class DARBlockListener extends BlockListener {

	private DARShrines shrines;
	private DARHandler ghosts;
	private DARGraves graves;
	private DARProperties config;
	private DARMessages message;
	
	public DARBlockListener(DARProperties config, DARShrines shrines, DARHandler ghosts, DARGraves graves, DARMessages message) {
		this.config = config;
		this.shrines = shrines;
		this.ghosts = ghosts;
		this.graves = graves;
		this.message = message;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
	// *** protecting graves ************************************************
		Material type = event.getBlock().getType(); 
		Player player = event.getPlayer();
		if(type.equals(Material.SIGN_POST)) {
			Block block = event.getBlock();
			if (graves.isProtected(player.getName(), player.getWorld().getName(), block.getX(), block.getY(), block.getZ())) {
				Sign sign = (Sign) event.getBlock().getState();
				message.send(player, Messages.graveProtected);
				event.setCancelled(true);
				sign.update(true);
				return;
			}
		}
		if(ghosts.isGhost(player)) {
			message.send(player, Messages.cantDoThat);
			event.setCancelled(true);
			return;
		}
	}
	
	public void onBlockDamage(BlockDamageEvent event) {
		if(event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		
		// check if the world is enabled
		if(!config.isEnabled(player.getWorld().getName())) {
			return;
		}
		
		
		// *** shrine is being damaged ***
		String shrine = shrines.getClose(player.getLocation());
		if (shrine != null) {
			if(shrines.isShrineArea(shrine, event.getBlock())) {
				message.send(player, Messages.shrineProtectedDestroy);
				event.setCancelled(true);
				return;
			}
		}
	}
	
	public void onBlockPlace (BlockPlaceEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		// check if the world is enabled
		if(!config.isEnabled(player.getWorld().getName())) {
			return;
		}

		// *** preventing ghosts from placing blocks ***
		if(ghosts.isGhost(player)) {
			message.send(player, Messages.cantDoThat);
			event.setBuild(false);
			event.setCancelled(true);
			return;
		}
		
		
		// *** preventing players from placing blocks on shrines ***
		String shrine = shrines.getClose(player.getLocation());
		if (shrine != null) {
			Block block = event.getBlock();
			if(shrines.isShrineArea(shrine, block)) {
				message.send(player, Messages.shrineProtectedBuild);
				event.setBuild(false);
				event.setCancelled(true);
			}
		}
	}
}
