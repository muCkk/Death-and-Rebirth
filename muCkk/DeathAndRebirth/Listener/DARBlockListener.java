package muCkk.DeathAndRebirth.Listener;

import muCkk.DeathAndRebirth.DARGraves;
import muCkk.DeathAndRebirth.DARHandler;
import muCkk.DeathAndRebirth.DARShrines;
import muCkk.DeathAndRebirth.Config.DARProperties;
import muCkk.DeathAndRebirth.Messages.DARMessages;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class DARBlockListener extends BlockListener {

	private DARShrines shrines;
	private DARHandler ghosts;
	private DARGraves graves;
	private DARProperties config;
	private DARMessages msg;
	
	public DARBlockListener(DARProperties config, DARMessages msg, DARShrines shrines, DARHandler ghosts, DARGraves graves) {
		this.config = config;
		this.msg = msg;
		this.shrines = shrines;
		this.ghosts = ghosts;
		this.graves = graves;
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
		
		// *** preventing ghosts from destroying blocks ***
		if(ghosts.isGhost(player)) {
//			TODO !!! removed message temporarily because of spam			
//			msg.cantDoThat(player);
			event.setCancelled(true);
			return;
		}
		// ***************************************************
		
		Material type = event.getBlock().getType(); 
		// *** FURNACE ***
		if(type.equals(Material.FURNACE) || type.equals(Material.CHEST)) {
			if(ghosts.isGhost(player)) {
				msg.cantDoThat(player);
				event.setCancelled(true);
				return;
			}
		}
		// *** SIGN_POST ***		
		if(type.equals(Material.SIGN_POST)) {
			Block block = event.getBlock();
			if (graves.isProtected(player.getName(), player.getWorld().getName(), block.getX(), block.getY(), block.getZ())) {
				msg.graveProtected(player);
				event.setCancelled(true);
				return;
			}
		}
		// *** shrine is being damaged ***
		String shrine = shrines.getClose(player);
		if (shrine != null) {
			if(shrines.isShrineArea(shrine, event.getBlock(), player)) {
				msg.shrineCantBeDestroyed(player);
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
			msg.cantDoThat(player);
			event.setBuild(false);
			event.setCancelled(true);
			return;
		}
		
		
		// *** preventing players from placing blocks on shrines ***
		String shrine = shrines.getClose(player);
		if (shrine != null) {
			Block block = event.getBlock();
			if(shrines.isShrineArea(shrine, block, player)) {
				msg.shrineCantBuild(player);
				event.setBuild(false);
				event.setCancelled(true);
			}
		}
	}
}
