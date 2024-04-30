package me.uranusdestroyer.etexvillagers.listeners;

import me.uranusdestroyer.etexvillagers.backend.ConfigFiles;
import me.uranusdestroyer.etexvillagers.features.VillagerManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInteractEntityListener implements Listener {

    private HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void villagerListener(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof Villager) {
            UUID uuid = event.getPlayer().getUniqueId();
            int cooldown_time = ConfigFiles.getConfig().getInt("vanilla-villagers-config.cooldown");

            cooldowns.putIfAbsent(uuid, 0L);
            if ( System.currentTimeMillis() >=  cooldowns.get(uuid) + cooldown_time * 1000L) {
                Villager villager = (Villager) entity;
                if (villager.isAdult()) {
                    event.setCancelled(true);

                    VillagerManager.openVillager(event.getPlayer(), villager);

                    cooldowns.put(uuid, System.currentTimeMillis());
                }
            } else {event.setCancelled(true);}
        }

    }

    @EventHandler
    public void OnDisconnect(PlayerQuitEvent e) {
        cooldowns.remove(e.getPlayer().getUniqueId());
    }

}
