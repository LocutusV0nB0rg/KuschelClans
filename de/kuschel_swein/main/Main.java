package de.kuschel_swein.main;

import de.kuschel_swein.commands.Clan;
import de.kuschel_swein.listener.Clanchat;
import de.kuschel_swein.listener.Join;
import de.kuschel_swein.listener.Quit;
import de.kuschel_swein.listener.Clandelete;
import de.kuschel_swein.utils.MySQLConnect;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public static MySQLConnect mysql;

	public static Main main;

	public static String Prefix = "§8[§e§lKuschelClans§8]";

	public static String Clanchat = "§8[§e§ClanChat§8]";

	public static String NoPerms = "§cDazu hast du keine Rechte!";

	public static String Clantag = "§6[%tag%]";

	public static String Version = "1.0";

	public void onEnable() {
		main = this;
		Config();
		Commands();
		Listener();
		MySQL();
	}

	private void Config() {
		final File ordner = new File(getDataFolder().getPath());
		if (!ordner.exists())
			ordner.mkdirs();
		final File config = new File(getDataFolder().getPath(), "config.yml");
		final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
		if (!config.exists()) {
			try {
				config.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		} else {
			Prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("Prefix"));
			NoPerms = ChatColor.translateAlternateColorCodes('&', cfg.getString("NoPerms"));
			Clanchat = ChatColor.translateAlternateColorCodes('&', cfg.getString("Clanchat"));
			Clantag = ChatColor.translateAlternateColorCodes('&', cfg.getString("Clantag"));
			sendInitMessage();
		}
		cfg.options().copyDefaults(true);
		cfg.addDefault("Prefix", "&8[&e&lKuschelClans&8]");
		cfg.addDefault("NoPerms", "&cDazu hast du keine Rechte!");
		cfg.addDefault("Clanchat", "&8[&e&lClanchat&8]");
		cfg.addDefault("Clantag", "&6[%tag%]");
		try {
			cfg.save(config);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		final File mysql = new File(getDataFolder().getPath(), "mysql.yml");
		final YamlConfiguration mysqlcfg = YamlConfiguration.loadConfiguration(mysql);
		if (!mysql.exists()) {
			try {
				mysql.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			mysqlcfg.options().copyDefaults(true);
			mysqlcfg.addDefault("host", "localhost");
			mysqlcfg.addDefault("database", "KuschelClans");
			mysqlcfg.addDefault("user", "root");
			mysqlcfg.addDefault("password", "");
			mysqlcfg.addDefault("port", Integer.valueOf(3306));
			try {
				mysqlcfg.save(mysql);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void Commands() {
		getCommand("clan").setExecutor((CommandExecutor) new Clan());
	}

	private void Listener() {
		Bukkit.getPluginManager().registerEvents((Listener) new Clanchat(), (Plugin) this);
		Bukkit.getPluginManager().registerEvents((Listener) new Join(), (Plugin) this);
		Bukkit.getPluginManager().registerEvents((Listener) new Clandelete(), (Plugin) this);
		Bukkit.getPluginManager().registerEvents((Listener) new Quit(), (Plugin) this);
	}

	private void MySQL() {
		File file = new File(getDataFolder().getPath(), "mysql.yml");
		YamlConfiguration mysql = YamlConfiguration.loadConfiguration(file);
		MySQLConnect.HOST = mysql.getString("host");
		MySQLConnect.DATABASE = mysql.getString("database");
		MySQLConnect.USER = mysql.getString("user");
		MySQLConnect.PASSWORD = mysql.getString("password");
		if (mysql.getInt("port") != 0) {
			MySQLConnect.PORT = Integer.valueOf(mysql.getInt("port"));
		} else {
			MySQLConnect.PORT = Integer.valueOf(3306);
		}
		Main.mysql = new MySQLConnect(MySQLConnect.HOST, MySQLConnect.DATABASE, MySQLConnect.USER,
				MySQLConnect.PASSWORD, MySQLConnect.PORT);
		Database();
	}

	private void Database() {
		Main.mysql.update(
				"CREATE TABLE IF NOT EXISTS `clans` ( `id` int(11) NOT NULL, `name` varchar(255) NOT NULL, `tag` varchar(255) DEFAULT NULL, `creator` varchar(255) DEFAULT NULL)");
		Main.mysql.update(
				"CREATE TABLE IF NOT EXISTS `clans_invitations` (`id` int(11) NOT NULL,`user` varchar(255) NOT NULL,`clanid` int(11) DEFAULT NULL,`state` int(11) DEFAULT NULL)");
		Main.mysql.update(
				"CREATE TABLE IF NOT EXISTS `clans_members` (`id` int(11) NOT NULL,`uuid` varchar(255) NOT NULL,`clanid` int(11) DEFAULT NULL,`rank` int(2) NOT NULL)");
		Main.mysql.update("ALTER TABLE `clans` ADD PRIMARY KEY (`id`);");
		Main.mysql.update("ALTER TABLE `clans_invitations` ADD PRIMARY KEY (`id`);");
		Main.mysql.update("ALTER TABLE `clans_members` ADD PRIMARY KEY (`id`);");
		Main.mysql.update("ALTER TABLE `clans` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT");
		Main.mysql.update("ALTER TABLE `clans_invitations` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT");
		Main.mysql.update("ALTER TABLE `clans_members` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT");
	}

	private void sendInitMessage() {
		Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
		Bukkit.getConsoleSender().sendMessage("§e§lKuschelClans §8| §7Version: §c" + Version);
		Bukkit.getConsoleSender().sendMessage("§eDeveloper: §e§lKuschel_Swein");
		Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
	}
}