package muCkk.DeathAndRebirth.Messages;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public final class DARMessagesSpout {

	private static String title = "Death & Rebirth";
	private static Material mat = Material.BONE;
	
	//		sp.sendNotification(title, msg, mat);
	
	public static void playerNotDead(Player player, String target) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = target +" is not dead";
		sp.sendNotification(title, msg, mat);
	}
	public static void souldNotBound(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "Soul not bound";
		sp.sendNotification(title, msg, mat);
	}
	public static void graveProtected(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "This grave is protected";
		sp.sendNotification(title, msg, mat);
	}
	public static void shrineCantBuild(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "You can't build on shrines";
		sp.sendNotification(title, msg, mat);
	}
	public static void tooFarAway(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "You are too far away";
		sp.sendNotification(title, msg, mat);
	}
	public static void cantDoThat(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "You can't do that";
		sp.sendNotification(title, msg, mat);
	}
	public static void shrineCantBeDestroyed(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "You can't destroy shrines";
		sp.sendNotification(title, msg, mat);
	}
	public static void nameNotFound(Player player)  {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "Name not found";
		sp.sendNotification(title, msg, mat);
	}
	public static void nameAlreadyExists(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "That name already exists";
		sp.sendNotification(title, msg, mat);
	}
	public static void youWereReborn(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "You were reborn";
		sp.sendNotification(title, msg, mat);
	}
	public static void youResurrected(Player player, Player target) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "You resurrected "+target.getName();
		sp.sendNotification(title, msg, mat);
	}
	public static void cantAttackGhosts(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "You can't attack ghosts";
		sp.sendNotification(title, msg, mat);
	}
	public static void boundShrine(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "Your soul is now bound";
		sp.sendNotification(title, msg, mat);
	}
	public static void youAreNotDead(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "You are not dead";
		sp.sendNotification(title, msg, mat);
	}
	public static void youHaveToStandOnShrine(Player player) {
		SpoutPlayer sp = (SpoutPlayer) player;
		String msg = "No shrine here";
		sp.sendNotification(title, msg, mat);
	}
}
