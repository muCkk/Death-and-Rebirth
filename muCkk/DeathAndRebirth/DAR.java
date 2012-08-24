package muCkk.DeathAndRebirth;

import java.util.logging.Logger;

import muCkk.DeathAndRebirth.ghost.Blacklist;
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

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin; 

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.herocraftonline.heroes.Heroes;

public class DAR extends JavaPlugin {

	private String dir				=	"plugins/Death and Rebirth";
	private String dataDir			=	dir+"/data";	
	
	private Ghosts ghosts;
	private Graves graves;
	private Shrines shrines;
	private PListener plistener;
	private Blacklist blacklist;
	
	private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    
	public Messenger message;
	public DARSpout darSpout;
	public DARmcMMO darmcmmo = null;
	public PluginManager pm;
    private static MobArenaHandler maHandler;
    private static ArenaMaster am;
    private static MobArena mobArena;
	
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		try {
			ghosts.saveCustomConfig();
			graves.saveCustomConfig();
			shrines.saveCustomConfig();
			message.saveCustomConfig();
			blacklist.saveCustomConfig();
		}catch (NullPointerException e) {
			// TODO: handle exception
		}
	}
	
	/*
	 * TODO create checkPerms methods for all permissions
	 * and config option which checks if permissions should be used
	 * 
	 * 
	 */

	public void onEnable() {
		setupEconomy();
		
		mobArena = (MobArena)Bukkit.getPluginManager().getPlugin("MobArena");
        if(mobArena != null && mobArena.isEnabled())
            setupMobArena(mobArena);
        
        Heroes heroes =  (Heroes) this.getServer().getPluginManager().getPlugin("Heroes");
        		
	// Config
		getConfig().options().copyDefaults(true);
		saveConfig();
				
	// DAR Classes
		darSpout = new DARSpout(this, dataDir);
		message = new Messenger(this);

		shrines = new Shrines(this, dataDir);
		graves = new Graves(this, dataDir);
		ghosts = new Ghosts(this, dir, graves, shrines, heroes);
		blacklist = new Blacklist(this);
		darSpout.setGhosts(ghosts);
		
	// custom configs
		message.reloadCustomConfig();
		message.saveCustomConfig();
		blacklist.reloadCustomConfig();
		blacklist.saveCustomConfig();
		shrines.reloadCustomConfig();
		shrines.saveCustomConfig();

	// Listener
		pm = getServer().getPluginManager();
		plistener = new PListener(this, ghosts, shrines, graves);
		pm.registerEvents(plistener, this);
		ghosts.setPListener(plistener);
		
		pm.registerEvents(new EListener(this, ghosts, shrines), this);
		pm.registerEvents(new BListener(this, shrines,ghosts,graves), this);
		SListener serverListener = new SListener(this);
		pm.registerEvents(serverListener, this);
		serverListener.checkForPlugins();
		
		if(this.getConfig().getBoolean("CITIZENS_ENABLED"))
			this.getLogger().info("Citizens found, support enabled");
		
		if(this.getConfig().getBoolean("MOBARENA_ENABLED"))
			this.getLogger().info("MobArena found, support enabled");
		
		if(this.getConfig().getBoolean("SPOUT_ENABLED"))
			this.getLogger().info("Spout found, features supported now");
		
		this.getLogger().info("Configs loaded!");
		
		this.getLogger().warning("If config-/messages.yml wasn't created");
		this.getLogger().warning("well, download defaults at:");
		this.getLogger().warning("http://dev.bukkit.org/server-mods/death-and-rebirth/");
		
	}
	
	private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }
        return (econ != null);
    }
		
    public static void setupMobArena(MobArena instance)
    {
        maHandler = new MobArenaHandler();
        am = instance.getArenaMaster();
    }
    public static MobArena getMA()
    {
        return mobArena;
    }
    
    public static MobArenaHandler getMAH()
    {
        return maHandler;
    }
    
    public static ArenaMaster getAM()
    {
        return am;
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
		if(cmd.getName().equalsIgnoreCase("rebirth") || cmd.getName().equalsIgnoreCase("reb") && sender instanceof Player) {
			
			if(!hasPermReb(player)) return true;
			
			// resurrect target
			if (args.length > 0) {
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
		if (cmd.getName().equalsIgnoreCase("shrine") && sender instanceof Player) {
			
			if(!hasPermAdmin(player)) return true;
			
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
		if(cmd.getName().equalsIgnoreCase("dar") && !(sender instanceof Player)) {
			String arg = "";
			try {
				arg = args[0];
			}catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
			
			// reloading all config files
			if(arg.equalsIgnoreCase("reload")) {
				reloadConfig();
				saveConfig();
				graves.reloadCustomConfig();
				graves.saveCustomConfig();
				ghosts.reloadCustomConfig();
				ghosts.saveCustomConfig();
				shrines.reloadCustomConfig();
				shrines.saveCustomConfig();
				message.reloadCustomConfig();
				message.saveCustomConfig();
				blacklist.reloadCustomConfig();
				blacklist.saveCustomConfig();
				log.info("[Death and Rebirth] Configs reloaded");
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("dar") && sender instanceof Player) {
			
			if(!hasPermAdmin(player)) return true;
				
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
				saveConfig();
				graves.reloadCustomConfig();
				graves.saveCustomConfig();
				ghosts.reloadCustomConfig();
				ghosts.saveCustomConfig();
				shrines.reloadCustomConfig();
				shrines.saveCustomConfig();
				message.reloadCustomConfig();
				message.saveCustomConfig();
				blacklist.reloadCustomConfig();
				blacklist.saveCustomConfig();
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
			if (arg.equalsIgnoreCase("compass")) {
				if(toggle("COMPASS")) message.sendChat(player, Messages.compassToggle, " disabled");
				else message.sendChat(player, Messages.compassToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("others")) {
				if(toggle("OTHERS_RESURRECT")) message.sendChat(player, Messages.othersToggle, " disabled");
				else message.sendChat(player, Messages.othersToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("hardcore")) {
				if(toggle("HARDCORE")) message.sendChat(player, Messages.hardcoreToggle, " disabled");
				else message.sendChat(player, Messages.hardcoreToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("needitem")) {
				if(toggle("NEED_ITEM")) message.sendChat(player, Messages.needItemToggle, " disabled");
				else message.sendChat(player, Messages.needItemToggle, " enabled");
				return true;
			}
			if (arg.equalsIgnoreCase("onlyday")) {
				if(toggle("ONLY_DAY")) message.sendChat(player, Messages.onlyDayToggle, " disabled");
				else message.sendChat(player, Messages.onlyDayToggle, " enabled");
				return true;
			}
			
		}
		
		else {}
		
		return false;		
	}
	
	public boolean toggle(String node) {
		if(!getConfig().getBoolean(node)) {
			getConfig().set(node, true);
			saveConfig();
			return false;
		}
		else {
			getConfig().set(node, false);
			saveConfig();
			return true;
		}
	}
	
	public boolean permsEnabled() {
		if(this.getConfig().getBoolean("PERMISSIONS"))
			return true;
		else
			return false;
	}
	
	public boolean hasPermIgnore(Player player) {
		if(permsEnabled())
			if(player.hasPermission("dar.ignore"))
				return true;
			else
				if(checkAdminPerms() && player.hasPermission("dar.admin"))
					return true;
				else 
					return false;
		else
			if(checkAdminPerms() && hasPermAdmin(player))
				return true;
			else 
				return false;
	}
	public boolean hasPermNoDrop(Player player) {
		if(permsEnabled())
			if(player.hasPermission("dar.nodrop"))
				return true;
			else
				if(checkAdminPerms() && player.hasPermission("dar.admin"))
					return true;
				else
					return false;
		else
			if(checkAdminPerms() && hasPermAdmin(player))
					return true;
			else
				return false;
	}
	public boolean hasPermAdmin(Player player) {
		if(permsEnabled())
			if(player.hasPermission("dar.admin"))
				return true;
			else {
				message.send(player, Messages.noPermission);
				return false;
			}

		else
			if(player.isOp())
				return true;
			else {
				message.send(player, Messages.noPermission);
				return false;
			}
	}
	public boolean hasPermAdminNoMsg(Player player) {
		if(permsEnabled())
			if(player.hasPermission("dar.admin"))
				return true;
			else
				return false;

		else
			if(player.isOp())
				return true;
			else
				return false;
	}
	public boolean hasPermReb(Player player) {
		if(permsEnabled())
			if(player.hasPermission("dar.reb") || player.hasPermission("dar.admin"))
				return true;
			else {
				message.send(player, Messages.noPermission);
				return false;
			}
		else
			return true;
	}
	public boolean hasPermRebOthers(Player player) {
		if(permsEnabled())
			if(player.hasPermission("dar.reb.others") || player.hasPermission("dar.admin"))
				return true;
			else
				return false;
		else
			return true;
	}
	public boolean hasPermRobb(Player player) {
		if(permsEnabled())
			if(player.hasPermission("dar.robb") || player.hasPermission("dar.admin"))
				return true;
			else
				return false;
		else
			return true;
	}
	public boolean hasPermShrine(Player player, String shrine) {
		if(permsEnabled())
			if(player.hasPermission("dar.shrine." + shrine) || player.hasPermission("dar.shrine.*") || player.hasPermission("dar.admin"))
				return true;
			else
				return false;
		else
			return true;
	}
}
