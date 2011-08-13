package muCkk.DeathAndRebirth.Messages;

import org.bukkit.entity.Player;

public final class DARMessagesChat {

	public static void playerNotDead(Player player, String target) {
		player.sendMessage("Player "+target +" is not dead.");
	}
	public static void souldNotBound(Player player) {
		player.sendMessage("Your soul is not bound anywhere. You have to search for a shrine.");
	}
	public static void graveProtected(Player player) {
		player.sendMessage("This grave is protected!");
	}
	public static void shrineCantBuild(Player player) {
		player.sendMessage("You can't build on shrines.");
	}
	public static void tooFarAway(Player player) {
		player.sendMessage("You are too far away.");
	}
	public static void cantDoThat(Player player) {
		player.sendMessage("You are dead. Ghosts can't do that.");
	}
	public static void shrineCantBeDestroyed(Player player) {
		player.sendMessage("You can't destroy shrines.");
	}
	public static void nameNotFound(Player player)  {
		player.sendMessage("Name not found!");
	}
	public static void nameAlreadyExists(Player player) {
		player.sendMessage("That name already exists.");
	}
	public static void youWereReborn(Player player) {
		player.sendMessage("You were reborn.");
	}
	public static void youResurrected(Player player, Player target) {
		player.sendMessage("You resurrected "+target.getName());
	}
	public static void cantAttackGhosts(Player player) {
		player.sendMessage("You can't attack ghosts.");		
	}
	public static void boundShrine(Player player) {
		player.sendMessage("Your soul is now bound to this shrine");
	}
	public static void youAreNotDead(Player player) {
		player.sendMessage("You are not dead.");
	}
}
