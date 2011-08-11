package muCkk.DeathAndRebirth;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

public class DAREntityListener extends EntityListener {

	DARHandler ghosts;
	
	public DAREntityListener(DARHandler ghosts) {
		this.ghosts = ghosts;
	}
	
	/**
	 * Checks for dying players
	 */
	public void onEntityDeath(EntityDeathEvent event) {		
		Entity entity = event.getEntity();		
		if(entity.getClass() == CraftPlayer.class) {
			Player player = (Player) entity;
			ghosts.died(player);
		}
	}
	
	/**
	 * Stops creatures from attacking ghosts
	 */
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		try {
			Entity target = event.getTarget();
			if(target.getClass().equals(CraftPlayer.class)) {
				Player player = (Player) target;
				if(ghosts.isGhost(player)) {
					event.setCancelled(true);
				}
			}
		}catch (NullPointerException e) {
			//TODO NullPointer: onEntityTarget - happens sometimes
		}
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		// *** a ghost tries to attack ***
		if (event instanceof EntityDamageByEntityEvent) {
			 Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
			 if (damager instanceof Player) {
				 Player attacker = (Player) damager;
				 if(ghosts.isGhost(attacker)) {
					 DARMessages.cantDoThat(attacker);
					 event.setCancelled(true);
					 return;
				 }
			 }
		}
		
		// *** a ghost is attacked ***
		Entity entity = event.getEntity();
		if(entity.getClass().equals(CraftPlayer.class)) {
			Player player = (Player) entity;
			if(ghosts.isGhost(player)) {
				
				// *** check if a player tries to attack a ghost ***
				if (event instanceof EntityDamageByEntityEvent) {
					 Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
					 if (damager instanceof Player) {
						 Player attacker = (Player) damager;
						 DARMessages.cantAttackGhosts(attacker);
					 }
				}
				// *************************************************
				event.setCancelled(true);
			}
		}
	}
}
