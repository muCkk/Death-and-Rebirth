package muCkk.DeathAndRebirth.messages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.ghost.Ghosts;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Messenger {

	private static final Logger log = Logger.getLogger("Minecraft");
	private boolean spoutEnabled;
	private DAR plugin;
	private Ghosts ghosts;
	private File messagesFile; 
	private FileConfiguration customConfig = null;
	
	private static String title = "Death & Rebirth";
	private static Material mat = Material.BONE;

	public Messenger(DAR instance) {
		this.plugin = instance;
		this.messagesFile = new File(plugin.getDataFolder()+"/messages.yml");
		this.spoutEnabled = false;
	}
		
	public void reloadCustomConfig() {
	    if (messagesFile == null) {
	    	messagesFile = new File(plugin.getDataFolder(), "messages.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(messagesFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("messages.yml");
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
	    if (customConfig == null || messagesFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(messagesFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + messagesFile, ex);
	    }
	}
//	********************************************************************************************	
	public void send(Player player, Messages msg) {
		String ownMessage = getCustomConfig().getString(msg.toString(), msg.msg());//yml.getString(msg.toString());
		if (checkSpout(player)) spout(player, ownMessage);
		else					chat(player, ownMessage);
	}
	
	public void sendSkill(Player player, Messages msg, String skillType) {
		String ownMessage = getCustomConfig().getString(msg.toString(), msg.msg()); //yml.getString(msg.msg());
		if (checkSpout(player)) spout(player,ownMessage.replace("%skill%", skillType));
		else					chat(player, ownMessage.replace("%skill%", skillType));
	}
	
	public void send(Player player, Messages msg, String arg) {
		String ownMessage = getCustomConfig().getString(msg.toString(), msg.msg());
		String message = ownMessage+" "+arg;
		if (checkSpout(player)) spout(player, message);
		else					chat(player, message);
	}
//	*******************
	public void sendChat(Player player, Messages msg) {
		String ownMessage = getCustomConfig().getString(msg.toString(), msg.msg());
		if (ownMessage == null)	chat(player, msg.msg());
		else					chat(player, ownMessage);
	}
	
	public void sendChat(Player player, Messages msg, String arg) {
		String ownMessage = getCustomConfig().getString(msg.toString(), msg.msg());
		String message = ownMessage+" "+arg;
		chat(player, message);
	}
//	************************************************************************************
	private void spout(Player player, String msg) {
	  String playerName = player.getName();
	  String worldName = plugin.worldData.get("worldName");
	  //checks if player wants to get Messages
	  if(ghosts.getCustomConfig().getBoolean("players." +playerName +"."+worldName +".msg") == true)
	  {
		SpoutPlayer sp = (SpoutPlayer) player;
		sp.sendNotification(title, msg, mat);
	  }
	  else if(ghosts.getCustomConfig().getBoolean("players." +playerName +"."+worldName +".msg") == false)
	  return;
	  else
	  {
			SpoutPlayer sp = (SpoutPlayer) player;
			sp.sendNotification(title, msg, mat);
	  }
	}
	
	@SuppressWarnings("unused")
	public void chat(Player player, String msg) {
	  String playerName = player.getName();
	  String worldName = plugin.worldData.get("worldName");
	  //checks if player wants to get Messages
	  if(ghosts.getCustomConfig().getBoolean("players." +playerName +"."+worldName +".msg") == true)
	  {
		if(player != null)	player.sendMessage(msg);
		else				System.out.println(msg);
	  }
	  else if(ghosts.getCustomConfig().getBoolean("players." +playerName +"."+worldName +".msg") == false)
	  return;
	  else
	  {
		if(player != null)	player.sendMessage(msg);
		else				System.out.println(msg);
	  }
		  
	}
//	********************************************************************************
	public void setSpout(boolean b) {
		this.spoutEnabled = b;
	}
	
	// *** private methods ***
	private boolean checkSpout(Player player) {
		if (player == null) return false;
		else 				return (spoutEnabled && SpoutManager.getPlayer(player).isSpoutCraftEnabled());
	}
	

	
	public void playerDied(final Player player) {
		new Thread() {
			@Override
			public void run() {				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					log.info("[Death and Rebirth] Error: Could not sleep while playerDied().");
					e.printStackTrace();
				}
				if (checkSpout(player)) {
					spout(player, Messages.playerDied.msg());
				}
				else {
					send(player, Messages.playerDied);
				}
			}
		}.start();
	}
}
