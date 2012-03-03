package muCkk.DeathAndRebirth.listener;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.messages.Errors;
import muCkk.DeathAndRebirth.otherPlugins.DARConomy;
import muCkk.DeathAndRebirth.otherPlugins.DARmcMMO;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager; 

import com.nijikokun.register.payment.Methods;

public class SListener implements Listener{

	private DAR plugin;
	
	public SListener(DAR plugin) {
		this.plugin = plugin;
	}
	
//	@Override
//    public void onPluginDisable(PluginDisableEvent event) {
//	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPluginEnable(PluginEnableEvent event) {
		String pluginName = event.getPlugin().getDescription().getName();
		PluginManager plugman = plugin.getServer().getPluginManager();
		// economy plugins
		if (!Methods.hasMethod() && Methods.setMethod(plugin.pm)) {
			plugin.darConomy = new DARConomy(plugin);
			DARConomy.register = Methods.getMethod();
        }
		
		// Spout
		if(pluginName.equalsIgnoreCase("spout")) {
			Plugin spoutPlugin = plugman.getPlugin("Spout");
			if (spoutPlugin != null) {
				Errors.foundSpout();
				plugin.message.setSpout(true);
				plugin.getConfig().set("SPOUT_ENABLED", true);
				plugin.message.setSpout(true);
			}
			else {
				plugin.message.setSpout(false);
				plugin.getConfig().set("SPOUT_ENABLED", false);
				plugin.message.setSpout(false);
			}
		}
		// Citizens
		if(pluginName.equalsIgnoreCase("citizens")) {
			Plugin citizensPlugin = plugman.getPlugin("Citizens");
			if (citizensPlugin != null)	plugin.getConfig().set("CITIZENS_ENABLED", true);
			else						plugin.getConfig().set("CITIZENS_ENABLED", false);
		}
		// mcMMO
		if(pluginName.equalsIgnoreCase("mcMMO")) {
			Plugin mcmmoPlug = plugman.getPlugin("mcMMO");
			if (mcmmoPlug != null) plugin.darmcmmo = new DARmcMMO(plugin, mcmmoPlug);
		}
	}
	
	public void checkForPlugins() {
		PluginManager plugman = plugin.getServer().getPluginManager();
		
		// spout
		Plugin spoutPlugin = plugman.getPlugin("Spout");
		if (spoutPlugin != null) {
			plugin.getConfig().set("SPOUT_ENABLED", true);
			plugin.message.setSpout(true);
		}
		else {
			plugin.getConfig().set("SPOUT_ENABLED", false);
			plugin.message.setSpout(false);
		}
		
		// citizens
		Plugin citizensPlugin = plugman.getPlugin("Citizens");
		if (citizensPlugin != null)	plugin.getConfig().set("CITIZENS_ENABLED", true);
		else						plugin.getConfig().set("CITIZENS_ENABLED", false);
		
		// mcMMO
		Plugin mcmmoPlug = plugman.getPlugin("mcMMO");
		if (mcmmoPlug != null) plugin.darmcmmo = new DARmcMMO(plugin, mcmmoPlug);
	}
}
