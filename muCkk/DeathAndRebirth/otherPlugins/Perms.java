package muCkk.DeathAndRebirth.otherPlugins;

import muCkk.DeathAndRebirth.DAR;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Perms {
	
	private static PermissionHandler permissionHandler;
	
	public static void setup(DAR plugin) {		
	    if (permissionHandler != null) {
	        return;
	    }
	    
	    Plugin permissionsPlugin = plugin.getServer().getPluginManager().getPlugin("Permissions");
	    
	    if (permissionsPlugin == null) {
	        return;
	    }
	    
	    permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	    return;
	}
	
	public static boolean hasPermission(Player player, String perm) {
	// Bukkit build in system
		if (player.hasPermission(perm)) {
			return true;
		}
		
	// Permissions 3.1.6
		if (permissionHandler != null) {
			if (permissionHandler.has(player, perm)) {
				return true;
			}
		}
		
	// OP system
		if(perm.equalsIgnoreCase("dar.admin") && player.isOp()) {
			return true;
		}
		
	// default
		return false;
	}
}
