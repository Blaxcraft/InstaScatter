package net.mcshockwave.scatter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class ScatterLocation {

	public static ArrayList<ScatterLocation>	locations	= new ArrayList<>();

	public Location								loc;
	public long									time;
	public Player								p;
	public Team									t;

	public ScatterLocation(Location loc, long time, Player p) {
		this.loc = loc;
		this.time = time;
		this.p = p;
		this.t = null;

		locations.add(this);
	}

	public ScatterLocation(Location loc, long time, Team t) {
		this.loc = loc;
		this.time = time;
		this.p = null;
		this.t = t;

		locations.add(this);
	}

	public static ScatterLocation getLocation(Player p) {
		for (ScatterLocation sl : locations) {
			if (sl.p == p || ScatterManager.score.getPlayerTeam(p) == sl.t) {
				return sl;
			}
		}
		return null;
	}
	
	public long getTimeSinceSpread() {
		return System.currentTimeMillis() - time;
	}

}
