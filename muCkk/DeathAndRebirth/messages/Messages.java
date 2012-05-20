package muCkk.DeathAndRebirth.messages;

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
	
	// for players - plugin protection
	//	max length:			("12345678901234567890123456")
	graveProtected			("The grave is protected"),
	shrineProtectedDestroy	("Shrines are protected"), 
	shrineProtectedBuild	("Can't build on shrines"), 
	cantAttackGhosts		("Can't attack ghosts"),
	messagesEnabled         ("Messages Enabled"),
	messagesDisabled        ("Messages Disabled"),
	
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
	invisToggle				("Invisibility for ghosts is now");	
	
	private String msg;
	
	Messages(String msg) {
		this.msg = msg;
	}
	
	public String msg() {
		return msg;
	}
} 
