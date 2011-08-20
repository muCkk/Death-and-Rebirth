package muCkk.DeathAndRebirth.listener;

import java.io.File;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.DARHandler;
import muCkk.DeathAndRebirth.config.DARProperties;
import muCkk.DeathAndRebirth.messages.DARMessages;
import muCkk.DeathAndRebirth.messages.Messages;
import muCkk.DeathAndRebirth.shrines.DARShrines;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import com.citizens.npcs.NPCManager;

public class DAREntityListener extends EntityListener {

	private DARHandler ghosts;
	private DARProperties config;
	private DARShrines shrines;
	private DARMessages message;
	
	public DAREntityListener(DARProperties config, DARHandler ghosts, DARShrines shrines, DARMessages message) {
		this.config = config;
		this.ghosts = ghosts;
		this.shrines = shrines;
	}
	
	/**
	 * Checks for dying players
	 */
	public void onEntityDeath(EntityDeathEvent event) {		
		Entity entity = event.getEntity();
		if(!(entity instanceof Player)) return;
		Player player = (Player) entity;
	
	// check if the world is enabled
		if(!config.isEnabled(entity.getWorld().getName()) || entity.getLastDamageCause().getCause().toString().equalsIgnoreCase("VOID")) {
			return;
		}
	// check for ignore
		if (DAR.permissionHandler != null) {
			if (!DAR.permissionHandler.has(player, "dar.ignore") || !DAR.permissionHandler.has(player, "dar.res")) {
				return;
			 }
		}
		
	// *** ignoring NPCs from citizens ***
		if (config.isCitizensEnabled()) {
			if (checkForNPC(entity)) {
				return;
			}
		}		

	// checking items
		ItemStack [] playerDrops = new ItemStack[event.getDrops().size()];
		if (!config.isDroppingEnabled()) {
			int i = 0;
			for (ItemStack item : event.getDrops()) {
				if (item == null) continue;
				playerDrops[i] = item;
				i++;
			}
			event.getDrops().clear();
		}
		ghosts.died(player, playerDrops);
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
			// this happens sometimes, i guess when mobs target something
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
					 message.send(attacker, Messages.cantDoThat);
					 event.setCancelled(true);
					 return;
				 }
			 }
			 // attacks with a bow
			 if (damager instanceof Projectile) {
				 Projectile arrow = (Projectile) damager;
				 LivingEntity shooter = arrow.getShooter();
				 if (shooter instanceof Player) {
					 Player attacker = (Player) shooter;
					 if (ghosts.isGhost(attacker)) {
						 message.send(attacker, Messages.cantDoThat);
						 event.setCancelled(true);
						 return;
					 }
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
						 message.send(attacker, Messages.cantAttackGhosts);
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
	
	
// *** private methods *******************************************************************
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
}
