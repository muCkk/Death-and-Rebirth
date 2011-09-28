package muCkk.DeathAndRebirth.listener;

import muCkk.DeathAndRebirth.DAR;
import muCkk.DeathAndRebirth.config.CFG;
import muCkk.DeathAndRebirth.config.Config;
import muCkk.DeathAndRebirth.messages.Errors;
import muCkk.DeathAndRebirth.otherPlugins.DARConomy;
import muCkk.DeathAndRebirth.otherPlugins.DARmcMMO;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager; 

import com.nijikokun.register.payment.Methods;

public class SListener extends ServerListener{

	private DAR plugin;
	private Config config;
	
	public SListener(DAR plugin, Config config) {
		this.plugin = plugin;
		this.config = config;
	}
	
//	@Override
//    public void onPluginDisable(PluginDisableEvent event) {
//	}
	
	@Override
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
				config.set(CFG.SPOUT_ENABLED, true);
				plugin.message.setSpout(true);
			}
			else {
				plugin.message.setSpout(false);
				config.set(CFG.SPOUT_ENABLED, false);
				plugin.message.setSpout(false);
			}
		}
		// Citizens
		if(pluginName.equalsIgnoreCase("citizens")) {
			Plugin citizensPlugin = plugman.getPlugin("Citizens");
			if (citizensPlugin != null)	config.set(CFG.CITIZENS_ENABLED, true);
			else						config.set(CFG.CITIZENS_ENABLED, false);
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
			config.set(CFG.SPOUT_ENABLED, true);
			plugin.message.setSpout(true);
		}
		else {
			config.set(CFG.SPOUT_ENABLED, false);
			plugin.message.setSpout(false);
		}
		
		// citizens
		Plugin citizensPlugin = plugman.getPlugin("Citizens");
		if (citizensPlugin != null)	config.set(CFG.CITIZENS_ENABLED, true);
		else						config.set(CFG.CITIZENS_ENABLED, false);
		
		// mcMMO
		Plugin mcmmoPlug = plugman.getPlugin("mcMMO");
		if (mcmmoPlug != null) plugin.darmcmmo = new DARmcMMO(plugin, mcmmoPlug);
	}
}
