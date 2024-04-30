package me.uranusdestroyer.etexvillagers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerCareerChangeEvent;

public class VillagerCareerChangeListener implements Listener {

    @EventHandler
    public void onVillagerCareerChange(VillagerCareerChangeEvent event) {
        event.setCancelled(true);
    }

}
