package muCkk.DeathAndRebirth.Messages;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

public class DARMessages {

	private static boolean spout;
	
	public DARMessages(boolean spout) {
		DARMessages.spout = spout;
	}
	
	public void playerNotDead(Player player, String target) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.playerNotDead(player, target);
		}
		else {
			DARMessagesChat.playerNotDead(player, target);
		}
	}
	public void souldNotBound(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.souldNotBound(player);
		}
		else {
			DARMessagesChat.souldNotBound(player);
		}
	}
	
	public void graveProtected(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.graveProtected(player);
		}
		else {
			DARMessagesChat.graveProtected(player);
		}
	}
	public void shrineCantBuild(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.shrineCantBuild(player);
		}
		else {
			DARMessagesChat.shrineCantBuild(player);
		}
	}
	public void tooFarAway(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.tooFarAway(player);
		}
		else {
			DARMessagesChat.tooFarAway(player);
		}
	}
	public void cantDoThat(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.cantDoThat(player);
		}
		else {
			DARMessagesChat.cantDoThat(player);
		}
	}
	public void shrineCantBeDestroyed(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.shrineCantBeDestroyed(player);
		}
		else {
			DARMessagesChat.shrineCantBeDestroyed(player);
		}
	}
	public void nameNotFound(Player player)  {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.nameNotFound(player);
		}
		else {
			DARMessagesChat.nameNotFound(player);
		}
	}
	public void nameAlreadyExists(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.nameAlreadyExists(player);
		}
		else {
			DARMessagesChat.nameAlreadyExists(player);
		}
	}
	public void youWereReborn(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.youWereReborn(player);
		}
		else {
			DARMessagesChat.youWereReborn(player);
		}
	}
	public void youResurrected(Player player, Player target) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.youResurrected(player, target);
		}
		else {
			DARMessagesChat.youResurrected(player, target);
		}
	}
	public void cantAttackGhosts(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.cantAttackGhosts(player);
		}
		else {
			DARMessagesChat.cantAttackGhosts(player);
		}		
	}
	public void boundShrine(Player player) {
		if (spout && SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
			DARMessagesSpout.boundShrine(player);
		}
		else {
			DARMessagesChat.boundShrine(player);
		}
	}
	public void youAreNotDead(Player player) {
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
	
	// *** System messages ***
	// stay in chat form
	public void shrineAlreadyAtLoc(Player player) {
		player.sendMessage("There is already a shrine at that location.");
	}
	public void noPermission(Player player) {
		player.sendMessage("You don't have permission to do that.");
	}
	
	public void reloadComplete(Player player) {
		player.sendMessage("[Death and Rebirth] Reload complete.");
	}
	public void noShrinesFound(Player player) {
		player.sendMessage("No shrines were found.");
	}
	public void worldEnabled(Player player, String world) {
		player.sendMessage("Death and Rebirth enabled for world "+world +".");
	}
	public void worldDisabled(Player player, String world) {
		player.sendMessage("Death and Rebirth disabled for world " +world +".");
	}

	public static void flyModeToggle(Player player, String string) {
		player.sendMessage("Flymode is now "+string);
	}

	public static void shrineModeToggle(Player player, String string) {
		player.sendMessage("ShrineOnly is now "+string);
	}

	public static void blockGhostInteractionToggle(Player player, String string) {
		player.sendMessage("Block ghost interaction is now "+string);
	}
	public static void playerDied(final Player player) {
		new Thread() {
			@Override
			public void run() {				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.sendMessage("You are now a ghost!");
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
