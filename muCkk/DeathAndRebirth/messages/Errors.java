package muCkk.DeathAndRebirth.messages;

public final class Errors {

	private static String prefix = "[Death and Rebirth] ";
	private static String prefixError = "[Death and Rebirth] ERROR: ";
	
	
	// ___ ERRORS ___

	public static void couldNotReadFile(String file) {
		System.out.println(prefixError+"Could not read "+file);
	}
	
	// Config
	public static void savingConfig() {
		System.out.println(prefixError +"Could not save config!");
	}
	public static void loadingConfig() {
		System.out.println(prefixError +"Could not load config!");
	}
	public static void messagesLoaded() {
		System.out.println(prefix+"Messages loaded.");
	}
	
	
	// PlayerListener
	public static void readingURL() {
		System.out.println(prefixError +"Reading URL");
	}
	public static void openingURL() {
		System.out.println(prefixError +"Opening URL");
	}
	
	//DARHandler
	public static void corruptGhostFile() {
		System.out.println(prefixError+"Corrupt ghost file!");
	}
	public static void couldNotReadGhostFile() {
		System.out.println(prefixError+"Could not read ghost file!");
	}
	public static void couldNotSaveGhostFile() {
		System.out.println(prefixError+"Could not save ghost-file!");
	}
	public static void markersFileErrors() {
		System.out.println(prefixError+"The markers file has errors.");
	}
	public static void markersFileReading() {
		System.out.println(prefixError+"Could not read markers file.");
	}
	public static void markersFileSaving() {
		System.out.println(prefixError+"Could not save markers file.");
	}
	public static void ghostNameWrong() {
		System.out.println(prefixError+"Wrong ghostName option. Check if it's set right.");
	}
	public static void whileRessing() {
		System.out.println(prefixError+"While resurrecting.");
	}
	
	//DARSigns
	public static void corruptSignsFile() {
		System.out.println(prefixError+"Corrupt grave file!");
	}
	public static void couldNotReadSignsFile() {
		System.out.println(prefixError+"Could not read grave file!");
	}
	public static void couldNotSaveSignsFile() {
		System.out.println(prefixError+"Could not save grave-file!");
	}
	
	// Drops
	public static void couldNotReadDropsFile() {
		System.out.println(prefixError+"Could not read drops file!");
	}
	// Spout
	public static void couldNotReadSpoutFile() {
		System.out.println(prefixError+"Could not read spout file!");
	}
	public static void couldNotSleepSkin() {
		System.out.println(prefixError+"Could not sleep while setting ghost skin.");
	}
	public static void couldNotSleepSound() {
		System.out.println(prefixError+"Could not sleep - ghostSoundEffect");
	}
	// ___ MESSAGES ___
	
	//DARShrines
	public static void shrinesLoaded() {
		System.out.println(prefix+"Shrines loaded.");
	}
	public static void shrinesLoadError() {
		System.out.println(prefixError+"Could not load shrines file.");
	}
	
	//DARGraves
	public static void gravesLoaded() {
		System.out.println(prefix+"Graves loaded.");
	}
	
	//Spout
	public static void foundSpout() {
		System.out.println(prefix +"Found Spout-plugin!");
	}
}
