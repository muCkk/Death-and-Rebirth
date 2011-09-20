package muCkk.DeathAndRebirth.messages;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Messenger {

	private boolean spoutEnabled;
	private String dir;
	private File file; 
	private Configuration yml;
	
	private static String title = "Death & Rebirth";
	private static Material mat = Material.BONE;

	public Messenger(String dir) {
		this.dir = dir;
		this.file = new File(dir+"/messages.yml");
		this.spoutEnabled = false;
		load();
	}
	
	public void save() {
		yml.save();
	}
	
	public void load() {
		if(!file.exists()){
            try {
            	new File(dir).mkdir();
                file.createNewFile(); 
            } catch (Exception ex) {
            }
        } else {
        	Errors.messagesLoaded();
        }
		try {
            yml = new Configuration(file);
            yml.load();
        } catch (Exception e) {
        }
	}
//	********************************************************************************************	
	public void send(Player player, Messages msg) {
		String ownMessage = yml.getString(msg.toString());
		if (ownMessage == null) {
			if (checkSpout(player)) spout(player, msg.msg());
			else					chat(player, msg.msg());
		}
		else {
			if (checkSpout(player)) spout(player, ownMessage);
			else					chat(player, ownMessage);
		}
	}
	
	public void sendSkill(Player player, Messages msg, String skillType) {
		String ownMessage = yml.getString(msg.msg());
		if (ownMessage == null) {
			if (checkSpout(player)) spout(player, msg.msg().replace("%skill%", skillType));
			else					chat(player, msg.msg().replace("%skill%", skillType));
		}
		else {
			ownMessage = ownMessage.replace("%skill%", skillType);
			if (checkSpout(player)) spout(player, ownMessage);
			else					chat(player, ownMessage);
		}
	}
	
	public void send(Player player, Messages msg, String arg) {
		String ownMessage = yml.getString(msg.toString());
		if (ownMessage == null) {
			String message = msg.msg()+" "+arg;
			if (checkSpout(player)) spout(player, message);
			else					chat(player, message);
		}
		else {
			String message = ownMessage+" "+arg;
			if (checkSpout(player)) spout(player, message);
			else					chat(player, message);
		}
	}
//	*******************
	public void sendChat(Player player, Messages msg) {
		String ownMessage = yml.getString(msg.toString());
		if (ownMessage == null)	chat(player, msg.msg());
		else					chat(player, ownMessage);
	}
	
	public void sendChat(Player player, Messages msg, String arg) {
		String ownMessage = yml.getString(msg.toString());
		if (ownMessage == null) {
			String message = msg.msg()+" "+arg;
			chat(player, message);
		}
		else {
			String message = ownMessage+" "+arg;
			chat(player, message);
		}
	}
//	************************************************************************************
	private void spout(Player player, String msg) {
		SpoutPlayer sp = (SpoutPlayer) player;
		sp.sendNotification(title, msg, mat);
	}
	
	public void chat(Player player, String msg) {
		if(player != null)	player.sendMessage(msg);
		else				System.out.println(msg);
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
					System.out.println("[Death and Rebirth] Error: Could not sleep while playerDied().");
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
