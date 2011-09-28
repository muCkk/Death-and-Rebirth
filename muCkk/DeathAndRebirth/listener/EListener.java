package muCkk.DeathAndRebirth.listener;

import java.io.File;
import java.util.List;
import java.util.Random;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.config.CFG;
import muCkk.DeathAndRebirth.config.Config;
import muCkk.DeathAndRebirth.ghost.Ghosts;
import muCkk.DeathAndRebirth.ghost.Shrines;
import muCkk.DeathAndRebirth.messages.Messages;
import muCkk.DeathAndRebirth.otherPlugins.Perms;

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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;

import net.citizensnpcs.api.CitizensManager;

public class EListener extends EntityListener {

	private DAR plugin;
	private Ghosts ghosts;
	private Config config;
	private Shrines shrines;
	
	public EListener(DAR plugin, Config config, Ghosts ghosts, Shrines shrines) {
		this.plugin = plugin;
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
		
		// other plugins which avoid death - for example mob arena
		if (player.getHealth() > 0) return;
		
	// check for death in the void 
		String damageCause = "";
		try {
			damageCause = entity.getLastDamageCause().getCause().toString();
			}catch (NullPointerException e) {
				// happens if there is no cause (/kill, /suicide ...)
			}
	// check if the world is enabled
		if(!config.isEnabled(entity.getWorld().getName()) ||  damageCause.equalsIgnoreCase("VOID")) {
			return;
		}
	// check for ignore	
		if (Perms.hasPermission(player, "dar.ignore")) {
			return;
		 }
		
	// check for citizen NPCs
		if (config.getBoolean(CFG.CITIZENS_ENABLED)) {
			if (checkForNPC(entity)) return;
		}
		
	// checking items
		List<ItemStack> drops = event.getDrops();
		PlayerInventory inv = player.getInventory();
		// PVP kill
		if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent && config.getBoolean(CFG.PVP_DROP) && drops.size() > 0) {
			 Entity damager = ((EntityDamageByEntityEvent)entity.getLastDamageCause()).getDamager();
			 if (damager instanceof Player) {
			// find one random item which will be dropped
				 ItemStack droppedItem = null;
				 Random generator = new Random(827823476);
				 int nr = generator.nextInt(drops.size());
				 int stopper = 0;
			
				 droppedItem = drops.get(nr);
				 
				 while(droppedItem == null && stopper < 30) {
					 nr = generator.nextInt(drops.size());
					 droppedItem = drops.get(nr);
					 stopper++;
				 }
				 
				 player.getWorld().dropItemNaturally(player.getLocation(), droppedItem);
				 inv.remove(droppedItem);
				 drops.clear();				 
				 ghosts.died(player, inv);
				 return;
			 }
		}
		
		// dropping OFF   OR    dar.nodrop 
		if (!config.getBoolean(CFG.DROPPING) || Perms.hasPermission(player, "dar.nodrop")) {
			drops.clear();
			ghosts.died(player, inv);
			return;
		}
		ghosts.died(player, inv);
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
					 plugin.message.send(attacker, Messages.cantDoThat);
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
						 plugin.message.send(attacker, Messages.cantDoThat);
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
						 plugin.message.send(attacker, Messages.cantAttackGhosts);
					 }
				}
				// *************************************************
				// TODO evil citizens do damage
				event.setDamage(0);
				event.setCancelled(true);
			}
		}
	}
	
	/**
	 * Protects shrines from explosions
	 */
	public void onEntityExplode(EntityExplodeEvent event) {
		String shrine = shrines.getClose(event.getLocation());
		if (shrine != null) {
			event.setCancelled(true);
		}
	}
	
	
// *** private methods *******************************************************************
	private boolean checkForNPC(Entity entity) {
		if(CitizensManager.isNPC(entity)) {
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
        	e.printStackTrace();
        }
		
		return false;
	}
}
