package muCkk.DeathAndRebirth;

import muCkk.DeathAndRebirth.Config.DARProperties;
import muCkk.DeathAndRebirth.Listener.DARBlockListener;
import muCkk.DeathAndRebirth.Listener.DAREntityListener;
import muCkk.DeathAndRebirth.Listener.DARPlayerListener;
import muCkk.DeathAndRebirth.Messages.DARErrors;
import muCkk.DeathAndRebirth.Messages.DARMessages;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class DAR extends JavaPlugin {

	private String dir				=	"plugins/Death and Rebirth";
	private String dataDir			=	dir+"/data";
	private String propertiesFile	=	dir+"/config.txt";
	private String handlerFile		=	dataDir+"/ghosts";
	private String gravesFile		=	dataDir+"/graves";
	private String shrinesFile 		=	dataDir+"/shrines.yml";
	private boolean permissions;
	
	private DARHandler ghosts;
	private DARProperties config;
	private DARGraves graves;
	private DARShrines shrines;
	
	public  static PermissionHandler permissionHandler;
	  
	public void onDisable() {
		config.save();
		ghosts.save();
		graves.save();
		shrines.save();
	}

	public void onEnable() {
		
		config = new DARProperties(dir, propertiesFile);
		config.load();
		
		checkThirdPartyPlugins();
		permissions = setupPermissions();
		
		graves = new DARGraves(dataDir, gravesFile);
		graves.load();
		ghosts = new DARHandler(dataDir, handlerFile, config, graves, permissions);
		ghosts.load();
		shrines = new DARShrines(dataDir, shrinesFile);
		
		// *** Listener stuff ***
		PluginManager pm = getServer().getPluginManager();
		
		DARPlayerListener playerlistener = new DARPlayerListener(this, config, ghosts, shrines);
		DAREntityListener entityListener = new DAREntityListener(config, ghosts, shrines);
		DARBlockListener blockListener = new DARBlockListener(config, shrines,ghosts,graves);
		
		pm.registerEvent(Type.PLAYER_JOIN, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_RESPAWN, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_PICKUP_ITEM, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_PORTAL, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_MOVE, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerlistener, Priority.Low, this);
		
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Low, this);
		
		pm.registerEvent(Type.BLOCK_DAMAGE, blockListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Low, this);
	}
	
	private void checkThirdPartyPlugins() {
	// *** check for spout ****************************************
		Plugin spoutPlugin = getServer().getPluginManager().getPlugin("Spout");
		if (spoutPlugin != null) {
			DARErrors.foundSpout();
			DARMessages.setSpout(true);
			config.setSpout(true);
		}
		else {
			DARMessages.setSpout(false);
			config.setSpout(false);
		}
	// *** check for citizens ************************************
		Plugin citizensPlugin = getServer().getPluginManager().getPlugin("Citizens");
		if (citizensPlugin != null) {
			config.setCitizens(true);
		}
	// *** checking for nocheat *************************
		Plugin nocheatPlugin = getServer().getPluginManager().getPlugin("NoCheat");
		if(nocheatPlugin != null) {
			config.setNoCheat(true);
		}
	}
	
	private boolean setupPermissions() {
	    if (permissionHandler != null) {
	        return true;
	    }
	    
	    Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
	    
	    if (permissionsPlugin == null) {
	        return false;
	    }
	    
	    permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	    return true;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
	         player = (Player)sender;
		} 
		DARMessages.save(player);
		
		/**
		 * rebirth | reb
		 * resurrects a nearby player  
		 * or the player itself
		 */
		if(cmd.getName().equalsIgnoreCase("rebirth") || cmd.getName().equalsIgnoreCase("reb")) {			
			// *** permission check ***
			if(permissionHandler != null) {
				if (!DAR.permissionHandler.has(player, "dar.res")) {
					permissionHandler.addUserPermission(null, null, null);
				    DARMessages.noPermission();  
					return true;
				 }
			}
			try {
				Player target = sender.getServer().getPlayer(args[0]);
				if(ghosts.isGhost(target)) {
					ghosts.resurrect(player, target);
					return true;
				}
				else {
					DARMessages.playerNotDead(player, args[0]);
					return true;	
				}
				// *** player resurrects itself ***
			}catch (ArrayIndexOutOfBoundsException e) {
				if(!ghosts.isGhost(player)) {
					DARMessages.youAreNotDead(player);
					return true;
				}
				if(shrines.isOnShrine(player)) {
					ghosts.resurrect(player);
					return true;
				}
				else {
					if (config.isShrineOnlyEnabled()) {
						DARMessages.youHaveToStandOnShrine(player);
						return true;
					}
					Location loc = ghosts.getBoundShrine(player);
					if(loc == null) {
						DARMessages.souldNotBound(player);
						return true;
					}
					player.teleport(loc);
					ghosts.resurrect(player);					
					return true;
				
				}
			}
		}
		/**
		 * dar <add, rm, list, reb, reload, enable, disable, world, fly, shrinemode, ghostinteraction, ghostchat, versionCheck> <name>
		 * Errects, removes and lists shrines. Admins can resurrect any player.
		 */
		if(cmd.getName().equalsIgnoreCase("dar")) {
			
		// *** check permission ********************************************************************
			if (permissionHandler != null) {
				if (!DAR.permissionHandler.has(player, "dar.admin")) {
					DARMessages.noPermission();  
					return true;
				 }
			}
		// ** defaulting to OP system ***
			else {
				if(!player.isOp()) {
					DARMessages.noPermission();
					return true;
				}
			}
			
			String arg = "";
			String name = "";
			try {
				arg = args[0];
			}catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
			
		// *** reloading all config files **********************************************************
			if(arg.equalsIgnoreCase("reload")) {
				config.load();
				graves.load();
				ghosts.load();
				shrines.load();
				DARMessages.reloadComplete();
				return true;
			}

		// *** giving the world name *******************************************************************
			if(arg.equalsIgnoreCase("world")) {
				String world = player.getWorld().getName();
				player.sendMessage("You are in world: "+world);
				String bool = "";
				if (config.isEnabled(world)) {
					 bool = "enabled";
				}
				else {
					bool = "disabled";
				}
				player.sendMessage("Death and Rebirth is "+bool);
				return true;
			}
		// *** enabling and disabling support for the world *******************************************
			if (arg.equalsIgnoreCase("enable")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				config.setBoolean(name, true);
				DARMessages.worldEnabled(name);
				return true;
			}
			
			if (arg.equalsIgnoreCase("disable")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				config.setBoolean(name, false);
				DARMessages.worldDisabled(name);
				return true;
			}
		// *** toggling dropping *************************************************************************
			if (arg.equalsIgnoreCase("dropping")) {
				if(config.isDroppingEnabled()) {
					config.setDropping(false);
					DARMessages.droppingToggle("disabled.");
				}
				else {
					config.setDropping(true);
					DARMessages.droppingToggle("enabled.");
				}
				return true;
			}
		// *** toggling version checking ******************************************************************
			if (arg.equalsIgnoreCase("versioncheck")) {
				if(config.isVersionCheckEnabled()) {
					config.setVersionCheck(false);
					DARMessages.versionCheckToggle("disabled.");
				}
				else {
					config.setVersionCheck(true);
					DARMessages.versionCheckToggle("enabled.");
				}
				return true;
			}
		// *** toggling fly mode ***************************************************************************
			if (arg.equalsIgnoreCase("fly")) {
				if(config.isFlyingEnabled()) {
					config.setFly(false);
					DARMessages.flyModeToggle("disabled.");
				}
				else {
					config.setFly(true);
					DARMessages.flyModeToggle("enabled.");
				}
				return true;
			}
		// *** toggling shrine  mode ***************************************************************************
			if (arg.equalsIgnoreCase("shrinemode")) {
				if(config.isShrineOnlyEnabled()) {
					config.setShrineOnly(false);
					DARMessages.shrineModeToggle("disabled.");
				}
				else {
					config.setShrineOnly(true);
					DARMessages.shrineModeToggle("enabled.");
				}
				return true;
			}
		// *** toggling blockGhostInteraction ********************************************************************
			if (arg.equalsIgnoreCase("ghostinteraction")) {
				if(config.isBlockGhostInteractionEnabled()) {
					config.setBlockGhostInteraction(false);
					DARMessages.blockGhostInteractionToggle("disabled.");
				}
				else {
					config.setBlockGhostInteraction(true);
					DARMessages.blockGhostInteractionToggle("enabled.");
				}
				return true;
			}
		// *** toggling ghost chat **********************************************************************************
			if (arg.equalsIgnoreCase("ghostchat")) {
				if(config.isGhostChatEnabled()) {
					config.setGhostChat(false);
					DARMessages.ghostChatToggle(player, "disabled.");
				}
				else {
					config.setGhostChat(true);
					DARMessages.ghostChatToggle(player, "enabled.");
				}
				return true;
			}
			
			
			
			
		// *** adding shrines *********************************************************************************
			if (arg.equalsIgnoreCase("add")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				if(shrines.exists(name, player.getWorld().getName())) {
					DARMessages.nameAlreadyExists(player);
					return true;
				}
				Block tb = player.getTargetBlock(null,5);
				
				String shrineClose = shrines.getClose(player.getLocation());
				if (shrineClose != null) {
					if(shrines.isShrineArea(shrineClose, tb, player)) {
						DARMessages.shrineAlreadyAtLoc(player);
						return true;
					}
				}
				
				// tb, rock1, rock2, rock3
				// NW, N, NE
				// SW, S, SE
				// W, E
				Block rock1, rock2, rock3,
					platformNW, platformN, platformNE,
					platformSW, platformS, platformSE,
					platformW, platformE;
				
				 
				
				rock1 = tb.getRelative(BlockFace.UP, 1);
				rock2 = tb.getRelative(BlockFace.UP, 2);
				rock3 = tb.getRelative(BlockFace.UP, 3);
				
				platformNW	=	tb.getRelative(BlockFace.NORTH_WEST, 1);
				platformN	=	tb.getRelative(BlockFace.NORTH, 1);
				platformNE	=	tb.getRelative(BlockFace.NORTH_EAST, 1);
				
				platformSW	=	tb.getRelative(BlockFace.SOUTH_WEST, 1);
				platformS	=	tb.getRelative(BlockFace.SOUTH, 1);
				platformSE	=	tb.getRelative(BlockFace.SOUTH_EAST, 1);
				
				platformW	=	tb.getRelative(BlockFace.WEST, 1);
				platformE	=	tb.getRelative(BlockFace.EAST, 1);
				
				Block [] shrineBlocks = { tb, rock1, rock2, rock3,
						platformNW, platformN, platformNE,
						platformSW, platformS, platformSE,
						platformW, platformE };
				
				DARShrine shrine = new DARShrine(shrineBlocks);
				shrines.addShrine(shrine, name);
					
				tb.setTypeId(49);
				rock1.setTypeId(49);
				rock2.setTypeId(49);
				rock3.setTypeId(49);
				
				platformNW.setTypeId(89);
				platformN.setTypeId(89);
				platformNE.setTypeId(89);
				platformSW.setTypeId(89);
				platformS.setTypeId(89);
				platformSE.setTypeId(89);
				platformW.setTypeId(89);
				platformE.setTypeId(89);
				
				return true;
			}
			
		// *** removing shrines *****************************************************************
			if (arg.equalsIgnoreCase("rm")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				if(!shrines.exists(name, player.getWorld().getName())) {
					DARMessages.nameNotFound();
					return true;
				}
				shrines.removeShrine(name, player);
				return true;
			}
		// *** listing shrines *********************************************************
			if(arg.equalsIgnoreCase("list")) {
				int page;
				try {
					page = Integer.parseInt(args[1]);
				}catch (ArrayIndexOutOfBoundsException e) {
					page = 1;
				}
				shrines.list(player, page);
				return true;
			}
			
		// *** resurrecting players ***************************************************
			if (arg.equalsIgnoreCase("reb")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				
				Player target = getServer().getPlayer(name);
			// *** check if the player is dead ***
				if (!ghosts.isGhost(target)) {
					DARMessages.playerNotDead(player, name);
					return true;
				}
				
			// *** resurrection ***
				ghosts.resurrect(target);
				DARMessages.youResurrected(target);
				return true;
			}
		}
		return false;		
	}

}
