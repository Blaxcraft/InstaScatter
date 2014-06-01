package net.mcshockwave.scatter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class InstaScatter extends JavaPlugin {

	public static ChatColor		textcolor	= ChatColor.WHITE;
	public static String		prefix		= "§7[§cInstaScatter§7] " + textcolor;
	public static InstaScatter	ins;

	public void onEnable() {
		ins = this;
		Bukkit.getPluginManager().registerEvents(new DefaultListener(), this);

		saveDefaultConfig();

		for (ConfigFile file : ConfigFile.values()) {
			file.saveDefaults();
		}

		textcolor = ChatColor.getByChar(getConfig().getString("text_color_id"));
		prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix")) + " " + textcolor;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender.isOp()) {

			if (args.length == 0) {
				return false;
			}

			if (label.equalsIgnoreCase("spread")) {
				String rad = args[0];

				if (isInteger(rad)) {
					int radius = Integer.parseInt(rad);
					World w = null;
					if (args.length > 1) {
						w = Bukkit.getWorld(args[1]);
					} else if (sender instanceof Player) {
						w = ((Player) sender).getWorld();
					}

					if (w != null) {
						ScatterManager.spreadPlayers(w, radius);
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else if (label.equalsIgnoreCase("spreadplayer")) {
				if (args.length == 1) {
					return false;
				}

				String player = args[0];
				if (Bukkit.getPlayer(player) == null) {
					return false;
				}
				Player pl = Bukkit.getPlayer(player);

				String rad = args[1];

				if (isInteger(rad)) {
					int radius = Integer.parseInt(rad);
					World w = null;
					if (args.length > 2) {
						w = Bukkit.getWorld(args[2]);
					} else {
						w = pl.getWorld();
					}

					if (w != null) {
						ScatterManager.teleportPlayer(pl, radius, w, true);
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		} else {
			sender.sendMessage("§cYou are not op!");
		}

		return true;
	}

	public boolean isInteger(String check) {
		try {
			Integer.parseInt(check);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static String getFormattedString(String input, String... vals) {
		for (int i = 0; i < vals.length; i += 2) {
			input = input.replace("%" + vals[i] + "%", vals[i + 1]);
		}

		return ChatColor.translateAlternateColorCodes('&', input);
	}

}
