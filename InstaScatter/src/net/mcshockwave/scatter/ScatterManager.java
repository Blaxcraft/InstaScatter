package net.mcshockwave.scatter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Random;

public class ScatterManager {

	public static Scoreboard	score	= Bukkit.getScoreboardManager().getMainScoreboard();

	public static Random		rand	= new Random();

	public static ArrayList<Player> getScatterPlayers() {
		ArrayList<Player> ret = new ArrayList<>();

		for (Player p : Bukkit.getOnlinePlayers()) {
			ret.add(p);
		}

		return ret;
	}

	public static void spreadPlayers(final World world, final int spreadDistance) {
		final int count = ScatterManager.getScatterPlayers().size();

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), Sound.EXPLODE, 10, 1.5f);
		}

		Bukkit.broadcastMessage("§7-----------------------------------------------------");
		Bukkit.broadcastMessage(InstaScatter.prefix
				+ InstaScatter.getFormattedString(ConfigFile.Messages.get().getString("initial_scatter_message"),
						"players", "§c" + count + InstaScatter.textcolor + " player" + (count == 1 ? "" : "s"),
						"radius", "§a" + spreadDistance + InstaScatter.textcolor, "world", "§e" + world.getName()
								+ InstaScatter.textcolor));
		Bukkit.broadcastMessage("§7-----------------------------------------------------");

		Bukkit.getScheduler().runTaskLater(InstaScatter.ins, new Runnable() {
			public void run() {
				Bukkit.broadcastMessage(InstaScatter.prefix + "Scattering in 3...");
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, 0.5f);
				}

				Bukkit.getScheduler().runTaskLater(InstaScatter.ins, new Runnable() {
					public void run() {
						Bukkit.broadcastMessage(InstaScatter.prefix + "Scattering in 2...");
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, 1f);
						}

						Bukkit.getScheduler().runTaskLater(InstaScatter.ins, new Runnable() {
							public void run() {
								Bukkit.broadcastMessage(InstaScatter.prefix + "Scattering in 1...");
								for (Player p : Bukkit.getOnlinePlayers()) {
									p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, 1.5f);
								}

								Bukkit.getScheduler().runTaskLater(InstaScatter.ins, new Runnable() {
									public void run() {
										final long start = System.currentTimeMillis();

										spread(world, spreadDistance, count);

										Bukkit.getScheduler().runTaskLater(InstaScatter.ins, new Runnable() {
											public void run() {
												if (InstaScatter.ins.getConfig().getBoolean("unstick_players")) {
													Bukkit.broadcastMessage(InstaScatter.prefix
															+ "Unsticking players...");
													for (Player p : world.getPlayers()) {
														p.teleport(p.getLocation().add(0, 1, 0));
													}
												}

												long time = System.currentTimeMillis() - start;
												Bukkit.broadcastMessage(InstaScatter.prefix
														+ InstaScatter.getFormattedString(ConfigFile.Messages.get()
																.getString("finished_scattering"), "time", "§a"
																+ (time / (double) 1000) + InstaScatter.textcolor
																+ " seconds", "players", "§c" + count
																+ InstaScatter.textcolor + " player"
																+ (count == 1 ? "" : "s")));

												for (Player p : Bukkit.getOnlinePlayers()) {
													p.playSound(p.getLocation(), Sound.FIREWORK_LARGE_BLAST2, 10, 1);
												}
											}
										}, 8l);
									}
								}, 20);
							}
						}, 20);
					}
				}, 20);
			}
		}, 30);
	}

	public static Material[]		nospawn	= { Material.STATIONARY_WATER, Material.WATER, Material.STATIONARY_LAVA,
			Material.LAVA, Material.CACTUS	};

	public static ArrayList<Player>	spread	= new ArrayList<Player>();

	public static void spread(World world, int spreadDistance, final int count) {
		ScatterLocation.locations.clear();

		if (spreadDistance <= -1) {
			spreadDistance = 1000;
		}
		int scattered = 0;
		ArrayList<Player> spread = new ArrayList<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			teleportPlayer(p, spreadDistance, world, false);
			scattered++;

			if (scattered % InstaScatter.ins.getConfig().getInt("scatter_broadcast_interval") == 0) {
				Bukkit.broadcastMessage(InstaScatter.prefix + "Scattered: §7[ §e§l" + scattered + " §7/§e§l " + count
						+ " §7]");

				for (Player p2 : Bukkit.getOnlinePlayers()) {
					p2.playSound(p2.getLocation(), Sound.NOTE_PLING, 10, 1f);
				}
			}
		}
		spread.clear();
	}

	public static Location teleportPlayer(Player p, int spreadDistance, World world, boolean single) {
		boolean goodSpawn = false;
		if (!single) {
			for (Player p2 : spread) {
				Team t = score.getPlayerTeam(p);
				Team t2 = score.getPlayerTeam(p2);
				if (t != null && t2 != null && t == t2) {
					p.teleport(p2);
					goodSpawn = true;
				}
			}
		}
		int tries = 0;
		while (!goodSpawn) {
			tries++;

			int x = rand.nextInt(spreadDistance) - rand.nextInt(spreadDistance);
			int z = rand.nextInt(spreadDistance) - rand.nextInt(spreadDistance);
			int y = world.getHighestBlockYAt(x, z);
			Location l = new Location(world, x, y, z);
			Material m = l.add(0, -1, 0).getBlock().getType();
			boolean noHazard = true;
			int minRadPlayers = (spreadDistance / (score.getTeams().size() > 2 ? score.getTeams().size() : Bukkit
					.getOnlinePlayers().length)) - tries;
			for (Entity e : p.getNearbyEntities(minRadPlayers, 256, minRadPlayers)) {
				if (e instanceof Player) {
					Player n = (Player) e;
					if (score.getPlayerTeam(p) != null && score.getPlayerTeam(n) != null
							&& score.getPlayerTeam(p) != score.getPlayerTeam(n)) {
						noHazard = false;
					}
				}
			}
			if (l.getBlockY() < 48) {
				noHazard = false;
			}
			for (Material no : nospawn) {
				if (m == no) {
					noHazard = false;
				}
			}
			if (noHazard) {
				goodSpawn = true;
				l.getChunk().load();
				while (l.getBlock().getType() != Material.AIR) {
					l.add(0, 1, 0);
					if (l.getY() > 256) {
						break;
					}
				}
				l.add(0, 3, 0);
				p.teleport(l);
				spread.add(p);

				if (score.getPlayerTeam(p) != null) {
					new ScatterLocation(l, System.currentTimeMillis(), score.getPlayerTeam(p));
				} else {
					new ScatterLocation(l, System.currentTimeMillis(), p);
				}

				return l;
			}
		}
		return null;
	}
}
