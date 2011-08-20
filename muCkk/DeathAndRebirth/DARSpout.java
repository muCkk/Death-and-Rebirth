package muCkk.DeathAndRebirth;

import muCkk.DeathAndRebirth.config.DARProperties;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SkyManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

public class DARSpout {

	private DARProperties config;
	
	public DARSpout(DARProperties config) {
		this.config = config;
	}
	
	/**
	 * Called when a player dies
	 * Sets the skin to a ghost skin and plays a sound
	 * @param player which dies
	 */
	public void playerDied(Player player, String sound) {
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(!sp.isSpoutCraftEnabled()) return;
		Plugin spoutPlugin = player.getServer().getPluginManager().getPlugin("Death and Rebirth");
	
		
		// *** Sound effect ***
		SoundManager soundM = SpoutManager.getSoundManager();
		soundM.playCustomSoundEffect(spoutPlugin, sp, sound, false);
	}
	
	/**
	 * Called when a player is resurrected
	 * Resets the skin and plays a sound
	 * @param player who gets resurrected
	 */
	public void playerRes(Player player, String sound) {
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(!sp.isSpoutCraftEnabled()) return;
		
		// *** Skin ***
		AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
		SkyManager sky = SpoutManager.getSkyManager();
		appearanceM.resetGlobalSkin(sp);
		if(config.changeColors()) {
			float [] skycol = config.getnormalSky();
			float [] fogcol = config.getnormalFog();
			float [] cloudcol = config.getnormalClouds();
			sky.setCloudColor(sp, new Color(cloudcol[0], cloudcol[1], cloudcol[2]));
			sky.setFogColor(sp, new Color(fogcol[0], fogcol[1], fogcol[2]));
			sky.setSkyColor(sp, new Color(skycol[0], skycol[1], skycol[2]));
		}
		playResSound(player,sound);		
	}
	
	public void playResSound(Player player, String sound) {
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		if(!sPlayer.isSpoutCraftEnabled()) return;
		Plugin spoutPlugin = player.getServer().getPluginManager().getPlugin("Death and Rebirth");
		SoundManager soundM = SpoutManager.getSoundManager();
		soundM.playCustomSoundEffect(spoutPlugin, sPlayer, sound, false);
	}

	/**
	 * Changes the skin ofthe player to a ghost skin.
	 * The thread is needed because minecraft resets the players skin on respawn.
	 * @param player which gets a new skin
	 */
	public void setGhostSkin(final Player player, final String skin) {
		final SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		if(!sPlayer.isSpoutCraftEnabled()) return;

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
				
				AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
				SkyManager sky = SpoutManager.getSkyManager();
				appearanceM.setGlobalSkin(sPlayer, skin);
				if(config.changeColors()) {
					float [] skycol = config.getGhostSky();
					float [] fogcol = config.getGhostFog();
					float [] cloudcol = config.getGhostClouds();
					sky.setCloudColor(sPlayer, new Color(cloudcol[0], cloudcol[1], cloudcol[2]));
					sky.setFogColor(sPlayer, new Color(fogcol[0], fogcol[1], fogcol[2]));
					sky.setSkyColor(sPlayer, new Color(skycol[0], skycol[1], skycol[2]));
				}
			}
		}.start();
	}
}
