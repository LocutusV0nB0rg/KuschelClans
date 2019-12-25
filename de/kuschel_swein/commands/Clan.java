package de.kuschel_swein.commands;

import de.kuschel_swein.main.Main;
import de.kuschel_swein.utils.Datamanager;
import de.kuschel_swein.utils.ItemManager;

import org.bukkit.command.Command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Clan implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Main.Prefix + "§7 Der Clan Befehl kann nur von Spielern verwendet werden.");
			return false;
		}

		Player p = (Player) sender;
		if (p.hasPermission("kuschelclans.use")) {
			if (args.length == 0) {
				p.sendMessage("§8[]======[§e§lKuschelClans§8]======[]");
				p.sendMessage(" §8> §e/clan create §8| §eerstelle einen Clan");
				p.sendMessage(" §8> §e/clan invitations §8| §ealle Clan Einladungen anzeigen");
				p.sendMessage(" §8> §e/clan invite §8| §eSpieler einladen");
				p.sendMessage(" §8> §e/clan chat §8| §eInformation zum Clanchat");
				p.sendMessage(" §8> §e/clan rank §8| §eClanränge ändern");
				p.sendMessage(" §8> §e/clan kick §8| §eMitglieder rauswerfen");
				p.sendMessage(" §8> §e/clan leave §8| §eClan verlassen");
				p.sendMessage(" §8> §e/clan rankinfo §8| §eRanginformationen");
				p.sendMessage(" §8> §e/clan delete §8| §eClan löschen");
				p.sendMessage(" §8> §e/clan members §8| §eClan Mitglieder");
				p.sendMessage("§8[]======[§e§lKuschelClans§8]======[]");
			} else if (args[0].equalsIgnoreCase("create")) {
				if (!Datamanager.isInClan(p.getUniqueId().toString())) {
					if (args.length == 1) {
						p.sendMessage(Main.Prefix + "§7 Verwendung: /clan create <Name> <Tag>");
					} else if (args.length == 2) {
						p.sendMessage(Main.Prefix + "§7 Verwendung: /clan create " + args[1] + " <Tag>");
					} else {
						if (Datamanager.existsClanname(args[1])) {
							p.sendMessage(Main.Prefix + "§7 Dieser Clanname ist bereis vergeben!");
						} else {
							if (Datamanager.existsClantag(args[2])) {
								p.sendMessage(Main.Prefix + "§7 Dieser Clantag ist bereits vergeben!");
							} else {
								Main.mysql.update("INSERT INTO `clans`(`name`, `tag`, `creator`) VALUES ('"
										+ args[1].replace("§", "&") + "','" + args[2].replace("§", "&") + "','"
										+ p.getUniqueId() + "')");
								Main.mysql.update("INSERT INTO `clans_members`(`uuid`, `clanid`, `rank`) VALUES ('"
										+ p.getUniqueId() + "','"
										+ Datamanager.getClanIDbyCreator(p.getUniqueId().toString()) + "', '5')");
								p.sendMessage(Main.Prefix + "§7 Dein Clan wurde erfolgreich erstellt.");
								p.setDisplayName(Main.Clantag.replace("%tag%",
										Datamanager.getClanTag(p.getUniqueId().toString())) + "§r "
										+ p.getDisplayName());
							}
						}
					}
				} else {
					p.sendMessage(Main.Prefix + "§7 Du bist bereits in einem Clan!");
				}
			} else if (args[0].equalsIgnoreCase("invite")) {
				if (Datamanager.isInClan(p.getUniqueId().toString())) {
					if (args.length == 1) {
						p.sendMessage(Main.Prefix + "§7 Verwendung: /clan invite <Spieler>");
					} else {
						Player target = Bukkit.getPlayer(args[1]);
						if (target == null) {
							p.sendMessage(Main.Prefix + "§7 Verwendung: /clan invite <Spieler>");
						} else {
							if (Datamanager.getClanRank(p.getUniqueId().toString()) > 1) {
								if (Datamanager.existsInvitation(target.getUniqueId().toString(),
										Datamanager.getClanID(p.getUniqueId().toString()))) {
									p.sendMessage(Main.Prefix
											+ "§7 Dein Clan hat diesem Benutzer bereits eine Einladung gesendet.");
								} else {
									if (!Datamanager.isInClan(target.getUniqueId().toString())) {
										target.sendMessage(
												Main.Prefix + " §7Du wurdest von §l" + p.getName() + "§7 in den Clan §l"
														+ Datamanager.getClanName(p.getUniqueId().toString())
														+ "§7 eingeladen.");
										TextComponent tc = new TextComponent();
										tc.setText("§a§l[AKZEPTIEREN]");
										tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
												"/clan invitations accept "
														+ Datamanager.getClanID(p.getUniqueId().toString())));
										target.spigot().sendMessage(tc);
										TextComponent tc2 = new TextComponent();
										tc2.setText("§c§l[ABLEHNEN]");
										tc2.setClickEvent(
												new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan invitations deny "
														+ Datamanager.getClanID(p.getUniqueId().toString())));
										target.spigot().sendMessage(tc2);
										Main.mysql.update(
												"INSERT INTO `clans_invitations`(`user`, `clanid`, `state`) VALUES ('"
														+ target.getUniqueId().toString() + "','"
														+ Datamanager.getClanID(p.getUniqueId().toString())
														+ "', '0')");
										p.sendMessage(Main.Prefix + "§7 Du hast §l" + target.getName()
												+ "§7 erfolgreich in deinen Clan eingeladen.");
									} else {
										p.sendMessage(Main.Prefix + "§7 Dieser Benutzer ist bereits in einem Clan!");
									}
								}
							} else {
								p.sendMessage(Main.Prefix + "§r " + Main.NoPerms);
							}
						}
					}
				} else {
					p.sendMessage(Main.Prefix + "§7 Du bist in keinem Clan!");
				}
			} else if (args[0].equalsIgnoreCase("invitations")) {
				if (!Datamanager.isInClan(p.getUniqueId().toString())) {
					boolean exists = false;
					if (args.length == 1) {
						p.sendMessage("§8[]======[§e§lEinladungen§8]======[]");
						for (int i : Datamanager.getInvitations(p.getUniqueId().toString())) {
							p.sendMessage(" §8> §7§l" + Datamanager.getClanNameByID(i) + "");
							TextComponent tc = new TextComponent();
							tc.setText("§a§l[AKZEPTIEREN]");
							tc.setClickEvent(
									new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan invitations accept " + i));
							p.spigot().sendMessage(tc);
							TextComponent tc2 = new TextComponent();
							tc2.setText("§c§l[ABLEHNEN]");
							tc2.setClickEvent(
									new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan invitations deny " + i));
							p.spigot().sendMessage(tc2);
							exists = true;

						}
						if (!exists) {
							p.sendMessage("§cDu hast keine Einladungen.");
						}
						p.sendMessage("§8[]======[§e§lEinladungen§8]======[]");
					} else if (args[1].equalsIgnoreCase("accept")) {
						if (args.length == 2) {
							p.sendMessage(Main.Prefix + "§7 Verwendung: /clan invitations accept <Clan ID>");
						} else {
							if (Datamanager.existsInvitation(p.getUniqueId().toString(), Integer.valueOf(args[2]))) {
								Main.mysql.update("INSERT INTO `clans_members`(`uuid`, `clanid`, `rank`) VALUES ('"
										+ p.getUniqueId() + "','" + Integer.valueOf(args[2]) + "', '1')");
								Main.mysql.update("UPDATE `clans_invitations` SET `state`='1' WHERE user = '"
										+ p.getUniqueId().toString() + "' AND clanid = '" + Integer.valueOf(args[2])
										+ "'");
								Main.mysql.update("UPDATE `clans_invitations` SET `state`='2' WHERE user = '"
										+ p.getUniqueId().toString() + "' AND state = '0'");
								p.sendMessage(Main.Prefix + "§7 Du bist erfolgreich dem Clan §l"
										+ Datamanager.getClanNameByID(Integer.valueOf(args[2])) + "§7 beigetreten.");
								p.setDisplayName(Main.Clantag.replace("%tag%",
										Datamanager.getClanTag(p.getUniqueId().toString())) + "§r "
										+ p.getDisplayName());
								for (Player player : Bukkit.getServer().getOnlinePlayers()) {
									if (Datamanager.isInIDClan(player.getUniqueId().toString(),
											Integer.valueOf(args[2]))) {
										player.sendMessage(
												Main.Clanchat + " §a§l" + p.getName() + " ist dem Clan beigetreten.");
									}
								}
							} else {
								p.sendMessage(Main.Prefix + "§7 Verwendung: /clan invitations accept <Clan ID>");
							}
						}
					} else if (args[1].equalsIgnoreCase("deny")) {
						if (args.length == 2) {
							p.sendMessage(Main.Prefix + "§7 Verwendung: /clan invitations deny <Clan ID>");
						} else {
							if (Datamanager.existsInvitation(p.getUniqueId().toString(), Integer.valueOf(args[2]))) {
								Main.mysql.update("UPDATE `clans_invitations` SET `state`='2' WHERE user = '"
										+ p.getUniqueId().toString() + "' AND clanid = '" + Integer.valueOf(args[2])
										+ "'");
								p.sendMessage(Main.Prefix + "§7 Du hast erfolgreich die Einladung des Clans §l"
										+ Datamanager.getClanNameByID(Integer.valueOf(args[2])) + "§7 abgelehnt.");
								for (Player player : Bukkit.getServer().getOnlinePlayers()) {
									if (Datamanager.isInIDClan(player.getUniqueId().toString(),
											Integer.valueOf(args[2]))) {
										player.sendMessage(Main.Clanchat + " §c§l" + p.getName()
												+ " hat die Clan Einladung abgelehnt.");
									}
								}
							} else {
								p.sendMessage(Main.Prefix + "§7 Verwendung: /clan invitations deny <Clan ID>");
							}
						}
					} else {
						p.sendMessage(Main.Prefix + "§7 Verwendung: /clan invitations [<accept/deny>] [<Clan ID>]");
					}

				} else {
					p.sendMessage(Main.Prefix + "§7 Du bist bereits in einem Clan!");
				}
			} else if (args[0].equalsIgnoreCase("chat")) {
				p.sendMessage(Main.Prefix
						+ "§7 Du kannst eine Nachricht in den Clanchat senden indem du §l@clan §7vor deine Nachricht schreibst, alternativ kannst du auch §l@c §7verwenden.");
			} else if (args[0].equalsIgnoreCase("members")) {
				if (!Datamanager.isInClan(p.getUniqueId().toString())) {
					p.sendMessage(Main.Prefix + "§7 Du bist in keinem Clan!");
				} else {
					p.sendMessage("§8[]======[§e§lMitglieder§8]======[]");
					for (String member : Datamanager.getClanMembers(p.getUniqueId().toString())) {
						String rank = "§7§lMitglied";
						if (Datamanager.getClanRank(member) == 5) {
							rank = "§4§lGründer";
						} else if (Datamanager.getClanRank(member) == 4) {
							rank = "§c§lAdmin";
						} else if (Datamanager.getClanRank(member) == 3) {
							rank = "§2§lModerator";
						} else if (Datamanager.getClanRank(member) == 2) {
							rank = "§9§lSupporter";
						}
						String online = "";
						if (Datamanager.isOnline(member)) {
							online = " §a§l[§a§l✔§a§l]";
						}
						p.sendMessage(" §8> " + rank + " | "
								+ Bukkit.getOfflinePlayer(UUID.fromString(member)).getName() + online);
					}
					p.sendMessage("§8[]======[§e§lMitglieder§8]======[]");
				}
			} else if (args[0].equalsIgnoreCase("rank")) {
				if (!Datamanager.isInClan(p.getUniqueId().toString())) {
					p.sendMessage(Main.Prefix + "§7 Du bist in keinem Clan!");
				} else {
					if (args.length == 1) {
						p.sendMessage(Main.Prefix
								+ "§7 Verwendung: /clan rank <Spieler> <Mitglied/Supporter/Moderator/Admin>");
					} else {
						Player target = Bukkit.getPlayer(args[1]);
						if (target != null) {
							if (args.length == 2) {
								p.sendMessage(Main.Prefix + "§7 Verwendung: /clan rank " + args[1]
										+ " <Mitglied/Supporter/Moderator/Admin>");
							} else {
								if (Datamanager.getClanID(p.getUniqueId().toString()) == Datamanager
										.getClanID(target.getUniqueId().toString())) {
									if (Datamanager.getClanRank(p.getUniqueId().toString()) > 3) {
										if (Datamanager.getClanRank(target.getUniqueId().toString()) > 3
												&& Datamanager.getClanRank(p.getUniqueId().toString()) != 5) {
											p.sendMessage(Main.Prefix + "§r " + Main.NoPerms);
										} else {
											if (Datamanager.getClanRank(target.getUniqueId().toString()) == 5) {
												p.sendMessage(Main.Prefix
														+ "§7 Der Rang des Gründers kann nicht geändert werden.");
											} else {
												if (args[2].equalsIgnoreCase("Mitglied")) {
													Main.mysql
															.update("UPDATE `clans_members` SET `rank`='1' WHERE uuid='"
																	+ target.getUniqueId().toString() + "'");
													p.sendMessage(Main.Prefix + "§7 Du hast den Rang von §l"
															+ target.getName()
															+ " §7erfolgreich zu §lMitglied §7geändert.");
													for (Player player : Bukkit.getServer().getOnlinePlayers()) {
														if (Datamanager.isInIDClan(player.getUniqueId().toString(),
																Integer.valueOf(Datamanager
																		.getClanID(p.getUniqueId().toString())))) {
															player.sendMessage(Main.Clanchat + " §3§lDer Rang von "
																	+ target.getName() + " wurde von " + p.getName()
																	+ " zu §7§lMitglied §3§lgeändert.");
														}
													}
												} else if (args[2].equalsIgnoreCase("Supporter")) {
													Main.mysql
															.update("UPDATE `clans_members` SET `rank`='2' WHERE uuid='"
																	+ target.getUniqueId().toString() + "'");
													p.sendMessage(Main.Prefix + "§7 Du hast den Rang von §l"
															+ target.getName()
															+ " §7erfolgreich zu §9§lSupporter §7geändert.");
													for (Player player : Bukkit.getServer().getOnlinePlayers()) {
														if (Datamanager.isInIDClan(player.getUniqueId().toString(),
																Integer.valueOf(Datamanager
																		.getClanID(p.getUniqueId().toString())))) {
															player.sendMessage(Main.Clanchat + " §3§lDer Rang von "
																	+ target.getName() + " wurde von " + p.getName()
																	+ " zu §9§lSupporter §3§lgeändert.");
														}
													}
												} else if (args[2].equalsIgnoreCase("Moderator")) {
													Main.mysql
															.update("UPDATE `clans_members` SET `rank`='3' WHERE uuid='"
																	+ target.getUniqueId().toString() + "'");
													p.sendMessage(Main.Prefix + "§7 Du hast den Rang von §l"
															+ target.getName()
															+ " §7erfolgreich zu §2§lModerator §7geändert.");
													for (Player player : Bukkit.getServer().getOnlinePlayers()) {
														if (Datamanager.isInIDClan(player.getUniqueId().toString(),
																Integer.valueOf(Datamanager
																		.getClanID(p.getUniqueId().toString())))) {
															player.sendMessage(Main.Clanchat + " §3§lDer Rang von "
																	+ target.getName() + " wurde von " + p.getName()
																	+ " zu §2§lModerator §3§lgeändert.");
														}
													}
												} else if (args[2].equalsIgnoreCase("Admin")) {
													if (Datamanager.getClanRank(p.getUniqueId().toString()) != 5) {
														p.sendMessage(Main.Prefix + "§r " + Main.NoPerms);
													} else {
														Main.mysql.update(
																"UPDATE `clans_members` SET `rank`='4' WHERE uuid='"
																		+ target.getUniqueId().toString() + "'");
														p.sendMessage(Main.Prefix + "§7 Du hast den Rang von §l"
																+ target.getName()
																+ " §7erfolgreich zu §c§lAdmin §7geändert.");
														for (Player player : Bukkit.getServer().getOnlinePlayers()) {
															if (Datamanager.isInIDClan(player.getUniqueId().toString(),
																	Integer.valueOf(Datamanager
																			.getClanID(p.getUniqueId().toString())))) {
																player.sendMessage(Main.Clanchat + " §3§lDer Rang von "
																		+ target.getName() + " wurde von " + p.getName()
																		+ " zu §c§lAdmin §3§lgeändert.");
															}
														}
													}
												} else {
													p.sendMessage(Main.Prefix + "§7 Verwendung: /clan rank " + args[1]
															+ " <Mitglied/Supporter/Moderator/Admin>");
												}
											}
										}
									} else {
										p.sendMessage(Main.Prefix + "§r " + Main.NoPerms);
									}
								} else {
									p.sendMessage(Main.Prefix + "§7 Dieser Spieler ist nicht in deinem Clan!");
								}
							}
						} else {
							p.sendMessage(Main.Prefix
									+ "§7 Verwendung: /clan rank <Spieler> <Mitglied/Supporter/Moderator/Admin>");
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("kick")) {
				if (!Datamanager.isInClan(p.getUniqueId().toString())) {
					p.sendMessage(Main.Prefix + "§7 Du bist in keinem Clan!");
				} else {
					if (args.length == 1) {
						p.sendMessage(Main.Prefix + "§7 Verwendung: /clan kick <Spieler>");
					} else {
						Player target = Bukkit.getPlayer(args[1]);
						if (target == null) {
							p.sendMessage(Main.Prefix + "§7 Verwendung: /clan kick <Spieler>");
						} else {
							if (Datamanager.getClanRank(p.getUniqueId().toString()) > 2) {
								if (Datamanager.getClanRank(p.getUniqueId().toString()) > Datamanager
										.getClanRank(target.getUniqueId().toString())) {
									target.setDisplayName(
											target.getDisplayName().toString()
													.replace(Main.Clantag.replace("%tag%",
															Datamanager.getClanTag(target.getUniqueId().toString())),
															"")
													.substring(3));
									p.sendMessage(Main.Prefix + "§7 Du hast §l" + target.getName()
											+ " §7erfolgreich aus dem Clan geworfen.");
									target.sendMessage(Main.Prefix + "§7 Du wurdest von §l" + p.getName()
											+ " §7aus dem Clan geworfen.");
									Main.mysql.update("DELETE FROM `clans_members` WHERE uuid='"
											+ target.getUniqueId().toString() + "'");
									for (Player player : Bukkit.getServer().getOnlinePlayers()) {
										if (Datamanager.isInIDClan(player.getUniqueId().toString(),
												Integer.valueOf(Datamanager.getClanID(p.getUniqueId().toString())))) {
											player.sendMessage(Main.Clanchat + " §c§l" + target.getName()
													+ " wurde von " + p.getName() + " aus dem Clan geworfen.");
										}
									}
								} else {
									p.sendMessage(Main.Prefix + "§r " + Main.NoPerms);
								}
							} else {
								p.sendMessage(Main.Prefix + "§r " + Main.NoPerms);
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("leave")) {
				if (!Datamanager.isInClan(p.getUniqueId().toString())) {
					p.sendMessage(Main.Prefix + "§7 Du bist in keinem Clan!");
				} else {
					if (Datamanager.getClanRank(p.getUniqueId().toString()) != 5) {
						p.setDisplayName(p.getDisplayName().toString().replace(
								Main.Clantag.replace("%tag%", Datamanager.getClanTag(p.getUniqueId().toString())), "")
								.substring(3));
						Main.mysql
								.update("DELETE FROM `clans_members` WHERE uuid='" + p.getUniqueId().toString() + "'");
						p.sendMessage(Main.Prefix + "§7 Du hast den Clan erfolgreich verlassen.");
						for (Player player : Bukkit.getServer().getOnlinePlayers()) {
							if (Datamanager.isInIDClan(player.getUniqueId().toString(),
									Integer.valueOf(Datamanager.getClanID(p.getUniqueId().toString())))) {
								player.sendMessage(Main.Clanchat + " §c§l" + p.getName() + " hat den Clan verlassen.");
							}
						}
					} else {
						p.sendMessage(Main.Prefix + "§7 Der Gründer kann den Clan nicht verlassen.");
					}
				}
			} else if (args[0].equalsIgnoreCase("rankinfo")) {
				if (args.length == 1) {
					p.sendMessage(
							Main.Prefix + "§7 Verwendung: /clan rankinfo <Mitglied/Supporter/Moderator/Admin/Gründer>");
					if (Datamanager.isInClan(p.getUniqueId().toString())) {
						int rank = Datamanager.getClanRank(p.getUniqueId().toString());
						String ranktext = "§7§lMitglied";
						if (rank == 1) {
							ranktext = "§7§lMitglied";
						} else if (rank == 2) {
							ranktext = "§9§lSupporter";
						} else if (rank == 3) {
							ranktext = "§2§lModerator";
						} else if (rank == 4) {
							ranktext = "§c§lAdmin";
						} else if (rank == 5) {
							ranktext = "§4§lGründer";
						}
						p.sendMessage(Main.Prefix + " §7Du hast derzeit den " + ranktext + " §7Rang.");
					}
				} else if (args[1].equalsIgnoreCase("Mitglied")) {
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
					p.sendMessage("§7§lMitglied");
					p.sendMessage("");
					p.sendMessage(" §8[§a✔§8] §7Clanchat");
					p.sendMessage(" §8[§c✘§8] §7Einladungen");
					p.sendMessage(" §8[§c✘§8] §7Mitglieder rauswerfen");
					p.sendMessage(" §8[§c✘§8] §7Ränge bearbeiten");
					p.sendMessage(" §8[§c✘§8] §7Admins verwalten");
					p.sendMessage(" §8[§c✘§8] §7Clan löschen");
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
				} else if (args[1].equalsIgnoreCase("Supporter")) {
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
					p.sendMessage("§9§lSupporter");
					p.sendMessage("");
					p.sendMessage(" §8[§a✔§8] §7Clanchat");
					p.sendMessage(" §8[§a✔§8] §7Einladungen");
					p.sendMessage(" §8[§c✘§8] §7Mitglieder rauswerfen");
					p.sendMessage(" §8[§c✘§8] §7Ränge bearbeiten");
					p.sendMessage(" §8[§c✘§8] §7Admins verwalten");
					p.sendMessage(" §8[§c✘§8] §7Clan löschen");
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
				} else if (args[1].equalsIgnoreCase("Moderator")) {
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
					p.sendMessage("§2§lModerator");
					p.sendMessage("");
					p.sendMessage(" §8[§a✔§8] §7Clanchat");
					p.sendMessage(" §8[§a✔§8] §7Einladungen");
					p.sendMessage(" §8[§a✔§8] §7Mitglieder rauswerfen");
					p.sendMessage(" §8[§c✘§8] §7Ränge bearbeiten");
					p.sendMessage(" §8[§c✘§8] §7Admins verwalten");
					p.sendMessage(" §8[§c✘§8] §7Clan löschen");
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
				} else if (args[1].equalsIgnoreCase("Admin")) {
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
					p.sendMessage("§c§lAdmin");
					p.sendMessage("");
					p.sendMessage(" §8[§a✔§8] §7Clanchat");
					p.sendMessage(" §8[§a✔§8] §7Einladungen");
					p.sendMessage(" §8[§a✔§8] §7Mitglieder rauswerfen");
					p.sendMessage(" §8[§a✔§8] §7Ränge bearbeiten");
					p.sendMessage(" §8[§c✘§8] §7Admins verwalten");
					p.sendMessage(" §8[§c✘§8] §7Clan löschen");
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
				} else if (args[1].equalsIgnoreCase("Gründer")) {
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
					p.sendMessage("§4§lGründer");
					p.sendMessage("");
					p.sendMessage(" §8[§a✔§8] §7Clanchat");
					p.sendMessage(" §8[§a✔§8] §7Einladungen");
					p.sendMessage(" §8[§a✔§8] §7Mitglieder rauswerfen");
					p.sendMessage(" §8[§a✔§8] §7Ränge bearbeiten");
					p.sendMessage(" §8[§a✔§8] §7Admins verwalten");
					p.sendMessage(" §8[§a✔§8] §7Clan löschen");
					p.sendMessage("§8[]======[§e§lRänge§8]======[]");
				} else {
					p.sendMessage(
							Main.Prefix + "§7 Verwendung: /clan rankinfo <Mitglied/Supporter/Moderator/Admin/Gründer>");
					if (Datamanager.isInClan(p.getUniqueId().toString())) {
						int rank = Datamanager.getClanRank(p.getUniqueId().toString());
						String ranktext = "§7§lMitglied";
						if (rank == 1) {
							ranktext = "§7§lMitglied";
						} else if (rank == 2) {
							ranktext = "§9§lSupporter";
						} else if (rank == 3) {
							ranktext = "§2§lModerator";
						} else if (rank == 4) {
							ranktext = "§c§lAdmin";
						} else if (rank == 5) {
							ranktext = "§4§lGründer";
						}
						p.sendMessage(Main.Prefix + " §7Du hast derzeit den " + ranktext + " §7Rang.");
					}
				}

			} else if (args[0].equalsIgnoreCase("delete")) {
				if (!Datamanager.isInClan(p.getUniqueId().toString())) {
					p.sendMessage(Main.Prefix + "§7 Du bist in keinem Clan!");
				} else {
					if (Datamanager.getClanRank(p.getUniqueId().toString()) == 5) {
						Inventory inv = Bukkit.createInventory((InventoryHolder) p, 27, "§c§lClan löschen");
						inv.setItem(11, ItemManager.createItem(Material.RED_STAINED_GLASS_PANE, 1, 0, "§4§lAbbruch"));
						inv.setItem(15,
								ItemManager.createItem(Material.LIME_STAINED_GLASS_PANE, 1, 0, "§2§lClan löschen"));
						p.openInventory(inv);
					} else {
						p.sendMessage(Main.Prefix + "§r " + Main.NoPerms);
					}
				}
			}

		} else {
			p.sendMessage(Main.Prefix + "§r " + Main.NoPerms);
		}

		return false;
	}
}
