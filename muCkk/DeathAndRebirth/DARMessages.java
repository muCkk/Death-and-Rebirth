package muCkk.DeathAndRebirth;

import org.bukkit.entity.Player;

public final class DARMessages {

	public static void playerNotDead(Player player, String target) {
		player.sendMessage("Player "+target +" is not dead.");
	}
	public static void shrineNotHere(Player player) {
		player.sendMessage("There is no shrine at your location.");
	}
	public static void shrineAlreadyAtLoc(Player player) {
		player.sendMessage("There is already a shrine at that location.");
	}
	public static void graveProtected(Player player) {
		player.sendMessage("This grave is protected!");
	}
	public static void shrineCantBuild(Player player) {
		player.sendMessage("You can't build on shrines.");
	}
	public static void tooFarAway(Player player) {
		player.sendMessage("You are too far away");
	}
	public static void cantUseThat(Player player) {
		player.sendMessage("You are dead. Ghosts can't use that.");
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
	public static void noPermission(Player player) {
		player.sendMessage("You don't have permission to do that.");
		
	}
	public static void youResurrected(Player player, Player target) {
		player.sendMessage("You resurrected "+target.getName());
	}
}
