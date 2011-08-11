package muCkk.DeathAndRebirth;

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
	
	
	private DARHandler ghosts;
	private DARProperties config;
	private DARGraves graves;
	private DARShrines shrines;
	
	public static PermissionHandler permissionHandler;
	  
	public void onDisable() {
		config.save();
		ghosts.save();
		graves.save();
		shrines.save();
	}

	public void onEnable() {
		config = new DARProperties(dir, propertiesFile);
		config.load();
		graves = new DARGraves(dataDir, gravesFile);
		graves.load();
		ghosts = new DARHandler(dataDir, handlerFile, config, graves);
		ghosts.load();
		shrines = new DARShrines(dataDir, shrinesFile);
		
		setupPermissions();
		
		PluginManager pm = getServer().getPluginManager();
		
		DARPlayerListener playerlistener = new DARPlayerListener(ghosts, shrines);
		DAREntityListener entityListener = new DAREntityListener(ghosts);
		DARBlockListener blockListener = new DARBlockListener(shrines,ghosts,graves);
		
		pm.registerEvent(Type.PLAYER_JOIN, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_RESPAWN, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_PICKUP_ITEM, playerlistener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerlistener, Priority.Low, this);
		
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Low, this);
		
		pm.registerEvent(Type.BLOCK_DAMAGE, blockListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Low, this);
	}
	
	private void setupPermissions() {
	    if (permissionHandler != null) {
	        return;
	    }
	    
	    Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
	    
	    if (permissionsPlugin == null) {
	        return;
	    }
	    
	    permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		/**
		 * resurrect | res
		 * resurrects a nearby player  
		 * or the player itself
		 */
		if(cmd.getName().equalsIgnoreCase("resurrect") || cmd.getName().equalsIgnoreCase("res")) {			
			// *** permission check ***
			if (!DAR.permissionHandler.has(player, "dar.res")) {
			    DARMessages.noPermission(player);  
				return true;
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
		 * shrine <add, rm, list> <name>
		 * Errects a shrine at the targeted location
		 */
		if(cmd.getName().equalsIgnoreCase("shrine")) {
			if (!DAR.permissionHandler.has(player, "dar.shrine")) {
			    DARMessages.noPermission(player);  
				return true;
			  }
			String arg = "";
			String name = "";
			try {
				arg = args[0];
			}catch (NullPointerException e) {
				return false;
			}
			if (arg.equalsIgnoreCase("add")) {
				try {
					name = args[1];
				}catch (NullPointerException e) {
					return false;
				}
				if(shrines.exists(name)) {
					DARMessages.nameAlreadyExists(player);
					return true;
				}
				Block tb = player.getTargetBlock(null,5);
				
				String shrineClose = shrines.getClose(player);
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
			if (arg.equalsIgnoreCase("rm")) {
				try {
					name = args[1];
				}catch (NullPointerException e) {
					return false;
				}
				if(!shrines.exists(name)) {
					DARMessages.nameNotFound(player);
					return true;
				}
				shrines.removeShrine(name, player);
				return true;
			}
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
		}
		return false;		
	}

}
