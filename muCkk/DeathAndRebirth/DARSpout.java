package muCkk.DeathAndRebirth;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

public class DARSpout {

	/**
	 * Called when a player dies
	 * Sets the skin to a ghost skin and plays a sound
	 * @param player which dies
	 */
	public static void playerDied(Player player, String sound) {
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		Plugin spoutPlugin = player.getServer().getPluginManager().getPlugin("SpoutTester");
		
		// *** Sound effect ***
		SoundManager soundM = SpoutManager.getSoundManager();
		soundM.playCustomSoundEffect(spoutPlugin, sp, sound, false);		
		
		// TODO !!! sky effect when spout releases it
	}
	
	/**
	 * Called when a player is resurrected
	 * Resets the skin and plays a sound
	 * @param player who gets resurrected
	 */
	public static void playerRes(Player player, String sound) {
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		
		// *** Skin ***
		AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
		appearanceM.resetGlobalSkin(sp);
		playResSound(player,sound);
		// TODO !!! sky effect when spout releases it		
	}
	
	public static void playResSound(Player player, String sound) {
		Plugin spoutPlugin = player.getServer().getPluginManager().getPlugin("SpoutTester");
		SoundManager soundM = SpoutManager.getSoundManager();
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		soundM.playCustomSoundEffect(spoutPlugin, sPlayer, sound, false);
	}

	/**
	 * Changes the skin ofthe player to a ghost skin.
	 * The thread is needed because minecraft resets the players skin on respawn.
	 * @param player which gets a new skin
	 */
	public static void setGhostSkin(final Player player, final String skin) {
		// wait for the player to spawn		
		new Thread() {
			@Override
			public void run() {				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					System.out.println("[Death and Rebirth] Error: Could not sleep while setting ghost skin.");
					e.printStackTrace();
				}
				// *** Skin ***
				SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
				AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
				appearanceM.setGlobalSkin(sPlayer, skin);
			}
		}.start();
	}
}
