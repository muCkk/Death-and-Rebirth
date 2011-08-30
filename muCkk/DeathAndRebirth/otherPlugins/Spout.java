package muCkk.DeathAndRebirth.otherPlugins;

import java.io.File;

import muCkk.DeathAndRebirth.config.DARProperties;
import muCkk.DeathAndRebirth.ghost.DARGhosts;
import muCkk.DeathAndRebirth.messages.DARErrors;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SkyManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

public class Spout {

	private DARProperties config;
//	private HashMap<String, String> skinSaver;
//	private HashMap<String, String> titleSaver;
	private DARGhosts ghosts;
	
	private String dir;
	private File spoutFile;
	private Configuration yml;
	
	public Spout(DARProperties config, String dir) {
		this.config = config;
//		skinSaver = new HashMap<String, String>();
//		titleSaver = new HashMap<String, String>();
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
            	DARErrors.couldNotReadSpoutFile();
            	e.printStackTrace();
            }
        } else {
        	DARErrors.gravesLoaded();
        }
		try {
            yml = new Configuration(spoutFile);
            yml.load();
        } catch (Exception e) {
        	DARErrors.couldNotReadSpoutFile();
        	e.printStackTrace();
        }
	}

	/**
	 * Saves the current information to a file
	 */
	public void save() {
		yml.save();
	}
	
	public void setGhosts(DARGhosts ghosts) {
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
		SkyManager sky = SpoutManager.getSkyManager();
		if(config.changeColors()) {
			sky.setCloudColor(sp, Color.remove());
			sky.setFogColor(sp, Color.remove());
			sky.setSkyColor(sp, Color.remove());
		}
		playResSound(player,sound);		
	}
	
	public void setTitle(Player player) {
		if (config.getGhostName() == "") return;
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
		yml.setProperty(player.getName()+".title", appearanceM.getTitle(sp, sp));
		yml.save();
		appearanceM.setGlobalTitle(sp, ghosts.getGhostDisplayName(player));
	}
	
	public void resetTtitle(Player player) {
		if (config.getGhostName() == "") return;
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
		appearanceM.setGlobalTitle(sp, yml.getString(player.getName()+".title"));
	}
	
	public void playResSound(Player player, String sound) {
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		if(!sPlayer.isSpoutCraftEnabled()) return;
		Plugin spoutPlugin = player.getServer().getPluginManager().getPlugin("Death and Rebirth");
		SoundManager soundM = SpoutManager.getSoundManager();
		soundM.playCustomSoundEffect(spoutPlugin, sPlayer, sound, false);
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
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					System.out.println("[Death and Rebirth] Error: Could not sleep while setting ghost skin.");
					e.printStackTrace();
				}
				// *** Skin ***
				
				AppearanceManager appearanceM = SpoutManager.getAppearanceManager();
				SkyManager sky = SpoutManager.getSkyManager();
				String skinUrl = appearanceM.getSkinUrl(sPlayer, sPlayer);
				if ( skinUrl != null) yml.setProperty(player.getName()+".skin", skinUrl);
				setTitle(player);
				yml.save();
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
