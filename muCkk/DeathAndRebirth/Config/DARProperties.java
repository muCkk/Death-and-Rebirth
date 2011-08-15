package muCkk.DeathAndRebirth.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class DARProperties extends Properties {
	static final long serialVersionUID = 1L;

	private String dir, fileName;
	FileWriter writer;
	FileReader reader;
	
	private String	ghostSkin = "http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost1.png",
			deathSound = "http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/death.wav",
			resSound = "http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/sounds/res.wav";
	
	private String info = "versionCheck: (true/false) Checks for updates and sends OPs a message on join.\n"
			+ "dropping: (true/false) True: players drop their items upon death. False: players don't drop items and receive them after resurrection.\n"
			+ "needItem: (true/false) Defines if an item is needed to resurrect players.\n" 
			+ "itemID: ID of the item which will be consumed.\n"
			+ "amount: Amount needed of that item to reserruct.\n"
			+ "distance: Maximal distance to the dead player.\n"
			+ "worldName: (true/false) Switch Death and Rebirth on and off for each world.\n"
			+ "blockGhostInteraction: (true/false) If set to true ghosts can't use doors, buttons and levers.\n"
			+ "fly: (true/false) Defines if ghosts can fly.\n"
			+ "shrineOnly: (true/false) If set to true dead players have to walk to a shrine. Deactivates soul binding.\n"
			+ "ghostChat: (true/false) Enables or disables if ghosts can chat.\n\n"
			
			+ "citizens: (true/false) This option is checked automatically and is set to true if you use Citizens.\n"
			+ "noCheat: (true/false) This option is checked automatically and is set to true if you use NoCheat.\n\n"

			+ "ghostSkin: URL to the skin used for ghosts. Defaults:\n"
			+ "Preview: http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost1_preview.png \n"
			+ ghostSkin +"\n"
			+ "Preview: http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost2_preview.png \n"
			+ "http://dl.dropbox.com/u/12769915/minecraft/plugins/DAR/skins/ghost2.png \n"
			+ "deathSound: URL to the sound played on death. Default:\n"
			+ deathSound +"\n"
			+ "resSound: URL to the sound played on resurrection.Default:\n"
			+ resSound;			

	private int distance = 10,
			amount = 1,
			itemID = 288;
	
	private boolean needItem = false,
					ghostInteraction = false,
					fly = false,
					shrineOnly = false,
					ghostChat = true,
					versionCheck = true,
					citizens = false,
					noCheat = false,
					dropping = true;
	
	public DARProperties(String dir, String fileName) {
		this.dir = dir;
		this.fileName = fileName;
	}

	public void load() {
		File file = new File(this.fileName);
		if (file.exists()) {
			try {
				load(new FileInputStream(this.fileName));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			File directory = new File(dir);
			if (!directory.exists())
				directory.mkdir();
		}
		getBoolean("versionCheck", versionCheck);
		getBoolean("needItem", needItem);
		getBoolean("dropping", dropping);
		getInteger("amount", amount);
		getInteger("itemID", itemID);
		getInteger("distance", distance);
		getBoolean("blockGhostInteraction", ghostInteraction);
		getBoolean("fly", fly);
		getBoolean("shrineOnly", shrineOnly);
		getBoolean("ghostChat", ghostChat);
		
		getBoolean("noCheat", noCheat);
		getBoolean("citizens", citizens);
		
		getString("ghostSkin", ghostSkin);
		getString("deathSound", deathSound);
		getString("resSound", resSound);
		save();
	}

	public void save() {
		try {
			store(new FileOutputStream(this.fileName), info);
		} catch (IOException ex) {
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
	public boolean isEnabled(String worldName) {
		return getBoolean(worldName);
	}
	public boolean isSpoutEnabled() {
		return getBoolean("spout");
	}
	public boolean isCitizensEnabled() {
		return getBoolean("citizens");
	}
	public boolean isBlockGhostInteractionEnabled() {
		return getBoolean("blockGhostInteraction");
	}
	public boolean isFlyingEnabled() {
		return getBoolean("fly");
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
	public boolean isNoCheatEnabled() {
		return getBoolean("noCheat");
	}
	public boolean isDroppingEnabled() {
		return getBoolean("dropping");
	}
	public String getGhostSkin() {
		return ghostSkin;
	}
	public String getDeathSound() {
		return deathSound;
	}
	public String getResSound() {
		return resSound;
	}
	// *** setter  ***********************************************************
	
	public void setBoolean(String name, boolean bool) {
		if (bool) {
			put(name, "true");
		}
		else {
			put(name, "false");
		}		
	}

	public void setSpout(boolean b) {
		if (b) {
			put("spout", "true");
		}
		else {
			put("spout", "false");
		}
	}

	public void setCitizens(boolean b) {
		if (b) {
			put("citizens", "true");
		}
		else {
			put("citizens", "false");
		}
	}
	
	public void setFly(boolean b) {
		if (b) {
			put("fly", "true");
		}
		else {
			put("fly", "false");
		}
	}
	
	public void setShrineOnly(boolean b) {
		if (b) {
			put("shrineOnly", "true");
		}
		else {
			put("shrineOnly", "false");
		}
	}
	
	public void setBlockGhostInteraction(boolean b) {
		if (b) {
			put("blockGhostInteraction", "true");
		}
		else {
			put("blockGhostInteraction", "false");
		}
	}
	public void setGhostChat(boolean b) {
		if (b) {
			put("ghostChat", "true");
		}
		else {
			put("ghostChat", "false");
		}
	}
	public void setVersionCheck(boolean b) {
		if (b) {
			put("versionCheck", "true");
		}
		else {
			put("versionCheck", "false");
		}
	}
	public void setNoCheat(boolean b) {
		if (b) {
			put("noCheat", "true");
		}
		else {
			put("noCheat", "false");
		}
	}
	public void setDropping(boolean b) {
		if (b) {
			put("dropping", "true");
		}
		else {
			put("dropping", "false");
		}
	}
}
