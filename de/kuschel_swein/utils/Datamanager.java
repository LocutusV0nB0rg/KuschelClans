package de.kuschel_swein.utils;

import de.kuschel_swein.main.Main;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Datamanager {
	public static boolean isInClan(String UUID) {
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans_members WHERE uuid='" + UUID + "'");
			if (rs.next()) {
				if (rs.getString("uuid") == null) {
					return false;
				}
				return true;
			}
		} catch (SQLException sQLException) {
		}

		return false;
	}

	public static boolean isInIDClan(String UUID, int id) {
		try {
			ResultSet rs = Main.mysql
					.query("SELECT * FROM clans_members WHERE uuid='" + UUID + "' AND clanid='" + id + "'");
			if (rs.next()) {
				if (Integer.valueOf(rs.getInt("clanid")) == id) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException sQLException) {
		}

		return false;
	}

	public static int getClanIDbyCreator(String UUID) {
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans WHERE creator='" + UUID + "'");
			if (rs.next()) {
				return Integer.valueOf(rs.getInt("id"));
			}
		} catch (SQLException sQLException) {
		}
		return 0;
	}

	public static boolean existsClanname(String name) {
		try {
			ResultSet rs = Main.mysql.query("SELECT count(name) FROM clans AS cname WHERE name='" + name + "'");
			if (rs.next()) {
				if (Integer.valueOf(rs.getInt("cname")) != 0) {
					return false;
				} else {
					return true;
				}
			}
		} catch (SQLException sQLException) {
		}
		return false;
	}

	public static boolean existsClantag(String name) {
		try {
			ResultSet rs = Main.mysql.query("SELECT count(tag) FROM clans AS ctag WHERE tag='" + name + "'");
			if (rs.next()) {
				if (Integer.valueOf(rs.getInt("ctag")) != 0) {
					return false;
				} else {
					return true;
				}
			}
		} catch (SQLException sQLException) {
		}
		return false;
	}

	public static boolean existsInvitation(String UUID, int clanid) {
		try {
			ResultSet rs = Main.mysql.query("SELECT count(clanid) FROM clans_invitations WHERE user='" + UUID
					+ "' AND state='0' AND clanid ='" + clanid + "'");
			if (rs.next()) {
				if (Integer.valueOf(rs.getInt("count(clanid)")) < 1) {
					return false;
				} else {
					return true;
				}
			}
		} catch (SQLException sQLException) {
		}
		return false;
	}

	public static int getClanRank(String UUID) {
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans_members WHERE uuid='" + UUID + "'");
			if (rs.next()) {
				return Integer.valueOf(rs.getInt("rank"));
			}
		} catch (SQLException sQLException) {
		}
		return 0;
	}

	public static ArrayList<Integer> getInvitationIDs() {
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans_invitations");
			ArrayList<Integer> ids = new ArrayList<>();
			while (rs.next()) {
				ids.add(rs.getInt("id"));
			}
			return ids;
		} catch (SQLException sQLException) {

			return null;
		}
	}

	public static int getClanID(String UUID) {
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans_members WHERE uuid='" + UUID + "'");
			if (rs.next()) {
				return Integer.valueOf(rs.getInt("clanid"));
			}
		} catch (SQLException sQLException) {
		}
		return 0;
	}

	public static String getClanTag(String UUID) {
		int clanID = getClanID(UUID);
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans WHERE id='" + clanID + "'");
			if (rs.next()) {
				return rs.getString("tag");
			}
		} catch (SQLException sQLException) {
		}
		return null;
	}

	public static String getClanName(String UUID) {
		int clanID = getClanID(UUID);
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans WHERE id='" + clanID + "'");
			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (SQLException sQLException) {
		}
		return null;
	}

	public static String getClanNameByID(int id) {
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans WHERE id='" + id + "'");
			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (SQLException sQLException) {
		}
		return null;
	}

	public static ArrayList<String> getClanMembers(String UUID) {
		int clanid = getClanID(UUID);
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans_members WHERE clanid = '" + clanid + "'");
			ArrayList<String> members = new ArrayList<>();
			while (rs.next()) {
				members.add(rs.getString("uuid"));
			}
			return members;
		} catch (SQLException sQLException) {

			return null;
		}
	}

	public static ArrayList<Integer> getInvitations(String UUID) {
		try {
			ResultSet rs = Main.mysql.query("SELECT * FROM clans_invitations WHERE user = '" + UUID + "' AND state='0'");
			ArrayList<Integer> inv = new ArrayList<>();
			while (rs.next()) {
				inv.add(Integer.valueOf(rs.getInt("clanid")));
			}
			return inv;
		} catch (SQLException sQLException) {

			return null;
		}
	}

	public static boolean isOnline(String uuid) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p.getUniqueId().toString().equals(uuid))
				return true;
		}
		return false;
	}
}
