package muCkk.DeathAndRebirth.otherPlugins;

import muCkk.DeathAndRebirth.DAR;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Perms {
	
	private static PermissionHandler permissionHandler;
	private static DAR plugin;
	
	public static void setup(DAR instance) {
		plugin = instance;
		
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
		// PEX
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")){
		    PermissionManager permissions = PermissionsEx.getPermissionManager();
		    
			if(perm.equalsIgnoreCase("dar.nodrop") || perm.equalsIgnoreCase("dar.ignore")) {
				if (permissions.has(player,"*") && !plugin.checkAdminPerms()) {
					return false;
				}
				if (permissions.has(player,"*") && plugin.checkAdminPerms()) {
					return true;
				}
			}
			
			
		    // Permission check
		    if(permissions.has(player, perm)){
		    	return true;
		    } else {
		    	return false;
		    }
		}
		
		// Permissions 3.1.6
		if (permissionHandler != null) {
			if(perm.equalsIgnoreCase("dar.nodrop") || perm.equalsIgnoreCase("dar.ignore")) {
				if (permissionHandler.has(player,"*") && !plugin.checkAdminPerms()) {
					return false;
				}
				if (permissionHandler.has(player,"*") && plugin.checkAdminPerms()) {
					return true;
				}
			}
			
			if (permissionHandler.has(player, perm)) {
				return true;
			}
		}
				
				
	// Bukkit build in system
		
		if (player.hasPermission(perm)) {
			return true;
		}	
	
		
	// OP system
		if(perm.equalsIgnoreCase("dar.admin") && player.isOp()) {
			return true;
		}
		
	// default
		return false;
	}
}
