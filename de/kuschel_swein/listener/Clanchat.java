package de.kuschel_swein.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.kuschel_swein.main.Main;
import de.kuschel_swein.utils.Datamanager;

public class Clanchat implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		String msg = e.getMessage();
		if (Datamanager.isInClan(e.getPlayer().getUniqueId().toString())) {
			if (msg.startsWith("@clan") || msg.startsWith("@c")) {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (Datamanager.isInIDClan(player.getUniqueId().toString(),
							Integer.valueOf(Datamanager.getClanID(e.getPlayer().getUniqueId().toString())))) {
						String pname = null;
						int rank = Datamanager.getClanRank(e.getPlayer().getUniqueId().toString());
						if (rank == 1) {
							pname = "§7§lMitglied | " + e.getPlayer().getName();
						} else if (rank == 2) {
							pname = "§9§lSupporter | " + e.getPlayer().getName();
						} else if (rank == 3) {
							pname = "§2§lModerator | " + e.getPlayer().getName();
						} else if (rank == 4) {
							pname = "§c§lAdmin | " + e.getPlayer().getName();
						}
						if (rank == 5) {
							pname = "§4§lGründer | " + e.getPlayer().getName();
						}
						player.sendMessage(Main.Clanchat + " " + pname + " §8>§7" + msg.replace("@clan", "").replace("@c", ""));
					}
				}
				e.setCancelled(true);
			}

		}
	}
}
