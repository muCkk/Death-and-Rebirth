package muCkk.DeathAndRebirth.otherPlugins;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.messages.Messages;

import org.bukkit.entity.Player;

import com.nijikokun.register.payment.Method;

public class DARConomy {
	
	public static Method register;
	private DAR plugin;
	
	public DARConomy(DAR plugin) {
		this.plugin = plugin;
	}
	
	public void take(Player player, double amount) {
		if(amount > register.getAccount(player.getName()).balance()) return;
		register.getAccount(player.getName()).subtract(amount);
		plugin.message.send(player, Messages.lostMoney, String.valueOf(amount));
	}
}
