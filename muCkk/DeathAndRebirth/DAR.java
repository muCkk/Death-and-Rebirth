package muCkk.DeathAndRebirth;

import java.util.HashMap;
import java.util.logging.Logger;

import muCkk.DeathAndRebirth.ghost.Ghosts;
import muCkk.DeathAndRebirth.ghost.Graves;
import muCkk.DeathAndRebirth.ghost.Shrines;
import muCkk.DeathAndRebirth.listener.BListener;
import muCkk.DeathAndRebirth.listener.EListener;
import muCkk.DeathAndRebirth.listener.PListener;
import muCkk.DeathAndRebirth.listener.SListener;
import muCkk.DeathAndRebirth.messages.Messenger;
import muCkk.DeathAndRebirth.messages.Messages;
import muCkk.DeathAndRebirth.otherPlugins.DARmcMMO;
import muCkk.DeathAndRebirth.otherPlugins.DARSpout;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin; 

public class DAR extends JavaPlugin {

	private String dir				=	"plugins/Death and Rebirth";
	private String dataDir			=	dir+"/data";	
	
	private Ghosts ghosts;
	private Graves graves;
	private Shrines shrines;
	
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    
	public Messenger message;
	public  DARSpout darSpout;
	public DARmcMMO darmcmmo = null;
	public PluginManager pm;
	
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		try {
			ghosts.saveCustomConfig();
			graves.saveCustomConfig();
			shrines.saveCustomConfig();
		}catch (NullPointerException e) {
			// TODO: handle exception
		}
	}

	public void onEnable() {
		if (!setupPermissions()) {
	        log.info(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
	        getServer().getPluginManager().disablePlugin(this);
	        return;
	    }
		setupEconomy();
		
	// Config
		getConfig().options().copyDefaults(true);
		saveConfig();
		
	// DAR Classes
		darSpout = new DARSpout(this, dataDir);
		message = new Messenger(this);

		graves = new Graves(this, dataDir);
		ghosts = new Ghosts(this, dir, graves);
		darSpout.setGhosts(ghosts);
		shrines = new Shrines(this, dataDir);

	// Listener
		pm = getServer().getPluginManager();
		pm.registerEvents(new PListener(this, ghosts, shrines), this);
		pm.registerEvents(new EListener(this, ghosts, shrines), this);
		pm.registerEvents(new BListener(this, shrines,ghosts,graves), this);
		SListener serverListener = new SListener(this);
		pm.registerEvents(serverListener, this);
		serverListener.checkForPlugins();
	}
	
	private Boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
        	perms = permissionProvider.getProvider();
        }
        return (perms != null);
    }
	
	private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }

        return (econ != null);
    }
	
	public boolean checkAdminPerms() {
		return getConfig().getBoolean("ADMIN_PERMS");
	}
	
	public float[] getFloatColor(String node) { 
		float [] array = new float[3];
		String [] strings = getConfig().getString(node).split(";");
		for(int i=0; i<strings.length; i++) {
			array[i] = Float.valueOf(strings[i]);
		}
		return array;
	}
	//overgives the worldName as String
	public HashMap<String, String> worldData = new HashMap<String, String>();
	
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
				if (!perms.has(player, "dar.reb") && !player.isOp()) {
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
				if (!this.getConfig().getBoolean("SHRINE_ONLY")) {
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
			if (!perms.has(player, "dar.admin") && !player.isOp()) {
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
				message.sendChat(player, Messages.shrineSpawnAdded);
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
				if (!perms.has(player, "dar.admin") && !player.isOp()) {
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
				reloadConfig();
				graves.reloadCustomConfig();
				ghosts.reloadCustomConfig();
				shrines.reloadCustomConfig();
				message.sendChat(player, Messages.reloadComplete);
				return true;
			}

		// giving the world name
			if(arg.equalsIgnoreCase("world")) {
				String world = player.getWorld().getName();
				player.sendMessage("You are in world: "+world);
				String bool = "";
				if (getConfig().getBoolean(world)) {
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
			//msg display for each player
			if(arg.equalsIgnoreCase("msg"))
			{
			   String worldName = player.getWorld().getName();
			   String playerName = player.getName();
			   if (ghosts.getCustomConfig().getBoolean("players." +playerName +"."+worldName +".msg") == true)
			   {
				    message.send(player, Messages.messagesDisabled);
					ghosts.getCustomConfig().set("players." +playerName +"."+worldName +".msg", false);
					worldData.put("worldName", worldName);

			   }
			   if (ghosts.getCustomConfig().getBoolean("players." +playerName +"."+worldName +".msg") == false)
			   {
				  ghosts.getCustomConfig().set("players." +playerName +"."+worldName +".msg", true);
				  worldData.put("worldName", worldName);
				  message.send(player, Messages.messagesEnabled);
			   }
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
					getConfig().set(name, true);
					message.sendChat(player, Messages.worldEnabled, " "+name);
				}
				else {
					getConfig().set(name, false);
					message.sendChat(player, Messages.worldDisabled, " "+name);
				}
				saveConfig();
				return true;
			}
		// show and hide ghosts
			if (arg.equalsIgnoreCase("showghosts")) {
				ghosts.showGhosts(player);
				message.sendChat(player, Messages.showGhosts);
				return true;
			}
			if (arg.equalsIgnoreCase("hideghosts")) {
				ghosts.hideGhosts(player);
				message.sendChat(player, Messages.hideGhosts);
				return true;
			}
		// toggles
			if (arg.equalsIgnoreCase("invis")) {
				if(toggle("INVISIBILITY")) message.sendChat(player, Messages.invisToggle, " disabled");
				else message.sendChat(player, Messages.invisToggle, " enabled");
			}
			if (arg.equalsIgnoreCase("dropping")) {
				if(toggle("DROPPING")) message.sendChat(player, Messages.droppingToggle, " disabled");
				else message.sendChat(player, Messages.droppingToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("pvpdrop")) {
				if(toggle("PVP_DROP")) message.sendChat(player, Messages.pvpDroppingToggle, " disabled");
				else message.sendChat(player, Messages.pvpDroppingToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("versioncheck")) {
				if(toggle("VERSION_CHECK")) message.sendChat(player, Messages.versionCheckToggle, " disabled");
				else message.sendChat(player, Messages.versionCheckToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("fly")) {
				if(toggle("FLY")) message.sendChat(player, Messages.flymodeToggle, " disabled");
				else message.sendChat(player, Messages.flymodeToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("shrinemode")) {
				if(toggle("SHRINE_ONLY")) message.sendChat(player, Messages.shrinemodeToggle, " disabled");
				else message.sendChat(player, Messages.shrinemodeToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("ghostinteraction")) {
				if(toggle("BLOCK_GHOST_INTERACTION")) message.sendChat(player, Messages.blockghostToggle, " disabled");
				else message.sendChat(player, Messages.blockghostToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("ghostchat")) {
				if(toggle("GHOST_CHAT")) message.sendChat(player, Messages.chatToggle, " disabled");
				else message.sendChat(player, Messages.chatToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("lightningD")) {
				if(toggle("LIGHTNING_DEATH")) message.sendChat(player, Messages.lightningDT, " disabled");
				else message.sendChat(player, Messages.lightningDT, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("lightningR")) {
				if(toggle("LIGHTNING_REBIRTH")) message.sendChat(player, Messages.lightningRT, " disabled");
				else message.sendChat(player, Messages.lightningRT, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("signs")) {
				if(toggle("GRAVE_SIGNS")) message.sendChat(player, Messages.signsToggle, " disabled");
				else message.sendChat(player, Messages.signsToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("spawn")) {
				if(toggle("CORPSE_SPAWNING")) message.sendChat(player, Messages.spawningToggle, " disabled");
				else message.sendChat(player, Messages.spawningToggle, " enabled");
				return true;
			}
		}
		return false;		
	}
	
	public boolean toggle(String node) {
		if(getConfig().getBoolean(node)) {
			getConfig().set(node, false);
			saveConfig();
			return false;
		}
		else {
			getConfig().set(node, true);
			saveConfig();
			return true;
		}
	}
}
