package muCkk.DeathAndRebirth.messages;

import java.util.logging.Logger;

public final class Errors {

	private static final Logger log = Logger.getLogger("Minecraft");
	private static String prefix = "[Death and Rebirth] ";
	private static String prefixError = "[Death and Rebirth] ERROR: ";
	
	
	// ___ ERRORS ___

	public static void couldNotReadFile(String file) {
		log.info(prefixError+"Could not read "+file);
	}
	
	// Config
	public static void savingConfig() {
		log.info(prefixError +"Could not save config!");
	}
	public static void loadingConfig() {
		log.info(prefixError +"Could not load config!");
	}
	public static void messagesLoaded() {
		log.info(prefix+"Messages loaded.");
	}
	
	
	// PlayerListener
	public static void readingURL() {
		log.info(prefixError +"Reading URL");
	}
	public static void openingURL() {
		log.info(prefixError +"Opening URL");
	}
	
	//DARHandler
	public static void corruptGhostFile() {
		log.info(prefixError+"Corrupt ghost file!");
	}
	public static void couldNotReadGhostFile() {
		log.info(prefixError+"Could not read ghost file!");
	}
	public static void couldNotSaveGhostFile() {
		log.info(prefixError+"Could not save ghost-file!");
	}
	public static void markersFileErrors() {
		log.info(prefixError+"The markers file has errors.");
	}
	public static void markersFileReading() {
		log.info(prefixError+"Could not read markers file.");
	}
	public static void markersFileSaving() {
		log.info(prefixError+"Could not save markers file.");
	}
	public static void ghostNameWrong() {
		log.info(prefixError+"Wrong ghostName option. Check if it's set right.");
	}
	public static void whileRessing() {
		log.info(prefixError+"While resurrecting.");
	}
	
	//DARSigns
	public static void corruptSignsFile() {
		log.info(prefixError+"Corrupt grave file!");
	}
	public static void couldNotReadSignsFile() {
		log.info(prefixError+"Could not read grave file!");
	}
	public static void couldNotSaveSignsFile() {
		log.info(prefixError+"Could not save grave-file!");
	}
	
	// Drops
	public static void couldNotReadDropsFile() {
		log.info(prefixError+"Could not read drops file!");
	}
	// Spout
	public static void couldNotReadSpoutFile() {
		log.info(prefixError+"Could not read spout file!");
	}
	public static void couldNotSleepSkin() {
		log.info(prefixError+"Could not sleep while setting ghost skin.");
	}
	public static void couldNotSleepSound() {
		log.info(prefixError+"Could not sleep - ghostSoundEffect");
	}
	// ___ MESSAGES ___
	
	//DARShrines
	public static void shrinesLoaded() {
		log.info(prefix+"Shrines loaded.");
	}
	public static void shrinesLoadError() {
		log.info(prefixError+"Could not load shrines file.");
	}
	
	//DARGraves
	public static void gravesLoaded() {
		log.info(prefix+"Graves loaded.");
	}
	
	//Spout
	public static void foundSpout() {
		log.info(prefix +"Found Spout-plugin!");
	}
}
