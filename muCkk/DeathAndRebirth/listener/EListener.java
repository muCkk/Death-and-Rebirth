package muCkk.DeathAndRebirth.listener;

import java.util.List;
import java.util.Random;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.ghost.Ghosts;
import muCkk.DeathAndRebirth.ghost.Shrines;
import muCkk.DeathAndRebirth.messages.Messages;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.citizensnpcs.api.CitizensAPI;

public class EListener implements Listener {

	//private static final Logger log = Logger.getLogger("Minecraft"); Used for citizens 1
	private DAR plugin;
	private Ghosts ghosts;
	private Shrines shrines;
	
	public EListener(DAR plugin, Ghosts ghosts, Shrines shrines) {
		this.plugin = plugin;
		this.ghosts = ghosts;
		this.shrines = shrines;
	}

	/**
	 * Checks for dying players
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {		
		Entity entity = event.getEntity();
		if(!(entity instanceof Player)) return;
		Player player = (Player) entity;
		
		// check for ignore	
		if(plugin.hasPermIgnore(player))
			return;
		
		// check if the world is enabled
		if(!plugin.getConfig().getBoolean(entity.getWorld().getName()))
			return;
		
		//Checks if player is in a MobArena
    	if(plugin.getConfig().getBoolean("MOBARENA_ENABLED") && DAR.getAM().getArenaWithPlayer(player) != null) return;	

		
		// check for citizen NPCs
		if(plugin.getConfig().getBoolean("CITIZENS_ENABLED"))
			if(checkForNPC(entity)) return;

		
		// check for death in the void 
		String damageCause = "";
		try {
			damageCause = entity.getLastDamageCause().getCause().toString();
			}catch (NullPointerException e) {
				// happens if there is no cause (/kill, /suicide ...)
			}
		if(plugin.getConfig().getBoolean("VOID_DEATH") && damageCause.equalsIgnoreCase("VOID")) return;
		
		// other plugins which avoid death
		if(player.getHealth() > 0) return;

		//Defines Location of death
		Location loc = player.getLocation();
		Block block = player.getWorld().getBlockAt(loc);	
		ghosts.getCustomConfig().set("players."+player.getName() +"."+block.getWorld().getName() +".location.x", block.getX());
		ghosts.getCustomConfig().set("players."+player.getName() +"."+block.getWorld().getName() +".location.y", block.getY());
		ghosts.getCustomConfig().set("players."+player.getName() +"."+block.getWorld().getName() +".location.z", block.getZ());
		
		if(plugin.getConfig().getBoolean("HARDCORE") && plugin.getConfig().getInt("TIMER") > 0)
		{
			long startTime = System.currentTimeMillis();
			ghosts.getCustomConfig().set("players."+player.getName() +"."+block.getWorld().getName() +".starttime", startTime);
		}
		
	// checking items
		List<ItemStack> drops = event.getDrops();
		PlayerInventory inv = player.getInventory();

		// PVP kill
		if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent && plugin.getConfig().getBoolean("PVP_DROP") && drops.size() > 0) {
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
				 ghosts.died(player, inv, loc, true);
				 return;
			 }
		}
		
		// dropping OFF   OR    dar.nodrop 
		if (!plugin.getConfig().getBoolean("DROPPING") || plugin.hasPermNoDrop(player)) {
			drops.clear();
			ghosts.died(player, inv, loc, false);
			return;
		}
		ghosts.died(player, inv, loc, false);
	}
		
	
	/**
	 * Stops creatures from attacking ghosts
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityTarget(EntityTargetEvent event) {		
		// check if the world is enabled
		if(!plugin.getConfig().getBoolean(event.getEntity().getWorld().getName())) {
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
	
	/*
	 * Prevents ghosts from losing hunger points
	 */
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		// check if the world is enabled
		if(!plugin.getConfig().getBoolean(event.getEntity().getWorld().getName())) {
			return;
		}
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			Player player = (Player) entity;
			if(ghosts.isGhost(player)) event.setCancelled(true);
		}
	}
	
	/**
	 * Preventing PvP with ghosts and ghosts from attacking monsters
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		
		// check if the world is enabled
		if(!plugin.getConfig().getBoolean(event.getEntity().getWorld().getName())) {
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
		//If the players fall into the void while dead he gets teleported back to...
	    if(event.getCause().toString().equals("VOID"))
		{
			//... his death location if cropse spawning is enabled
			if (!plugin.getConfig().getBoolean("CORPSE_SPAWNING"))
			{
				player.teleport(ghosts.getLocation(player, player.getWorld().getName()));
			}
			//... the next shrine if he needs to resurrect at his grave
			else
			{
				player.teleport(shrines.getNearestShrineSpawn(player.getLocation()));
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
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		String shrine = shrines.getClose(event.getLocation());
		if (shrine != null) {
			event.setCancelled(true);
		}
	}
	
// *** private methods *******************************************************************
	private boolean checkForNPC(Entity entity) {
		
		if(CitizensAPI.getNPCRegistry().isNPC(entity))
			return true;
		
		/*if(CitizensManager.isNPC(entity)) {
			return true;
		}
		// *** checking all names for evil npcs ... ***
		File namesFile = new File("plugins/Citizens/mobs.yml");
		FileConfiguration yml;
		String player = ((Player) entity).getName();
		
		try {
            yml = YamlConfiguration.loadConfiguration(namesFile);//new Configuration(namesFile);
            
            String [] evilNames = null;
            String [] pirateNames = null;
            
            try {
	            evilNames = yml.getString("evil.misc.names").split(",");
	            pirateNames = yml.getString("pirates.misc.names").split(",");
            }catch (NullPointerException e) {
				//nullpointer pirate names
			}
            
            if(evilNames != null) {
		        for (String name : evilNames) {
					if (player.equalsIgnoreCase(name)) return true;
				}
            }
            if(pirateNames != null)  {
	            for (String name : pirateNames) {
					if (player.equalsIgnoreCase(name)) return true;
				}
            }
		} catch (Exception e) {
			log.info("[Death and Rebirth] Error while checking for NPCs");
        	e.printStackTrace();
        }*/
		return false;
	}
}
