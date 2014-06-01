package net.mcshockwave.scatter;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DefaultListener implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity ee = event.getEntity();

		if (ee instanceof Player) {
			Player p = (Player) ee;

			if (ScatterLocation.getLocation(p) != null) {
				ScatterLocation sl = ScatterLocation.getLocation(p);

				if (sl.getTimeSinceSpread() < InstaScatter.ins.getConfig().getInt("scatter_damage_time") * 1000) {
					event.setCancelled(true);
					if (event.getCause() == DamageCause.SUFFOCATION) {
						p.teleport(p.getLocation().add(0, 3, 0));
					} else if (event.getCause() != DamageCause.CONTACT) {
						p.sendMessage(InstaScatter.prefix + ConfigFile.Messages.get().getString("scatter_damage"));
					}
				}
			}
		}
	}

}
