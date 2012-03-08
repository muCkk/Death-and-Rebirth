package muCkk.DeathAndRebirth.tools;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DARInventory implements ConfigurationSerializable {

	
	private ItemStack[] contents;

	public DARInventory(PlayerInventory inv) {
		this.contents = inv.getContents();
	}
	
	public DARInventory(ItemStack[] items) {
		this.contents = items;
	}

	public ItemStack[] getContents() {
		return contents;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		for (ItemStack item : contents) {
			if(item == null) continue;
			map.put(Integer.toString(item.getTypeId()), item);
		}
		
		return map;
	}
}
