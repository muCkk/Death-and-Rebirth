package muCkk.DeathAndRebirth;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class DARBlockListener extends BlockListener {

	private DARShrines shrines;
	
	public DARBlockListener(DARShrines shrines) {
		this.shrines = shrines;
	}
	
	public void onBlockPlace (BlockPlaceEvent event) {
		Player player = event.getPlayer();
		String shrine = shrines.getClose(player);
		if (shrine != null) {
			Block block = event.getBlock();
			if(shrines.isShrine(shrine, block, player)) {
				DARMessages.shrineCantBuild(player);
				event.setBuild(false);
				event.setCancelled(true);
			}
		}
	}
}
