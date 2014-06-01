package net.mcshockwave.scatter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public enum ConfigFile {

	Messages(
		"messages.yml");

	public String				name;

	private FileConfiguration	config	= null;
	private File				file	= null;

	private ConfigFile(String name) {
		this.name = name;

		saveDefaults();
	}

	public void reload() {
		if (file == null) {
			file = new File(InstaScatter.ins.getDataFolder(), name);
		}
		config = YamlConfiguration.loadConfiguration(file);

		// Look for defaults in the jar
		InputStream defConfigStream = InstaScatter.ins.getResource(name);
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}

	public FileConfiguration get() {
		if (config == null) {
			reload();
		}
		return config;
	}

	public void update() {
		save();
		reload();
	}

	public void save() {
		if (config == null || file == null) {
			return;
		}
		try {
			get().save(file);
		} catch (IOException ex) {
			InstaScatter.ins.getLogger().log(Level.SEVERE, "Could not save config to " + file, ex);
		}
	}

	public void saveDefaults() {
		if (file == null) {
			file = new File(InstaScatter.ins.getDataFolder(), name);
		}
		if (!file.exists()) {
			InstaScatter.ins.saveResource(name, false);
		}
	}

}
