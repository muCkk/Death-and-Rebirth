package muCkk.DeathAndRebirth.config;

public enum CFG {
	
	// ghost related
		BLOCK_GHOST_INTERACTION	(false),
		FLY						(false),
		FLY_SPEED				(0.75),
		GHOST_CHAT				(true),
		GHOST_NAME				("Ghost of %player%"),
		INVISIBILITY			(true),
		LIGHTNING_DEATH			(true),
		LIGHTNING_REBIRTH		(true),
		
		//dropping
		DROPPING				(false),
		PVP_DROP				(true),
		
		// self res punishment
		HEALTH					(10),
		PERCENT					(15),
		ECONOMY					(0),
		
		MCMMO					(false),
		XP						(0),
		
		// resurrection
		NEED_ITEM				(false),
		ITEM_ID					(288),
		AMOUNT					(1),
		DISTANCE				(10),
		TIME					(10),
		
		// spawning
		CORPSE_SPAWNING			(false),
		
		// plugin related
		GRAVE_SIGNS				(true),
		SHRINE_NOTE				(true),
		SHRINE_RADIUS			(3),
		SHRINE_ONLY				(false),
		VERSION_CHECK			(true),
		
		// Spout
		GHOST_SOUND_EFFECTS		(true),
		DEATH_SOUND				("http:dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/death.wav"),
		REB_SOUND				("http:dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/res.wav"),
		RES_SOUND				(""),
		
		GHOST_SKIN				("http:dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost1.png"),
		GHOST_TEXTPACK			(""),
		
		CHANGE_COLORS			(true),
		GHOST_SKY				("0.8;0;0"),
		GHOST_FOG				("0.8;0;0"),
		GHOST_CLOUDS			("0;0;0"),
		
		// special nodes
		SPOUT_ENABLED			(false),
		CITIZENS_ENABLED		(false);
	
	
	
	
	
	
	
//	// ghost related
//	BLOCK_GHOST_INTERACTION	(false),
//	FLY						(false),
//	FLY_SPEED				(0.75),
//	GHOST_CHAT				(true),
//	GHOST_NAME				("Ghost of %player%"),
//	INVISIBILITY			(true),
//	LIGHTNING_DEATH			(true),
//	LIGHTNING_REBIRTH		(true),
//	
//	//dropping
//	DROPPING				(false),
//	PVP_DROP				(true),
//	
//	// self res punishment
//	HEALTH					(10),
//	PERCENT					(15),
//	
//	// resurrection
//	NEED_ITEM				(false),
//	ITEM_ID					(288),
//	AMOUNT					(1),
//	DISTANCE				(10),
//	TIME					(10),
//	
//	// spawning
//	CORPSE_SPAWNING			(false),
//	
//	// plugin related
//	GRAVE_SIGNS				(true),
//	SHRINE_NOTE				(true),
//	SHRINE_RADIUS			(3),
//	SHRINE_ONLY				(false),
//	VERSION_CHECK			(true),
//	
//	// Spout
//	GHOST_SOUND_EFFECTS		(true),
//	DEATH_SOUND				("http:dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/death.wav"),
//	REB_SOUND				("http:dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/res.wav"),
//	RES_SOUND				(""),
//	
//	GHOST_SKIN				("http:dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost1.png"),
//	GHOST_TEXTPACK			(""),
//	
//	CHANGE_COLORS			(true),
//	GHOST_SKY				("0.8;0;0"),
//	GHOST_FOG				("0.8;0;0"),
//	GHOST_CLOUDS			("0;0;0"),
//	
//	// special nodes
//	SPOUT_ENABLED			(false),
//	CITIZENS_ENABLED		(false);
	
	
// alphabetical	
//	AMOUNT					(1),
//	BLOCK_GHOST_INTERACTION	(false),
//	CORPSE_SPAWNING			(false),
//	DISTANCE				(10),
//	DROPPING				(false),
//	FLY						(false),
//	FLY_SPEED				(0.75),
//	GHOST_CHAT				(true),
//	GHOST_NAME				("Ghost of %player%"),
//	GRAVE_SIGNS				(true),
//	HEALTH					(10),
//	INVISIBILITY			(true),
//	ITEM_ID					(288),
//	LIGHTNING_DEATH			(true),
//	LIGHTNING_REBIRTH		(true),
//	NEED_ITEM				(false),
//	PERCENT					(15),
//	PVP_DROP				(true),
//	SHRINE_NOTE				(true),
//	SHRINE_RADIUS			(3),
//	SHRINE_ONLY				(false),
//	TIME					(10),
//	VERSION_CHECK			(true),
//	
//	// Spout
//	GHOST_SOUND_EFFECTS		(true),
//	GHOST_SKIN				("http:dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost1.png"),
//	DEATH_SOUND				("http:dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/death.wav"),
//	REB_SOUND				("http:dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/res.wav"),
//	RES_SOUND				(""),
//	GHOST_TEXTPACK			(""),
//	CHANGE_COLORS			(true),
//	GHOST_SKY				("0.8;0;0"),
//	GHOST_FOG				("0.8;0;0"),
//	GHOST_CLOUDS			("0;0;0");

	// plugins: spout, citizens, mobarena
	// worlds
	
	// variables
	private String s;
	private boolean b;
	private int i;
	private double d;
	private boolean isString = false, isBoolean = false, isInt = false, isDouble = false;
	
	// Constructors
	CFG(boolean b) {
		this.b = b;
		this.isBoolean = true;
	}
	CFG(String s) {
		this.s = s;
		this.isString = true;
	}
	CFG(int i) {
		this.i = i;
		this.isInt = true;
	}
	CFG(double d) {
		this.d = d;
		this.isDouble = true;
	}
	
	// Getter
	/**
	 * Returns the information as string
	 * @return
	 */
	public String s() {
		return s;
	}
	/**
	 * Returns the information as boolean
	 * @return
	 */
	public boolean b() {
		return b;
	}
	/**
	 * Returns the information as integer
	 * @return
	 */
	public int i() {
		return i;
	}
	/**
	 * Returns the information as double
	 * @return
	 */
	public double d() {
		return d;
	}
	
	public boolean isString() {
		return isString;
	}
	public boolean isInt() {
		return isInt;
	}
	public boolean isDouble() {
		return isDouble;
	}
	public boolean isBoolean() {
		return isBoolean;
	}
}
