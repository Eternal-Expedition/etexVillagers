package me.uranusdestroyer.etexvillagers.features;


import me.uranusdestroyer.etexvillagers.etexVillagers;
import me.uranusdestroyer.etexvillagers.features.objects.etexVillager;
import me.uranusdestroyer.etexvillagers.guis.VillagerGui;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillagerManager {

    private static etexVillagers instance;

    public VillagerManager(etexVillagers pluginInstance) {
        instance = pluginInstance;
    }


    public static List<UUID> activeVillagers = new ArrayList<>();

    public static void openVillager(Player player, Villager villager) {

        if (!activeVillagers.contains(villager.getUniqueId())) {

            etexVillager ev = new etexVillager(instance, villager);
            ev.getVillagerDebug();

            activeVillagers.add(ev.getVillager().getUniqueId());

            VillagerGui.VillagerGui(ev).open(player);

        } else {
            // TODO: add from messages.yml
            player.sendMessage("Villager is already active!");
        }



    }

}
