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
	private String info = "needItem: (true/false) Defines if an item is needed to resurrect players\n" 
			+ "itemID: ID of the item which will be consumed\n"
			+ "amount: Amount needed of that item to reserruct\n"
			+ "distance: Maximal distance to the dead player"
			+ "worldName: (true/false) Switch Death and Rebirth on and off for each world";

	private int distance = 10,
			amount = 1,
			itemID = 288;
	
	private boolean needItem = false;
			
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
			getBoolean("needItem", needItem);
			getInteger("amount", amount);
			getInteger("itemID", itemID);
			getInteger("distance", distance);
			save();
		}
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
}
