package muCkk.DeathAndRebirth.otherPlugins;

import java.io.File;
import java.util.Random;

import muCkk.DeathAndRebirth.config.CFG;
import muCkk.DeathAndRebirth.config.Config;
import muCkk.DeathAndRebirth.ghost.Ghosts;
import muCkk.DeathAndRebirth.messages.Errors;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SkyManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;
import org.getspout.spoutapi.sound.SoundManager;

public class DARSpout {

	private Config config;
	private Ghosts ghosts;
	
	private String dir;
	private File spoutFile;
	private Configuration yml;
	
	public DARSpout(Config config, String dir) {
		this.config = config;
		this.dir = dir;
		this.spoutFile = new File(dir+"/spout");
		load();
	}
	
	public void load() {
		if(!spoutFile.exists()){
            try {
            	new File(dir).mkdir();
                spoutFile.createNewFile(); 
            } catch (Exception e) {
            	Errors.couldNotReadSpoutFile();
            	e.printStackTrace();
            }
        } else {
        	// loaded
        }
		try {
            yml = new Configuration(spoutFile);
            yml.load();
        } catch (Exception e) {
        	Errors.couldNotReadSpoutFile();
        	e.printStackTrace();
        }
	}

	/**
	 * Saves the current information to a file
	 */
	public void save() {
		yml.save();
	}
	
	public void setGhosts(Ghosts ghosts) {
		this.ghosts = ghosts;
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
		String skin = yml.getString(player.getName()+".skin");
		if (skin != null) appearanceM.setGlobalSkin(sp, skin);
		
		resetTtitle(player);

		String textPack = config.getString(CFG.GHOST_TEXTPACK);
		if(!textPack.equalsIgnoreCase("")) sp.resetTexturePack();
		
		SkyManager sky = SpoutManager.getSkyManager();
		if(config.getBoolean(CFG.CHANGE_COLORS)) {
			sky.setCloudColor(sp, Color.remove());
			sky.setFogColor(sp, Color.remove());
			sky.setSkyColor(sp, Color.remove());
		}
		playRebirthSound(player,sound);		
	}
	
	public void setTitle(Player player) {
		if (!config.getString(CFG.GHOST_NAME).equalsIgnoreCase("")) return;
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
		yml.setProperty(player.getName()+".title", appearanceM.getTitle(sp, sp));
		yml.save();
		appearanceM.setGlobalTitle(sp, ghosts.getGhostDisplayName(player));
	}
	
	public void resetTtitle(Player player) {
		if (!config.getString(CFG.GHOST_NAME).equalsIgnoreCase("")) return;
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
		appearanceM.setGlobalTitle(sp, yml.getString(player.getName()+".title"));
	}
	
	public void playRebirthSound(Player player, String sound) {
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		if(!sPlayer.isSpoutCraftEnabled()) return;
		Plugin spoutPlugin = player.getServer().getPluginManager().getPlugin("Death and Rebirth");
		SoundManager soundM = SpoutManager.getSoundManager();
		soundM.playGlobalCustomSoundEffect(spoutPlugin, sound, false, player.getLocation());
	}
	public void playResSound(Player player, String sound) {
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		if(!sPlayer.isSpoutCraftEnabled()) return;
		Plugin spoutPlugin = player.getServer().getPluginManager().getPlugin("Death and Rebirth");
		SoundManager soundM = SpoutManager.getSoundManager();
		soundM.playGlobalCustomSoundEffect(spoutPlugin, sound, false, player.getLocation());
	}

	/**
	 * Changes the skin of the player to a ghost skin.
	 * The thread is needed because minecraft resets the players skin on respawn.
	 * @param player which gets a new skin
	 */
	public void setDeathOptions(final Player player, final String skin) {
		final SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		if(!sPlayer.isSpoutCraftEnabled()) return;

		// wait for the player to spawn
		new Thread() {
			@Override
			public void run() {				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Errors.couldNotSleepSkin();
				}
				// Get all those managers
				AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
				SkyManager sky = SpoutManager.getSkyManager();
				
				// skin
				String skinUrl = appearanceM.getSkinUrl(sPlayer, sPlayer);
				if ( skinUrl != null) yml.setProperty(player.getName()+".skin", skinUrl);
				setTitle(player);
				yml.save();
				appearanceM.setGlobalSkin(sPlayer, skin);
				
				// texturepack
				String textPack = config.getString(CFG.GHOST_TEXTPACK);
				if(!textPack.equalsIgnoreCase("")) sPlayer.setTexturePack(textPack);
				
				// colors
				if(config.getBoolean(CFG.CHANGE_COLORS)) {
					float [] skycol = config.getFloatColor(CFG.GHOST_SKY);
					float [] fogcol = config.getFloatColor(CFG.GHOST_FOG);
					float [] cloudcol = config.getFloatColor(CFG.GHOST_CLOUDS);
					sky.setCloudColor(sPlayer, new Color(cloudcol[0], cloudcol[1], cloudcol[2]));
					sky.setFogColor(sPlayer, new Color(fogcol[0], fogcol[1], fogcol[2]));
					sky.setSkyColor(sPlayer, new Color(skycol[0], skycol[1], skycol[2]));
				}
				
				// sound effect
				if (config.getBoolean(CFG.GHOST_SOUND_EFFECTS)) {
					new Thread() {
						@Override
						public void run() {
							final SoundManager soundM = SpoutManager.getSoundManager();
							Random rand = new Random();
							int r = rand.nextInt(12)+3;
							while(ghosts.isGhost(sPlayer)) {
								soundM.playSoundEffect(sPlayer, SoundEffect.GHAST_MOAN, player.getLocation(), 10, 100);
								r = rand.nextInt(12)+3;
								try {
									sleep(r*1000);
								} catch (InterruptedException e) {
									Errors.couldNotSleepSound();
								}
							}
						}
					}.start();
				}
			}
		}.start();
	}
}
