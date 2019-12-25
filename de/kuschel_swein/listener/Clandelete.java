package de.kuschel_swein.listener;

import de.kuschel_swein.main.Main;
import de.kuschel_swein.utils.Datamanager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Clandelete implements Listener {
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    if(e.getView().getTitle().equals("§c§lClan löschen") && e.getClickedInventory().getHolder() instanceof Player) {
    	e.setCancelled(true);
    }
    if(e.getView().getTitle().equals("§c§lClan löschen") && e.getClickedInventory().getHolder() instanceof Player) {
    	e.setCancelled(true);
    }
    
    if (e.getView().getTitle().equals("§c§lClan löschen") && e.getCurrentItem().getType() != null && e.getCurrentItem() != null) {
    	if(e.getCurrentItem().getType() == Material.LIME_STAINED_GLASS_PANE && e.getCurrentItem().getItemMeta().getDisplayName().equals("§2§lClan löschen")) {
    		p.closeInventory();
    		for(Player player : Bukkit.getServer().getOnlinePlayers()){
				if(Datamanager.isInIDClan(player.getUniqueId().toString(), Integer.valueOf(Datamanager.getClanID(p.getUniqueId().toString())))) {
					player.sendMessage("");
					player.sendMessage(Main.Clanchat + " §c§lDein Clan wurde gelöscht!");
					player.sendMessage("");
					player.setDisplayName(player.getDisplayName().toString().replace(Main.Clantag.replace("%tag%", Datamanager.getClanTag(player.getUniqueId().toString())), "").substring(3));
				}
			}
    		int clanid = Datamanager.getClanID(p.getUniqueId().toString());
    		Main.mysql.update("DELETE FROM `clans_members` WHERE clanid='" + clanid + "'");
    		Main.mysql.update("DELETE FROM `clans_invitations` WHERE clanid='" + clanid + "'");
    		Main.mysql.update("DELETE FROM `clans` WHERE id='" + clanid + "'");
    		p.sendMessage(Main.Prefix + "§7 Dein Clan wurde erfolgreich gelöscht.");
    	} else if(e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE && e.getCurrentItem().getItemMeta().getDisplayName().equals("§4§lAbbruch")) {
    		p.closeInventory();
    		p.sendMessage(Main.Prefix + "§7 Die Löschung wurde erfolgreich abgebrochen.");
    	} else {
    		e.setCancelled(true);
    	}
    }
  }
}  

