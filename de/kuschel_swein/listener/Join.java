package de.kuschel_swein.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.kuschel_swein.main.Main;
import de.kuschel_swein.utils.Datamanager;

public class Join implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (Datamanager.isInClan(p.getUniqueId().toString())) {
			p.setDisplayName(Main.Clantag.replace("%tag%", Datamanager.getClanTag(p.getUniqueId().toString())) + "§r " + p.getDisplayName());
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (Datamanager.isInIDClan(player.getUniqueId().toString(),
						Integer.valueOf(Datamanager.getClanID(e.getPlayer().getUniqueId().toString())))) {
					player.sendMessage(Main.Clanchat + " §e§l" + p.getName() + " hat den Server betreten.");
				}
			}
		}

	}
}
