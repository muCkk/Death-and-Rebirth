package muCkk.DeathAndRebirth.tools;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DARArmor implements ConfigurationSerializable {
	
	private ItemStack[] armor;

	public DARArmor(PlayerInventory inv) {
		this.armor = inv.getArmorContents();
	}
	
	public ItemStack[] getArmor() {
		return armor;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		for (ItemStack item : armor) {
			if(item == null) continue;
			map.put(Integer.toString(item.getTypeId()), item);
		}
		
		return map;
	}
}
