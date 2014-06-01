package net.mcshockwave.scatter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

	public static void spreadPlayers(final World world, int spreadDistance) {
		final long start = System.currentTimeMillis();

		ScatterLocation.locations.clear();

		Material[] nospawn = { Material.STATIONARY_WATER, Material.WATER, Material.STATIONARY_LAVA, Material.LAVA,
				Material.CACTUS };
		if (spreadDistance <= -1) {
			spreadDistance = 1000;
		}
		ArrayList<Player> spread = new ArrayList<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			boolean goodSpawn = false;
			for (Player p2 : spread) {
				Team t = score.getPlayerTeam(p);
				Team t2 = score.getPlayerTeam(p2);
				if (t != null && t2 != null && t == t2) {
					p.teleport(p2);
					goodSpawn = true;
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
				}
			}
		}
		spread.clear();

		Bukkit.getScheduler().runTaskLater(InstaScatter.ins, new Runnable() {
			public void run() {
				Bukkit.broadcastMessage(InstaScatter.prefix + "Unsticking players...");
				for (Player p : world.getPlayers()) {
					p.teleport(p.getLocation().add(0, 1, 0));
				}

				long time = System.currentTimeMillis() - start;
				Bukkit.broadcastMessage(InstaScatter.prefix + "Done scattering! (took " + time + "ms)");
			}
		}, 10l);
	}

}
