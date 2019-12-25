package de.kuschel_swein.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {
	public static ItemStack createItem(Material material, int anzahl, int subid, String displayname) {
		short neuesubid = (short) subid;
		ItemStack i = new ItemStack(material, anzahl, neuesubid);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName(displayname);
		i.setItemMeta(m);
		return i;
	}
}