package muCkk.DeathAndRebirth.Listener;

import java.io.File;

import muCkk.DeathAndRebirth.DARHandler;
import muCkk.DeathAndRebirth.DARShrines;
import muCkk.DeathAndRebirth.Config.DARProperties;
import muCkk.DeathAndRebirth.Messages.DARMessages;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.config.Configuration;

import com.citizens.npcs.NPCManager;

public class DAREntityListener extends EntityListener {

	private DARHandler ghosts;
	private DARProperties config;
	private DARMessages msg;
	private DARShrines shrines;
	
	public DAREntityListener(DARProperties config, DARMessages msg, DARHandler ghosts, DARShrines shrines) {
		this.config = config;
		this.msg = msg;
		this.ghosts = ghosts;
		this.shrines = shrines;
	}
	
	/**
	 * Checks for dying players
	 */
	public void onEntityDeath(EntityDeathEvent event) {		
		Entity entity = event.getEntity();
		// check if the world is enabled
		if(!config.isEnabled(entity.getWorld().getName())) {
			return;
		}		
		if(entity instanceof Player) {
			// *** ignoring NPCs from citizens ***
			if (config.isCitizensEnabled()) {
				if (checkForNPC(entity)) {
					return;
				}
			}
			// *****************************************
			
			Player player = (Player) entity;
			ghosts.died(player);
		}
	}
	
	private boolean checkForNPC(Entity entity) {
		if(NPCManager.isNPC(entity)) {
			return true;
		}
		// *** checking all names for evil npcs ... ***
		File namesFile = new File("plugins/Citizens/mobs.yml");
		Configuration yml;
		String player = ((Player) entity).getName();
		
		try {
            yml = new Configuration(namesFile);
            yml.load();
        
            String [] evilNames = yml.getString("evil.misc.names").split(",");
            String [] pirateNames = yml.getString("pirates.misc.names").split(",");
            
            for (String name : evilNames) {
				if (player.equalsIgnoreCase(name)) return true;
			}
            
            for (String name : pirateNames) {
				if (player.equalsIgnoreCase(name)) return true;
			}
		} catch (Exception e) {
        	System.out.println("[Death and Rebirth] Error while checking for NPCs");
        }
		
		return false;
	}
	/**
	 * Stops creatures from attacking ghosts
	 */
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		// check if the world is enabled
		if(!config.isEnabled(event.getEntity().getWorld().getName())) {
			return;
		}
		
		try {
			Entity target = event.getTarget();
			if(target instanceof Player) {
				Player player = (Player) target;
				if(ghosts.isGhost(player)) {
					event.setCancelled(true);
				}
			}
		}catch (NullPointerException e) {
			//TODO NullPointer: onEntityTarget - happens sometimes
		}
	}
	
	/**
	 * Preventing PvP with ghosts and ghosts from attacking monsters
	 */
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		// check if the world is enabled
		if(!config.isEnabled(event.getEntity().getWorld().getName())) {
			return;
		}
		
		// *** a ghost tries to attack ***
		if (event instanceof EntityDamageByEntityEvent) {
			 Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
			 if (damager instanceof Player) {
				 Player attacker = (Player) damager;
				 if(ghosts.isGhost(attacker)) {
					 msg.cantDoThat(attacker);
					 event.setCancelled(true);
					 return;
				 }
			 }
		}
		
		// *** a ghost gets damage ***
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			Player player = (Player) entity;
			if(ghosts.isGhost(player)) {
				
				// *** check if a player tries to attack a ghost ***
				if (event instanceof EntityDamageByEntityEvent) {
					 Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
					 if (damager instanceof Player) {
						 Player attacker = (Player) damager;
						 msg.cantAttackGhosts(attacker);
					 }
				}
				// *************************************************
				event.setCancelled(true);
			}
		}
	}
	
	/**
	 * Protects shrines from explosions
	 */
	public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
		String shrine = shrines.getClose(entity.getLocation());
		if (shrine != null) {
			event.setCancelled(true);
		}
	}
}
