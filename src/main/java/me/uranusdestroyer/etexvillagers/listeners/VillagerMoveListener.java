package me.uranusdestroyer.etexvillagers.listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

import static me.uranusdestroyer.etexvillagers.features.VillagerManager.activeVillagers;

public class VillagerMoveListener implements Listener {

    @EventHandler
    public void onVillagerMove(EntityMoveEvent e) {
        if(e.getEntity() instanceof Villager) {
            if(activeVillagers.contains(e.getEntity().getUniqueId())) {
                if(e.getEntity().getFallDistance() == 0){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onVillagerDeath(EntityDeathEvent e) {
        if(e.getEntity() instanceof Villager){
            activeVillagers.remove(e.getEntity().getUniqueId());
        }
    }



}
