package muCkk.DeathAndRebirth.otherPlugins;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.ghost.Ghosts;
import muCkk.DeathAndRebirth.messages.Errors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SkyManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;
import org.getspout.spoutapi.sound.SoundManager;

public class DARSpout {

	private DAR plugin;
	private Ghosts ghosts;
	
	private File spoutFile;
	private FileConfiguration customConfig = null;
	
	public DARSpout(DAR dar, String dir) {
		this.plugin = dar;
		this.spoutFile = new File(dir+"/spout");
	}
	
	public void reloadCustomConfig() {
	    if (spoutFile == null) {
	    	spoutFile = new File(plugin.getDataFolder(), "customConfig.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(spoutFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("customConfig.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getCustomConfig() {
	    if (customConfig == null) {
	        reloadCustomConfig();
	    }
	    return customConfig;
	}
	
	public void saveCustomConfig() {
	    if (customConfig == null || spoutFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(spoutFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + spoutFile, ex);
	    }
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
		String skin = getCustomConfig().getString(player.getName()+".skin");
		if (skin != null) appearanceM.setGlobalSkin(sp, skin);
		
		resetTtitle(player);

		String textPack = plugin.getConfig().getString("GHOST_TEXTPACK");
		if(!textPack.equalsIgnoreCase("")) sp.resetTexturePack();
		
		SkyManager sky = SpoutManager.getSkyManager();
		if(plugin.getConfig().getBoolean("CHANGE_COLORS")) {
			sky.setCloudColor(sp, Color.remove());
			sky.setFogColor(sp, Color.remove());
			sky.setSkyColor(sp, Color.remove());
		}
		playRebirthSound(player,sound);		
	}
	
	public void setTitle(Player player) {
		if (!plugin.getConfig().getString("GHOST_NAME").equalsIgnoreCase("")) return;
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
		getCustomConfig().set(player.getName()+".title", appearanceM.getTitle(sp, sp));
		saveCustomConfig();
		appearanceM.setGlobalTitle(sp, ghosts.getGhostDisplayName(player));
	}
	
	public void resetTtitle(Player player) {
		if (!plugin.getConfig().getString("GHOST_NAME").equalsIgnoreCase("")) return;
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
		appearanceM.setGlobalTitle(sp, getCustomConfig().getString(player.getName()+".title"));
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
				if ( skinUrl != null) getCustomConfig().set(player.getName()+".skin", skinUrl);
				setTitle(player);
				saveCustomConfig();
				appearanceM.setGlobalSkin(sPlayer, skin);
				
				// texturepack
				String textPack = plugin.getConfig().getString("GHOST_TEXTPACK");
				if(!textPack.equalsIgnoreCase("")) sPlayer.setTexturePack(textPack);
				
				// colors
				if(plugin.getConfig().getBoolean("CHANGE_COLORS")) {
					float [] skycol = plugin.getFloatColor("GHOST_SKY");
					float [] fogcol = plugin.getFloatColor("GHOST_FOG");
					float [] cloudcol = plugin.getFloatColor("GHOST_CLOUDS");
					sky.setCloudColor(sPlayer, new Color(cloudcol[0], cloudcol[1], cloudcol[2]));
					sky.setFogColor(sPlayer, new Color(fogcol[0], fogcol[1], fogcol[2]));
					sky.setSkyColor(sPlayer, new Color(skycol[0], skycol[1], skycol[2]));
				}
				
				// sound effect
				if (plugin.getConfig().getBoolean("GHOST_SOUND_EFFECTS")) {
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
