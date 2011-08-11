package muCkk.DeathAndRebirth;

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
	
	public DARBlockListener(DARShrines shrines, DARHandler ghosts, DARGraves graves) {
		this.shrines = shrines;
		this.ghosts = ghosts;
		this.graves = graves;
	}
	
	public void onBlockDamage(BlockDamageEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		// *** preventing ghosts from destroying blocks ***
		if(ghosts.isGhost(player)) {
			DARMessages.cantDoThat(player);
			event.setCancelled(true);
			return;
		}
		// ***************************************************
		
		Material type = event.getBlock().getType(); 
		// *** FURNACE ***
		if(type.equals(Material.FURNACE) || type.equals(Material.CHEST)) {
			if(ghosts.isGhost(player)) {
				DARMessages.cantDoThat(player);
				event.setCancelled(true);
				return;
			}
		}
		// *** SIGN_POST ***		
		if(type.equals(Material.SIGN_POST)) {
			Block block = event.getBlock();
			if (graves.isProtected(player.getName(), block.getX(), block.getY(), block.getZ())) {
				DARMessages.graveProtected(player);
				event.setCancelled(true);
				return;
			}
		}
		// *** shrine is being damaged ***
		String shrine = shrines.getClose(player);
		if (shrine != null) {
			if(shrines.isShrineArea(shrine, event.getBlock(), player)) {
				DARMessages.shrineCantBeDestroyed(player);
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
		
		// *** preventing ghosts from placing blocks ***
		if(ghosts.isGhost(player)) {
			DARMessages.cantDoThat(player);
			event.setBuild(false);
			event.setCancelled(true);
			return;
		}
		
		
		// *** preventing players from placing blocks on shrines ***
		String shrine = shrines.getClose(player);
		if (shrine != null) {
			Block block = event.getBlock();
			if(shrines.isShrineArea(shrine, block, player)) {
				DARMessages.shrineCantBuild(player);
				event.setBuild(false);
				event.setCancelled(true);
			}
		}
	}
}
