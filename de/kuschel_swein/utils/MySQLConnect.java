package de.kuschel_swein.utils;

import de.kuschel_swein.main.Main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.*;

public class MySQLConnect {
	public static String HOST;
	public static String DATABASE;
	public static String USER;
	public static String PASSWORD;
	public static Integer PORT;
	private Connection con;

	public MySQLConnect(String host, String database, String user, String password, Integer port) {
		HOST = host;
		DATABASE = database;
		USER = user;
		PASSWORD = password;
		PORT = port;

		connect();
	}

	public void connect() {
		try {
			this.con = DriverManager.getConnection(
					"jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true", USER, PASSWORD);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(Main.Prefix + " §aVerbindung mit der MySQL Datenbank wurde erfolgreich hergestellt");
		} catch (SQLException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(
					Main.Prefix + " §cVerbindung mit der MySQL Datenbank ist fehlgeschlagen: " + e.getMessage());
		}
	}

	public void close() {
		try {
			if (this.con != null) {
				this.con.close();
			}
		} catch (SQLException sQLException) {
		}
	}

	public void update(String qry) {
		try {
			Statement st = this.con.createStatement();
			st.executeUpdate(qry);
			st.close();
		} catch (SQLException e) {
			connect();
			System.err.println(e);
		}
	}

	public ResultSet query(String qry) {
		ResultSet rs = null;

		try {
			Statement st = this.con.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException e) {
			connect();
			System.err.println(e);
		}
		return rs;
	}
}
