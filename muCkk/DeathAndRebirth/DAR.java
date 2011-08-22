package muCkk.DeathAndRebirth;

import muCkk.DeathAndRebirth.config.DARProperties;
import muCkk.DeathAndRebirth.ghost.DARGhosts;
import muCkk.DeathAndRebirth.ghost.DARGraves;
import muCkk.DeathAndRebirth.ghost.DARShrines;
import muCkk.DeathAndRebirth.listener.DARBlockListener;
import muCkk.DeathAndRebirth.listener.DAREntityListener;
import muCkk.DeathAndRebirth.listener.DARPlayerListener;
import muCkk.DeathAndRebirth.messages.DARErrors;
import muCkk.DeathAndRebirth.messages.DARMessages;
import muCkk.DeathAndRebirth.messages.Messages;
import muCkk.DeathAndRebirth.otherPlugins.Perms;
import muCkk.DeathAndRebirth.otherPlugins.Spout;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DAR extends JavaPlugin {

	private String dir				=	"plugins/Death and Rebirth";
	private String dataDir			=	dir+"/data";
	private String propertiesFile	=	dir+"/config.txt";
	private String messageFile 		=	dir+"/messages.yml";
	private String handlerFile		=	dataDir+"/ghosts";
	private String gravesFile		=	dataDir+"/graves";
	private String shrinesFile 		=	dataDir+"/shrines.yml";
	
	private DARGhosts ghosts;
	private DARProperties config;
	private DARGraves graves;
	private DARShrines shrines;
	private Spout spout;
	private DARMessages message;
	public static Perms perms;
	  
	public void onDisable() {
		ghosts.onDisable(this);
		config.save();
		ghosts.save();
		graves.save();
		shrines.save();
	}

	public void onEnable() {
		
		config = new DARProperties(dir, propertiesFile);
		config.load();
		spout = new Spout(config);
		message = new DARMessages(dataDir, messageFile);
		message.load();
		checkThirdPartyPlugins();
		Perms.setup(this);
		
		graves = new DARGraves(dataDir, gravesFile);
		graves.load();
		ghosts = new DARGhosts(dataDir, handlerFile, config, graves, spout, message);
		ghosts.load();
		spout.setGhosts(ghosts);
		shrines = new DARShrines(dataDir, shrinesFile, message, config);
		
	// Listener
		PluginManager pm = getServer().getPluginManager();
		
		DARPlayerListener playerlistener = new DARPlayerListener(this, config, ghosts, shrines, spout,message);
		DAREntityListener entityListener = new DAREntityListener(config, ghosts, shrines,message);
		DARBlockListener blockListener = new DARBlockListener(config, shrines,ghosts,graves,message);
		
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
	}
	
	private void checkThirdPartyPlugins() {
	// check for spout
		Plugin spoutPlugin = getServer().getPluginManager().getPlugin("Spout");
		if (spoutPlugin != null) {
			DARErrors.foundSpout();
			message.setSpout(true);
			config.setSpout(true);
		}
		else {
			message.setSpout(false);
			config.setSpout(false);
		}
	// check for citizens
		Plugin citizensPlugin = getServer().getPluginManager().getPlugin("Citizens");
		if (citizensPlugin != null)	config.setCitizens(true);
		else						config.setCitizens(false);
	// checking for nocheat
		Plugin nocheatPlugin = getServer().getPluginManager().getPlugin("NoCheat");
		if(nocheatPlugin != null)	config.setNoCheat(true);
		else						config.setNoCheat(false); 
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
		 * or the player himself
		 */
		if(cmd.getName().equalsIgnoreCase("rebirth") || cmd.getName().equalsIgnoreCase("reb")) {			
		// permission check
			if (!Perms.hasPermission(player, "dar.res")) {
				message.send(player, Messages.noPermission);
				return true;
			 }
		// resurrection
			try {
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
		//  player resurrects himself
			}catch (ArrayIndexOutOfBoundsException e) {
				if(!ghosts.isGhost(player)) {
					message.send(player, Messages.youAreNotDead);
					return true;
				}
				if(shrines.isOnShrine(player)) {
					ghosts.resurrect(player);
					return true;
				}
				else {
					if (config.isShrineOnlyEnabled()) {
						message.send(player, Messages.haveToStandOnShrine);
						return true;
					}
					Location loc = ghosts.getBoundShrine(player);
					if(loc == null) {
						message.send(player, Messages.soulNotBound);
						return true;
					}
					player.teleport(loc);
					ghosts.resurrect(player);					
					return true;
				
				}
			}
		}
		
		/**
		 * shrine <add, rm, list, pos1, pos2, select>
		 */
		if (cmd.getName().equalsIgnoreCase("shrine")) {
		// check permission
			if (!Perms.hasPermission(player, "dar.admin")) {
				message.send(player, Messages.noPermission);
				return true;
			 }			
		// ** defaulting to OP system ***
//			else {
//				if(!player.isOp()) {
//					message.send(player, Messages.noPermission);
//					return true;
//				}
//			}
			
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
		}
		
		
		/**
		 * dar <reb, reload, enable, disable, world, fly, shrinemode, ghostinteraction, ghostchat, dropping, versionCheck, lightningD, lightningD> <arg>
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
		// defaulting to OP system
//			else {
//				if(!player.isOp()) {
//					message.send(player, Messages.noPermission);
//					return true;
//				}
//			}
			
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
		// enabling and disabling support for the world
			if (arg.equalsIgnoreCase("enable")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				config.setBoolean(name, true);
				message.sendChat(player, Messages.worldEnabled, " "+name);
				return true;
			}
			
			if (arg.equalsIgnoreCase("disable")) {
				try {
					name = args[1];
				}catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				config.setBoolean(name, false);
				message.sendChat(player, Messages.worldDisabled, " "+name);
				return true;
			}
		// toggling dropping
			if (arg.equalsIgnoreCase("dropping")) {
				if(config.isDroppingEnabled()) {
					config.setDropping(false);
					message.sendChat(player, Messages.droppingToggle, " disabled");
				}
				else {
					config.setDropping(true);
					message.sendChat(player, Messages.droppingToggle, " enabled");
				}
				return true;
			}
		// toggling version checking
			if (arg.equalsIgnoreCase("versioncheck")) {
				if(config.isVersionCheckEnabled()) {
					config.setVersionCheck(false);
					message.sendChat(player, Messages.versionCheckToggle, " disabled");
				}
				else {
					config.setVersionCheck(true);
					message.sendChat(player, Messages.versionCheckToggle, " enabled");
				}
				return true;
			}
		// toggling fly mode
			if (arg.equalsIgnoreCase("fly")) {
				if(config.isFlyingEnabled()) {
					config.setFly(false);
					message.sendChat(player, Messages.flymodeToggle, " disabled");
				}
				else {
					config.setFly(true);
					message.sendChat(player, Messages.flymodeToggle, " enabled");
				}
				return true;
			}
		// toggling shrine  mode
			if (arg.equalsIgnoreCase("shrinemode")) {
				if(config.isShrineOnlyEnabled()) {
					config.setShrineOnly(false);
					message.sendChat(player, Messages.shrinemodeToggle, " disabled");
				}
				else {
					config.setShrineOnly(true);
					message.sendChat(player, Messages.shrinemodeToggle, " enabled");
				}
				return true;
			}
		// toggling blockGhostInteraction
			if (arg.equalsIgnoreCase("ghostinteraction")) {
				if(config.isBlockGhostInteractionEnabled()) {
					config.setBlockGhostInteraction(false);
					message.sendChat(player, Messages.blockghostToggle, " disabled");
				}
				else {
					config.setBlockGhostInteraction(true);
					message.sendChat(player, Messages.blockghostToggle, " enabled");
				}
				return true;
			}
		// toggling ghost chat
			if (arg.equalsIgnoreCase("ghostchat")) {
				if(config.isGhostChatEnabled()) {
					config.setGhostChat(false);
					message.sendChat(player, Messages.chatToggle, " disabled");
				}
				else {
					config.setGhostChat(true);
					message.sendChat(player, Messages.chatToggle, " enabled");
				}
				return true;
			}
			
		// lightning toggles
			if (arg.equalsIgnoreCase("lightningD")) {
				if(config.isLightningDEnabled()) {
					config.setLightningD(false);
					message.sendChat(player, Messages.lightningDT, " disabled");
				}
				else {
					config.setLightningD(true);
					message.sendChat(player, Messages.lightningDT, " enabled");
				}
				return true;
			}
			
			if (arg.equalsIgnoreCase("lightningR")) {
				if(config.isLightningREnabled()) {
					config.setLightningR(false);
					message.sendChat(player, Messages.lightningRT, " disabled");
				}
				else {
					config.setLightningR(true);
					message.sendChat(player, Messages.lightningRT, " enabled");
				}
				return true;
			}
		// grave sign toggle
			if (arg.equalsIgnoreCase("signs")) {
				if(config.isSignsEnabled()) {
					config.setGraveSigns(false);
					message.sendChat(player, Messages.signsToggle, " disabled");
				}
				else {
					config.setGraveSigns(true);
					message.sendChat(player, Messages.signsToggle, " enabled");
				}
				return true;
			}
		// reverseSpawning toggle
			if (arg.equalsIgnoreCase("spawn")) {
				if(config.isReverseSpawningEnabled()) {
					config.setReverseSpawning(false);
					message.sendChat(player, Messages.spawningToggle, " disabled");
				}
				else {
					config.setReverseSpawning(true);
					message.sendChat(player, Messages.spawningToggle, " enabled");
				}
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
				message.send(player, Messages.resurrected, name);
				return true;
			}
		}
		return false;		
	}

}
