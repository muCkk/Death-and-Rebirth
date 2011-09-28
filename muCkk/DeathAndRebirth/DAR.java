package muCkk.DeathAndRebirth;

import muCkk.DeathAndRebirth.config.CFG;
import muCkk.DeathAndRebirth.config.Config;
import muCkk.DeathAndRebirth.config.DARProperties;
import muCkk.DeathAndRebirth.ghost.Ghosts;
import muCkk.DeathAndRebirth.ghost.Graves;
import muCkk.DeathAndRebirth.ghost.Shrines;
import muCkk.DeathAndRebirth.listener.BListener;
import muCkk.DeathAndRebirth.listener.EListener;
import muCkk.DeathAndRebirth.listener.PListener;
import muCkk.DeathAndRebirth.listener.SListener;
import muCkk.DeathAndRebirth.messages.Messenger;
import muCkk.DeathAndRebirth.messages.Messages;
import muCkk.DeathAndRebirth.otherPlugins.DARConomy;
import muCkk.DeathAndRebirth.otherPlugins.DARmcMMO;
import muCkk.DeathAndRebirth.otherPlugins.Perms;
import muCkk.DeathAndRebirth.otherPlugins.DARSpout;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin; 

public class DAR extends JavaPlugin {

	private String dir				=	"plugins/Death and Rebirth";
	private String dataDir			=	dir+"/data";	
	
	private Ghosts ghosts;
	private DARProperties oldConfig;
	private Config config;
	private Graves graves;
	private Shrines shrines;
	
	public static Perms perms;
	public Messenger message;
	public  DARSpout darSpout;
	public DARConomy darConomy;
	public DARmcMMO darmcmmo = null;
	public PluginManager pm;
	
	public void onDisable() {
		ghosts.onDisable(this);
		getServer().getScheduler().cancelTasks(this);
		config.save();
		ghosts.save();
		graves.save();
		shrines.save();
	}

	public void onEnable() {
	// Config
		oldConfig = new DARProperties(dir);
		config = new Config(dir, oldConfig);
		if (oldConfig.configExists() && !config.isConverted()) config.convert();
		
	// DAR Classes
		darSpout = new DARSpout(config, dataDir);
		message = new Messenger(dir);
		Perms.setup(this);

		graves = new Graves(dataDir, config);
		ghosts = new Ghosts(this, dir, config, graves);
		darSpout.setGhosts(ghosts);
		shrines = new Shrines(this, dataDir, config);

	// Listener
		pm = getServer().getPluginManager();
		
		PListener playerlistener = new PListener(this, config, ghosts, shrines);
		EListener entityListener = new EListener(this, config, ghosts, shrines);
		BListener blockListener = new BListener(this, config, shrines,ghosts,graves);
		SListener serverListener = new SListener(this, config);
		serverListener.checkForPlugins();
		
		pm.registerEvent(Type.PLAYER_RESPAWN, playerlistener, Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_PICKUP_ITEM, playerlistener, Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_JOIN, playerlistener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerlistener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_PORTAL, playerlistener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, playerlistener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_MOVE, playerlistener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerlistener, Priority.Lowest, this);
		
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Lowest, this);
		
		pm.registerEvent(Type.BLOCK_DAMAGE, blockListener, Priority.Lowest, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Lowest, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Lowest, this);

		pm.registerEvent(Type.PLUGIN_ENABLE, serverListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLUGIN_DISABLE, serverListener, Priority.Monitor, this);
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
	         player = (Player)sender;
		} 
		
		/**
		 * mygrave
		 * tells the player where his grave is
		 */
		if(cmd.getName().equalsIgnoreCase("mygrave")) {
			player.sendMessage(ghosts.getGrave(player));
			return true;
		}
		
		/**
		 * rebirth | reb
		 * resurrects a nearby player  
		 */
		if(cmd.getName().equalsIgnoreCase("rebirth") || cmd.getName().equalsIgnoreCase("reb")) {
			// resurrect target
			if (args.length > 0) {
				// permission check
				if (!Perms.hasPermission(player, "dar.reb")) {
					message.send(player, Messages.noPermission);
					return true;
				 }
				Player target = sender.getServer().getPlayer(args[0]);
				if(ghosts.isGhost(target)) {
					if(player.getName().equalsIgnoreCase(target.getName())) {
						message.send(player, Messages.cantResurrectYourself);
						return true;
					}
					ghosts.resurrect(player, target);
					return true;
				}
				else {
					message.send(player, Messages.playerNotDead);
					return true;	
				}
			}
			// self-resurrection
			else {
				if(!ghosts.isGhost(player)) {
					message.send(player, Messages.youAreNotDead);
					return true;
				}
				// normal mode
				if (!config.getBoolean(CFG.SHRINE_ONLY)) {
					ghosts.selfRebirth(player, shrines);
					return true;
				}
				// shrine only
				else {
					if(shrines.isOnShrine(player)) {
						ghosts.resurrect(player);
						return true;
					}
					else {	
						message.send(player, Messages.haveToStandOnShrine);
						return true;
						}
				}
			}
		}
		
		/**
		 * shrine <add, rm, list, pos1, pos2, select, binding> <name>
		 */
		if (cmd.getName().equalsIgnoreCase("shrine")) {
		// check permission
			if (!Perms.hasPermission(player, "dar.admin")) {
				message.send(player, Messages.noPermission);
				return true;
			 }
			
			String arg = "";
			String name = "";
			try {
				arg = args[0];
			}catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
			
			
		// selecting a shrine area
			if (arg.equalsIgnoreCase("pos1") || arg.equals("pos2")) {
				Block tb = player.getTargetBlock(null, 100);
				if (arg.equalsIgnoreCase("pos1")) {
					shrines.setSel1(tb.getLocation());
					player.sendMessage("Position 1 set");
					return true;
				}
				if (arg.equalsIgnoreCase("pos2")){
					shrines.setSel2(tb.getLocation());
					player.sendMessage("Position 2 set");
					return true;
				}
			}
			
			if (arg.equalsIgnoreCase("select")) {
				shrines.setPlayer(player.getName());
				if (shrines.isSelModeEnable()) {
					shrines.setSelectionMode(false);
					player.sendMessage("Selection mode disabled");
				}
				else {
					shrines.setSelectionMode(true);
					player.sendMessage("Selection mode enabled");
				}
				return true;
			}
				
			if(arg.equalsIgnoreCase("update")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				shrines.update(player, name);
				return true;
			}

		// adding shrines
			if (arg.equalsIgnoreCase("add")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				if(shrines.exists(name, player.getWorld().getName())) {
					message.sendChat(player, Messages.nameAlreadyExists);
					return true;
				}
				
				if(!shrines.checkSelection()) {
					message.sendChat(player, Messages.noSelectionMade);
					return true;
				}
				
				if (shrines.locationIsAlreadyShrine()) {
					message.sendChat(player, Messages.shrineAlreadyThere);
					return true;
				}
				shrines.addShrine(name);
				message.sendChat(player, Messages.shrineAdded);
				return true;
			}
		// adding a spawn
			if(arg.equalsIgnoreCase("setSpawn")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				if(!shrines.exists(name, player.getWorld().getName())) {
					message.sendChat(player, Messages.nameNotFound);
					return true;
				}
				shrines.setSpawn(name, player);
				return true;
			}
		// removing shrines
			if (arg.equalsIgnoreCase("rm")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				if(!shrines.exists(name, player.getWorld().getName())) {
					message.sendChat(player, Messages.nameNotFound);
					return true;
				}
				shrines.removeShrine(name, player);
				message.sendChat(player, Messages.shrineRemoved);
				return true;
			}
		// listing shrines
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
		// toggling soulbinding
			if (arg.equalsIgnoreCase("binding")) {
				String shrineName = "";
				try {
					shrineName = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				message.sendChat(player, Messages.bindingToggle, shrines.setBinding(shrineName, player.getWorld().getName()));
				return true;
			}
		}
		
		
		/**
		 * dar <reb, reload, enable, disable, world, fly, shrinemode, ghostinteraction, ghostchat, dropping, versionCheck, lightningD, lightningD, invis> <arg>
		 * Errects, removes and lists shrines. Admins can resurrect any player.
		 */
		if(cmd.getName().equalsIgnoreCase("dar")) {
			
		// check permission
			if (sender instanceof Player) {
				if (!Perms.hasPermission(player, "dar.admin")) {
					message.send(player, Messages.noPermission);
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
			
		// reloading all config files
			if(arg.equalsIgnoreCase("reload")) {
				config.load();
				graves.load();
				ghosts.load();
				shrines.load();
				message.sendChat(player, Messages.reloadComplete);
				return true;
			}

		// giving the world name
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
		// resurrecting players
			if (arg.equalsIgnoreCase("reb")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				
				Player target = getServer().getPlayer(name);
			// check if the player is dead
				if (!ghosts.isGhost(target)) {
					message.send(player, Messages.playerNotDead);
					return true;
				}
				
			// resurrection 
				ghosts.resurrect(target);
				return true;
			}
		// world toggle
			if (arg.equalsIgnoreCase("enable") || arg.equalsIgnoreCase("disable")) {
				if (args.length == 1) name = player.getWorld().getName();
				if (args.length > 1) name = args[1];
				// worlds with spaces
				if (args.length>2) {
					for (int i=2; i<args.length; i++) {
						name = name +" "+args[i];
					}
				}
				if (arg.equalsIgnoreCase("enable")) {
					config.set(name, true);
					message.sendChat(player, Messages.worldEnabled, " "+name);
				}
				else {
					config.set(name, false);
					message.sendChat(player, Messages.worldDisabled, " "+name);
				}
				return true;
			}
			// toggles
			if (arg.equalsIgnoreCase("invis")) {
				if(toggle(CFG.INVISIBILITY)) message.sendChat(player, Messages.invisToggle, " disabled");
				else message.sendChat(player, Messages.invisToggle, " enabled");
			}
			if (arg.equalsIgnoreCase("dropping")) {
				if(toggle(CFG.DROPPING)) message.sendChat(player, Messages.droppingToggle, " disabled");
				else message.sendChat(player, Messages.droppingToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("pvpdrop")) {
				if(toggle(CFG.PVP_DROP)) message.sendChat(player, Messages.pvpDroppingToggle, " disabled");
				else message.sendChat(player, Messages.pvpDroppingToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("versioncheck")) {
				if(toggle(CFG.VERSION_CHECK)) message.sendChat(player, Messages.versionCheckToggle, " disabled");
				else message.sendChat(player, Messages.versionCheckToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("fly")) {
				if(toggle(CFG.FLY)) message.sendChat(player, Messages.flymodeToggle, " disabled");
				else message.sendChat(player, Messages.flymodeToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("shrinemode")) {
				if(toggle(CFG.SHRINE_ONLY)) message.sendChat(player, Messages.shrinemodeToggle, " disabled");
				else message.sendChat(player, Messages.shrinemodeToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("ghostinteraction")) {
				if(toggle(CFG.BLOCK_GHOST_INTERACTION)) message.sendChat(player, Messages.blockghostToggle, " disabled");
				else message.sendChat(player, Messages.blockghostToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("ghostchat")) {
				if(toggle(CFG.GHOST_CHAT)) message.sendChat(player, Messages.chatToggle, " disabled");
				else message.sendChat(player, Messages.chatToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("lightningD")) {
				if(toggle(CFG.LIGHTNING_DEATH)) message.sendChat(player, Messages.lightningDT, " disabled");
				else message.sendChat(player, Messages.lightningDT, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("lightningR")) {
				if(toggle(CFG.LIGHTNING_REBIRTH)) message.sendChat(player, Messages.lightningRT, " disabled");
				else message.sendChat(player, Messages.lightningRT, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("signs")) {
				if(toggle(CFG.GRAVE_SIGNS)) message.sendChat(player, Messages.signsToggle, " disabled");
				else message.sendChat(player, Messages.signsToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("spawn")) {
				if(toggle(CFG.CORPSE_SPAWNING)) message.sendChat(player, Messages.spawningToggle, " disabled");
				else message.sendChat(player, Messages.spawningToggle, " enabled");
				return true;
			}
		}
		return false;		
	}
	
	public boolean toggle(CFG node) {
		if(config.getBoolean(node)) {
			config.set(node, false);
			return false;
		}
		else {
			config.set(node, true);
			return true;
		}
	}
}
