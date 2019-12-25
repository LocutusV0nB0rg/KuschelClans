package de.kuschel_swein.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.kuschel_swein.main.Main;
import de.kuschel_swein.utils.Datamanager;

public class Quit implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (Datamanager.isInClan(p.getUniqueId().toString())) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (Datamanager.isInIDClan(player.getUniqueId().toString(),
						Integer.valueOf(Datamanager.getClanID(e.getPlayer().getUniqueId().toString())))) {
					player.sendMessage(Main.Clanchat + " §e§l" + p.getName() + " hat den Server verlassen.");
				}
			}
		}

	}
}
