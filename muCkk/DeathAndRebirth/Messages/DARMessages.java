package muCkk.DeathAndRebirth.Messages;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

public class DARMessages {

	private static boolean spout;
	private static Player player;
		
	// *** Config stuff *************************************************
	public static void save(Player player) {
		DARMessages.player = player;
	}

	public static void setSpout(boolean b) {
		DARMessages.spout = b;
	}
	// *** Messages *****************************************************
	public static void souldNotBound(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.souldNotBound(player);
		}
		else {
			DARMessagesChat.souldNotBound(player);
		}
	}
	
	public static void graveProtected(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.graveProtected(player);
		}
		else {
			DARMessagesChat.graveProtected(player);
		}
	}
	public static void shrineCantBuild(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.shrineCantBuild(player);
		}
		else {
			DARMessagesChat.shrineCantBuild(player);
		}
	}
	public static void tooFarAway(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.tooFarAway(player);
		}
		else {
			DARMessagesChat.tooFarAway(player);
		}
	}
	public static void cantDoThat(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.cantDoThat(player);
		}
		else {
			DARMessagesChat.cantDoThat(player);
		}
	}
	public static void shrineCantBeDestroyed(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.shrineCantBeDestroyed(player);
		}
		else {
			DARMessagesChat.shrineCantBeDestroyed(player);
		}
	}
	public static void nameAlreadyExists(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.nameAlreadyExists(player);
		}
		else {
			DARMessagesChat.nameAlreadyExists(player);
		}
	}
	public static void youWereReborn(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.youWereReborn(player);
		}
		else {
			DARMessagesChat.youWereReborn(player);
		}
	}
	public static void cantAttackGhosts(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.cantAttackGhosts(player);
		}
		else {
			DARMessagesChat.cantAttackGhosts(player);
		}		
	}
	public static void boundShrine(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.boundShrine(player);
		}
		else {
			DARMessagesChat.boundShrine(player);
		}
	}
	public static void youAreNotDead(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.youAreNotDead(player);
		}
		else {
			DARMessagesChat.youAreNotDead(player);
		}
	}
	
	public static void youHaveToStandOnShrine(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.youHaveToStandOnShrine(player);
		}
		else {
			DARMessagesChat.youHaveToStandOnShrine(player);
		}
	}
	// *** Console and Player *****************************************
	public static void nameNotFound()  {
		if (player != null) {
			if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
				DARMessagesSpout.nameNotFound(player);
			}
			else {
				DARMessagesChat.nameNotFound(player);
			}
		}
		else {
			System.out.println("Name not found!");
		}
	}
	public static void playerNotDead(Player player, String target) {
		if (player != null) {
			if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
				DARMessagesSpout.playerNotDead(player, target);
			}
			else {
				DARMessagesChat.playerNotDead(player, target);
			}
		}
		else {
			System.out.println("Player is not dead.");
		}
		
	}
	public static void youResurrected(Player target) {
		if (player != null) {
			if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
				DARMessagesSpout.youResurrected(player, target);
			}
			else {
				DARMessagesChat.youResurrected(player, target);
			}
		}
		else {
			System.out.println("You resurrected "+target.getName());
		}
	}
	
	// *** System messages ***
	// stay in chat form
	
	// *** console and player ****************************************************
	public static void noPermission() {
		if(player != null) {
			player.sendMessage("You don't have permission to do that.");
		}
		else{
			System.out.println("You don't have permission to do that.");
		}
	}	
	public static void reloadComplete() {
		if(player != null) {
			player.sendMessage("[Death and Rebirth] Reload complete.");
		}
		else {
			System.out.println("[Death and Rebirth] Reload complete.");
		}
	}
	public static void worldEnabled(String world) {
		if(player != null) {
			player.sendMessage("Death and Rebirth enabled for world "+world +".");
		}
		else {
			System.out.println("Death and Rebirth enabled for world "+world +".");
		}
	}
	public static void worldDisabled(String world) {
		if(player != null) {
			player.sendMessage("Death and Rebirth disabled for world " +world +".");
		}
		else {
			System.out.println("Death and Rebirth disabled for world " +world +".");
		}
	}
	public static void droppingToggle(String string) {
		if(player != null) {
			player.sendMessage("Dropping is now "+string);
		}
		else {
			System.out.println("Dropping is now "+string);
		}
	}
	public static void versionCheckToggle(String string) {
		if(player != null) {
			player.sendMessage("Version checking is now "+string);
		}
		else {
			System.out.println("Version checking is now "+string);
		}
	}
	public static void flyModeToggle(String string) {
		if(player != null) {
			player.sendMessage("Flymode is now "+string);
		}
		else {
			System.out.println("Flymode is now "+string);
		}
	}
	public static void shrineModeToggle(String string) {
		if(player != null) {
			player.sendMessage("ShrineOnly is now "+string);
		}
		else {
			System.out.println("ShrineOnly is now "+string);
		}
	}
	public static void blockGhostInteractionToggle(String string) {
		if(player != null) {
			player.sendMessage("Block ghost interaction is now "+string);
		}
		else {
			System.out.println("Block ghost interaction is now "+string);
		}
	}
	
	// *** Player only *********************************************
	public static void newVersion(Player player, String version) {
		player.sendMessage("[Death and Rebirth] New version available: "+version);
	}
	public static void shrineAlreadyAtLoc(Player player) {
		player.sendMessage("There is already a shrine at that location.");
	}	
	public static void noShrinesFound(Player player) {
		player.sendMessage("No shrines were found.");
	}
	
	public static void playerDied(final Player player) {
		new Thread() {
			@Override
			public void run() {				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					System.out.println("[Death and Rebirth] Error: Could not sleep while playerDied().");
					e.printStackTrace();
				}
				if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
					DARMessagesSpout.youAreAGhost(player);
				}
				else {
					DARMessagesChat.youAreAGhost(player);
				}
			}
		}.start();
	}

	public static void ghostsCantChat(Player player) {
		player.sendMessage("You try to speak but nobody can hear you.");
	}

	public static void ghostChatToggle(Player player, String string) {
		player.sendMessage("Chatting for ghosts is now "+string);
	}
}
