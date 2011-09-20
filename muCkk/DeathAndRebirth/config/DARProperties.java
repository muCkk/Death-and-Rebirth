package muCkk.DeathAndRebirth.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import muCkk.DeathAndRebirth.messages.Errors;

public class DARProperties extends Properties {
	static final long serialVersionUID = 1L;

	private String dir, fileName;
	FileWriter writer;
	FileReader reader;
	
	private String	ghostSkin = "http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost1.png",
			deathSound = "http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/death.wav",
			resSound = "http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/res.wav",
			ghostTextPack = "",
			resurrectionS = "";
	
	private String info =
			  " __________ INFORMATIONS __________\n\n\n\n"
			
			
			
			
			+ "# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"
			+ "# !!! THIS IS THE OLD CONFIG - USE THE config.yml FILE !!!\n"
			+ "# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n\n\n"
			
			
			
			
			+ " __________ Basic Options __________\n"
			+ " amount: Amount needed of that item to reserruct.\n"
			+ " blockGhostInteraction: (true/false) If set to true ghosts can't use doors, buttons and levers.\n"
			+ " distance: Maximal distance to the dead player.\n"
			+ " dropping: (true/false) True: players drop their items upon death. False: players don't drop items and receive them after resurrection.\n"
			+ " fly: (true/false) Defines if ghosts can fly.\n"
			+ " flySpeed: Defines how fasts ghosts will fly. Default is 0.75, 1 is already quite fast and can cause lag on the server if new chunks have to be generated.\n"
			+ " ghostChat: (true/false) Enables or disables if ghosts can chat.\n"
			+ " ghostName: Edit how the playername will be displayed. User %player% for the original playername.\n"
			+ " graveSigns: (true/false) Toggles if grave signs are placed upon death.\n"
			+ " health: Amount of health a player gets if he uses /reb. Must be between 1 and 20 (20 max, 0 dead).\n"
			+ " itemID: ID of the item which will be consumed.\n"
			+ " lightningOnDeath: (true/false) Toggles lightning on death.\n"
			+ " lightningOnRebirth: (true/false) Toggles lightning on rebirth.\n"
			+ " invisibility: (true/false) Toggles invisibility for ghosts.\n"
			+ " needItem: (true/false) Defines if an item is needed to resurrect players.\n"
			+ " percent: Number of items which will be removed when using /reb in percent.\n"
			+ " pvpDrop: If this is enabled players will drop one random item if they are killed by a player. This option overrides 'dropping'.\n"
			+ " reverseSpawning: If set to true the player will spawn at his shrine and has to resurrect at his corpse.\n"
			+ " shrineNotification: If set to true a ghost will receive a notification if he enters a shrine area.\n"
			+ " shrineRadius: Radius of shriens. Default 3.\n"
			+ " shrineOnly: (true/false) If set to true dead players have to walk to a shrine. Deactivates soul binding.\n"
			+ " time: Time it takes to resurrect somebody.\n"
			+ " versionCheck: (true/false) Checks for updates and sends OPs a message on join.\n"
			+ " worldName: (true/false) Switch Death and Rebirth on and off for each world.\n\n"			

			+ " __________ (Spout) Skin, Sounds & Texture-Pack __________\n"
			+ " Colons have a leading backslash! Example: http\\:www.foo.com/bar.png\n"
			+ " ghostSoundEffects: (true/false) If enabled ghosts will hear noises from ghasts.\n"
			+ " ghostTextPack: URL to the texturepack used for ghosts.\n"
			+ " ghostSkin: URL to the skin used for ghosts. Defaults:\n"
			+ " Preview 1: http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost1_preview.png \n"
			+ " "+ ghostSkin +"\n"
			+ " Preview 2: http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost2_preview.png \n"
			+ " http\\://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost2.png \n"
			+ " deathSound: URL to the sound played on death. Default:\n"
			+ " "+ deathSound +"\n"
			+ " resurrectionS: Sound played when someone tries to resurrect a player.\n"
			+ " resSound: URL to the sound played on rebirth. Default:\n"
			+ " "+ resSound+"\n\n"
			
			+ " __________ (Spout) Colors __________\n"
			+ " Colors are defined using RGB values from 0 to 1. So 1;1;1 would be white, 1;0;0 would be red. Separate the values with ;\n"
			+ " changeColors: (true/false) Defines if the colors will get changed for ghosts. Default true.\n"
			+ " Defaults:\n"
			+ " ghostSky: 0.8;0;0\n"
			+ " ghostFog: 0.8;0;0\n"
			+ " ghostClouds: 0;0;0"			
			+ " \n\n"
			
			+ " __________ Do not edit following settings __________\n"
			+ " citizens: (true/false) This option is checked automatically and is set to true if you use Citizens.\n"
			+ " mobArena:(true/false) This option is checked automatically and is set to true if you use MobArena.\n"
			+ "\n"
			+ "\n"
			+ "\n"
			+ " __________ EDITABLE OPTIONS BELOW THIS LINE __________";			

	private int distance = 10,
			amount = 1,
			itemID = 288,
			shrineRadius = 3,
			time = 10,
			percent = 15,
			health = 10;	
	private double flySpeed = 0.75;
	private String 
			ghostSky = "0.8;0;0",
			ghostFog = "0.8;0;0",
			ghostClouds = "0;0;0",
			ghostName = "Ghost of %player%";
			
	private boolean needItem = false,
					ghostInteraction = false,
					fly = false,
					shrineOnly = false,
					ghostChat = true,
					versionCheck = true,
					citizens = false,
					mobArena = false,
					dropping = false,
					changeColors = true,
					lightningOnDeath = true,
					lightningOnRebirth = true,
					graveSigns = true,
					reverseSpawning = true,
					pvpDrop=true,
					shrineNotification = false,
					ghostSoundEffects = true,
					invisibility = true,
					configExists = false;
	
	public DARProperties(String dir) {
		this.dir = dir;
		this.fileName = dir+"/config.txt";
		load();
	}

	public void load() {
		clear();
		File file = new File(this.fileName);
		if (file.exists()) {
			try {
				FileInputStream input = new FileInputStream(this.fileName); 
				load(input);
				configExists = true;
				input.close();
			} catch (IOException ex) {
				Errors.loadingConfig();
			}
		} else {
			configExists = false;
			File directory = new File(dir);
			if (!directory.exists())
				directory.mkdir();
		}
		if(!configExists) return;
	// Setting defualt values
		getInteger("amount", amount);
		getBoolean("blockGhostInteraction", ghostInteraction);
		getBoolean("changeColors", changeColors);
		getBoolean("citizens", citizens);
		getString("deathSound", deathSound);
		getInteger("distance", distance);
		getBoolean("dropping", dropping);
		getBoolean("fly", fly);
		getDouble("flySpeed", flySpeed);
		getBoolean("ghostChat", ghostChat);
		getString("ghostName", ghostName);
		getString("ghostSkin", ghostSkin);
		getString("ghostTextPack", ghostTextPack);
		getBoolean("ghostSoundEffects", ghostSoundEffects);
		getString("ghostSky", ghostSky);
		getString("ghostFog", ghostFog);
		getString("ghostClouds", ghostClouds);
		getBoolean("graveSigns", graveSigns);
		getInteger("health", health);
		getInteger("itemID", itemID);
		getBoolean("lightningOnDeath", lightningOnDeath);
		getBoolean("lightningOnRebirth", lightningOnRebirth);
		getBoolean("invisibility", invisibility);
		getBoolean("mobArena", mobArena);
		getBoolean("needItem", needItem);
		getInteger("percent", percent);
		getBoolean("pvpDrop", pvpDrop);
		getString("resSound", resSound);
		getString("resurrectionS", resurrectionS);
		getBoolean("reverseSpawning", reverseSpawning);
		getBoolean("shrineNotification", shrineNotification);
		getBoolean("shrineOnly", shrineOnly);
		getInteger("shrineRadius", shrineRadius);
		getInteger("time", time);
		getBoolean("versionCheck", versionCheck);
		
		save();
	}

	public boolean configExists() {
		return configExists;
	}
	
	public void save() {
		try {
			store(new FileOutputStream(this.fileName), info);
		} catch (IOException ex) {
			Errors.savingConfig();
			ex.printStackTrace();
		}
	}

	// *** getter **********************************************************
	public int getInteger(String key, int value) {
		if (containsKey(key)) {
			return Integer.parseInt(getProperty(key));
		}
		put(key, String.valueOf(value));
		return value;
	}
	public int getInteger(String key) {
		return Integer.parseInt(getProperty(key));
	}
	public double getDouble(String key, double value) {
		if(contains(key)) {
			return Double.parseDouble(getProperty(key));
		}
		put(key, String.valueOf(value));
		return value;
	}
	public double getDouble(String key) {
		return Double.parseDouble(getProperty(key));
	}
	public String getString(String key, String value) {
		if (containsKey(key)) {
			return getProperty(key);
		}
		put(key,value);
		return value;
	}
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(getProperty(key));
	}
	public boolean getBoolean(String key, boolean value) {
		if (containsKey(key)) {
			String boolString = getProperty(key);
			return (boolString.length() > 0)
					&& (boolString.toLowerCase().charAt(0) == 't');
		}
		put(key, value ? "true" : "false");
		return value;
	}
	
// special getters
	public boolean isSignsEnabled() {
		return getBoolean("graveSigns");
	}
	public boolean isPvPDropEnabled() {
		return getBoolean("pvpDrop");
	}
	public boolean isReverseSpawningEnabled() {
		return getBoolean("reverseSpawning");
	}
	public String getGhostName() {
		return getProperty("ghostName");
	}
	public boolean isEnabled(String worldName) {
		return getBoolean(worldName);
	}
	public boolean isSpoutEnabled() {
		return getBoolean("spout");
	}
	public boolean isCitizensEnabled() {
		return getBoolean("citizens");
	}
	public boolean isMobArenaEnabled() {
		return getBoolean("mobArena");
	}
	public boolean isBlockGhostInteractionEnabled() {
		return getBoolean("blockGhostInteraction");
	}
	public boolean isFlyingEnabled() {
		return getBoolean("fly");
	}
	public double getFlySpeed() {
		return Double.parseDouble(getProperty("flySpeed"));
	}
	public boolean isShrineOnlyEnabled() {
		return getBoolean("shrineOnly");
	}
	public boolean isGhostChatEnabled() {
		return getBoolean("ghostChat");
	}
	public boolean isVersionCheckEnabled() {
		return getBoolean("versionCheck");
	}
	public boolean isDroppingEnabled() {
		return getBoolean("dropping");
	}
	public boolean isLightningDEnabled() {
		return getBoolean("lightningOnDeath");
	}
	public boolean isLightningREnabled() {
		return getBoolean("lightningOnRebirth");
	}
	public int getTime() {
		return getInteger("time");
	}
	public int getPercent() {
		return getInteger("percent");
	}
	public String getGhostSkin() {
		return getProperty("ghostSkin");
	}
	public String getDeathSound() {
		return getProperty("deathSound");
	}
	public String getRebSound() {
		return getProperty("resSound");
	}
	public String getResSound() {
		return getProperty("resurrectionS");
	}
	public boolean changeColors() {
		return getBoolean("changeColors");
	}
	public int getShrineRadius() {
		return getInteger("shrineRadius");
	}
	public int getHealth() {
		return getInteger("health");
	}
	public boolean isShrineMsgEnabled() {
		return getBoolean("shrineNotification");
	}
	public String getTextPack() {
		return getProperty("ghostTextPack");
	}
	public boolean isGhostSoundEnabled() {
		return getBoolean("ghostSoundEffects");
	}
	public boolean invisEnabled() {
		return getBoolean("invisibility");
	}
	
	
	
	public float[] getGhostSky() {
		return makeFloat(getProperty("ghostSky"));
	}
	public float[] getGhostFog() {
		return makeFloat(getProperty("ghostFog"));
	}
	public float[] getGhostClouds() {
		return makeFloat(getProperty("ghostClouds"));
	}
	
	private float[] makeFloat(String string) {
		float [] array = new float[3];
		String [] strings = string.split(";");
		for (int i =0; i<strings.length; i++) {
			array[i] = Float.valueOf(strings[i]);
		}
		return array;
	}
	// *** setter  ***********************************************************
	
	public void setBoolean(String name, boolean bool) {
		if (bool) {
			put(name, "true");
		}
		else {
			put(name, "false");
		}
		save();
	}

	public void setSpout(boolean b) {
		if (b) {
			put("spout", "true");
		}
		else {
			put("spout", "false");
		}
		save();
	}

	public void setCitizens(boolean b) {
		if (b) {
			put("citizens", "true");
		}
		else {
			put("citizens", "false");
		}
		save();
	}
	public void setMobArena(boolean b) {
		if (b) {
			put("mobArena", "true");
		}
		else {
			put("mobArena", "false");
		}
		save();
	}
	public void setFly(boolean b) {
		if (b) {
			put("fly", "true");
		}
		else {
			put("fly", "false");
		}
		save();
	}
	
	public void setShrineOnly(boolean b) {
		if (b) {
			put("shrineOnly", "true");
		}
		else {
			put("shrineOnly", "false");
		}
		save();
	}
	
	public void setBlockGhostInteraction(boolean b) {
		if (b) {
			put("blockGhostInteraction", "true");
		}
		else {
			put("blockGhostInteraction", "false");
		}
		save();
	}
	public void setGhostChat(boolean b) {
		if (b) {
			put("ghostChat", "true");
		}
		else {
			put("ghostChat", "false");
		}
		save();
	}
	public void setVersionCheck(boolean b) {
		if (b) {
			put("versionCheck", "true");
		}
		else {
			put("versionCheck", "false");
		}
		save();
	}
	public void setDropping(boolean b) {
		if (b) {
			put("dropping", "true");
		}
		else {
			put("dropping", "false");
		}
		save();
	}
	public void setLightningD(boolean b) {
		if (b) {
			put("lightningOnDeath", "true");
		}
		else {
			put("lightningOnDeath", "false");
		}
		save();
	}
	public void setLightningR(boolean b) {
		if (b) {
			put("lightningOnRebirth", "true");
		}
		else {
			put("lightningOnRebirth", "false");
		}
		save();
	}
	public void setGraveSigns(boolean b) {
		if (b) {
			put("graveSigns", "true");
		}
		else {
			put("graveSigns", "false");
		}
		save();
	}
	public void setReverseSpawning(boolean b) {
		if (b) {
			put("reverseSpawning", "true");
		}
		else {
			put("reverseSpawning", "false");
		}
		save();
	}
	
	public void setPvPDrop(boolean b) {
		if (b) {
			put("pvpDrop", "true");
		}
		else {
			put("pvpDrop", "false");
		}
		save();
	}
	public void setInvis(boolean b) {
		if (b) {
			put("invisibility", "true");
		}
		else {
			put("invisibility", "false");
		}
		save();
	}
}
