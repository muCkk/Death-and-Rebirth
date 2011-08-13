package muCkk.DeathAndRebirth.Messages;

import org.bukkit.entity.Player;

public class DARMessages {

	private boolean spout;
	
	public DARMessages(boolean spout) {
		this.spout = spout;
	}
	
	public void playerNotDead(Player player, String target) {
		if (spout) {
			DARMessagesSpout.playerNotDead(player, target);
		}
		else {
			DARMessagesChat.playerNotDead(player, target);
		}
	}
	public void souldNotBound(Player player) {
		if (spout) {
			DARMessagesSpout.souldNotBound(player);
		}
		else {
			DARMessagesChat.souldNotBound(player);
		}
	}
	
	public void graveProtected(Player player) {
		if (spout) {
			DARMessagesSpout.graveProtected(player);
		}
		else {
			DARMessagesChat.graveProtected(player);
		}
	}
	public void shrineCantBuild(Player player) {
		if (spout) {
			DARMessagesSpout.shrineCantBuild(player);
		}
		else {
			DARMessagesChat.shrineCantBuild(player);
		}
	}
	public void tooFarAway(Player player) {
		if (spout) {
			DARMessagesSpout.tooFarAway(player);
		}
		else {
			DARMessagesChat.tooFarAway(player);
		}
	}
	public void cantDoThat(Player player) {
		if (spout) {
			DARMessagesSpout.cantDoThat(player);
		}
		else {
			DARMessagesChat.cantDoThat(player);
		}
	}
	public void shrineCantBeDestroyed(Player player) {
		if (spout) {
			DARMessagesSpout.shrineCantBeDestroyed(player);
		}
		else {
			DARMessagesChat.shrineCantBeDestroyed(player);
		}
	}
	public void nameNotFound(Player player)  {
		if (spout) {
			DARMessagesSpout.nameNotFound(player);
		}
		else {
			DARMessagesChat.nameNotFound(player);
		}
	}
	public void nameAlreadyExists(Player player) {
		if (spout) {
			DARMessagesSpout.nameAlreadyExists(player);
		}
		else {
			DARMessagesChat.nameAlreadyExists(player);
		}
	}
	public void youWereReborn(Player player) {
		if (spout) {
			DARMessagesSpout.youWereReborn(player);
		}
		else {
			DARMessagesChat.youWereReborn(player);
		}
	}
	public void youResurrected(Player player, Player target) {
		if (spout) {
			DARMessagesSpout.youResurrected(player, target);
		}
		else {
			DARMessagesChat.youResurrected(player, target);
		}
	}
	public void cantAttackGhosts(Player player) {
		if (spout) {
			DARMessagesSpout.cantAttackGhosts(player);
		}
		else {
			DARMessagesChat.cantAttackGhosts(player);
		}		
	}
	public void boundShrine(Player player) {
		if (spout) {
			DARMessagesSpout.boundShrine(player);
		}
		else {
			DARMessagesChat.boundShrine(player);
		}
	}
	public void youAreNotDead(Player player) {
		if (spout) {
			DARMessagesSpout.youAreNotDead(player);
		}
		else {
			DARMessagesChat.youAreNotDead(player);
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
}
