package muCkk.DeathAndRebirth.messages;

/*
public class Messages {
	private static DAR plugin;
	
	//for normal plugin users
	public static String soulNotBound = plugin.getConfig().getString("SOUL_NOT_BOUND");
	public static String tooFarAway = plugin.getConfig().getString("TOO_FAR_AWAY");
	public static String cantDoThat = plugin.getConfig().getString("CANT_DO_THAT");
	public static String reborn = plugin.getConfig().getString("REBORN");
	public static String resurrected = plugin.getConfig().getString("RESURRECTED");
	public static String soulNowBound = plugin.getConfig().getString("SOUL_NOW_BOUND");
	public static String unbindSoul = plugin.getConfig().getString("UNBIND_SOUL");
	public static String haveToStandOnShrine = plugin.getConfig().getString("HAVE_TO_STAND_ON_SHRINE");
	public static String youAreNotDead = plugin.getConfig().getString("CANT_DO_THAT");
	public static String playerNotDead = plugin.getConfig().getString("CANT_DO_THAT");
	public static String playerDied = plugin.getConfig().getString("CANT_DO_THAT");
	public static String ghostNoChat = plugin.getConfig().getString("CANT_DO_THAT");
	public static String cantResurrectYourself = plugin.getConfig().getString("CANT_DO_THAT");
	public static String cantBindSoul = plugin.getConfig().getString("CANT_DO_THAT");
	public static String resurrecting = plugin.getConfig().getString("CANT_DO_THAT");
	public static String shrineArea = plugin.getConfig().getString("CANT_DO_THAT");
	public static String lostMoney = plugin.getConfig().getString("CANT_DO_THAT");
	public static String skillDropped = plugin.getConfig().getString("CANT_DO_THAT");
	
	//for player - plugin protection
	public static String graveProctected = plugin.getConfig().getString("CANT_DO_THAT");
	public static String shrineProtectedDestroy = plugin.getConfig().getString("CANT_DO_THAT");
	public static String shrineProtectedBuild = plugin.getConfig().getString("CANT_DO_THAT");
	public static String cantAttackGhosts = plugin.getConfig().getString("CANT_DO_THAT");
	
	// chat form only
	public static String notEnoughItems = plugin.getConfig().getString("CANT_DO_THAT");
	public static String noPermission = plugin.getConfig().getString("CANT_DO_THAT");
	public static String yourGraveIsHere = plugin.getConfig().getString("CANT_DO_THAT");
	public static String youHaveNoGrave = plugin.getConfig().getString("CANT_DO_THAT");
	
	// admins
	public static String newVersion = plugin.getConfig().getString("Death and Rebirth - A new version is available");
	public static String worldEnabled = plugin.getConfig().getString("World enabled");
	public static String worldDisabled = plugin.getConfig().getString("World disabled");
	public static String droppingToggle = plugin.getConfig().getString("Dropping");
	public static String pvpDroppingToggle = plugin.getConfig().getString("PvP dropping is now");
	public static String versionCheckToggle = plugin.getConfig().getString("Version check");
	public static String flymodeToggle = plugin.getConfig().getString("Flying");
	public static String shrinemodeToggle = plugin.getConfig().getString("Shrine only");
	public static String blockghostToggle = plugin.getConfig().getString("Block ghost interactions");
	public static String chatToggle = plugin.getConfig().getString("Ghost chatting");
	public static String lightningDT = plugin.getConfig().getString("Lightning effect on death");
	public static String lightningRT = plugin.getConfig().getString("Lightning effect on rebirth");
	public static String signsToggle = plugin.getConfig().getString("Grave-Signs are now");
	public static String spawningToggle = plugin.getConfig().getString("Corpse spawning is now");
	public static String bindingToggle = plugin.getConfig().getString("Soulbinding for this shrine is now");
	public static String invisToggle = plugin.getConfig().getString("Invisibility for ghosts is now");	
	
	private String msg;
	
	Messages(String msg) {
		this.msg = msg;
	}
	
	public String msg() {
		return msg;
	}
	
}
*/


public enum Messages {
	// for players using the plugin
	soulNotBound			("Soul not bound"), 
	tooFarAway				("You are too far away"), 
	cantDoThat				("You can't do that"), 
	reborn					("You were reborn"), 
	resurrected				("Resurrecting"), 
	soulNowBound			("Your soul is now bound"),
	unbindSoul				("Soul unbound"),
	haveToStandOnShrine		("No shrine here"),
	youAreNotDead			("You are not dead"),
	playerNotDead			("Player is not dead"),
	playerDied				("You are now a ghost"), 
	ghostNoChat				("No one can hear you"),
	cantResurrectYourself	("That can't be done"),
	cantBindSoul			("Soul can't be bound here"),
	resurrecting			("Resurrecting"),
	shrineArea				("Entering a shrine area"),
	lostMoney				("You lost"),
	skillDropped			("%skill% skill dropped"),
	disabledCommand			("Command disabled"),
	
	// for players - plugin protection
	//	max length:			("12345678901234567890123456")
	graveProtected			("The grave is protected"),
	shrineProtectedDestroy	("Shrines are protected"), 
	shrineProtectedBuild	("Can't build on shrines"), 
	cantAttackGhosts		("Can't attack ghosts"),
	
	// chat form only
	notEnoughItems			("Resurrection is not possible. You need"),
	noPermission			("No permission."),
	yourGraveIsHere			("Your grave is here"),
	youHaveNoGrave			("You have no grave."),
	
	// admins
	nameAlreadyExists		("Name already exists."),
	nameNotFound			("Name not found."),
	reloadComplete			("Reload complete."),
	shrineAlreadyThere		("There is already a shrine at that location."),
	shrineAdded				("Shrine added."),
	shrineRemoved			("Shrine removed."),
	shrineSpawnAdded		("Spawn added."),
	noShrinesFound			("No shrines were found."),
	noSelectionMade			("You have to select an area to create a shrine."),
	update					("Shrine updated."),
	showGhosts				("Revealing ghosts."),
	hideGhosts				("Hiding ghosts."),
	
	newVersion				("Death and Rebirth - A new version is available"),
	worldEnabled			("World enabled"),
	worldDisabled			("World disabled"),
	droppingToggle			("Dropping"),
	pvpDroppingToggle		("PvP dropping is now"),
	versionCheckToggle		("Version check"),
	flymodeToggle			("Flying"),
	shrinemodeToggle		("Shrine only"),
	blockghostToggle		("Block ghost interactions"),
	chatToggle				("Ghost chatting"),
	lightningDT				("Lightning effect on death"),
	lightningRT				("Lightning effect on rebirth"),
	signsToggle				("Grave-Signs are now"),
	spawningToggle			("Corpse spawning is now"),
	bindingToggle			("Soulbinding for this shrine is now"),
	othersToggle			("Others ignore shrine only is now"),
	invisToggle				("Invisibility for ghosts is now");	
	
	private String msg;
	
	Messages(String msg) {
		this.msg = msg;
	}
	
	public String msg() {
		return msg;
	}
}



