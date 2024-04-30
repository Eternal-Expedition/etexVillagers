package me.uranusdestroyer.etexvillagers.guis;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.uranusdestroyer.etexcoreplugin.api.API;
import me.uranusdestroyer.etexvillagers.backend.ConfigFiles;
import me.uranusdestroyer.etexvillagers.etexVillagers;
import me.uranusdestroyer.etexvillagers.features.objects.etexVillager;
import me.uranusdestroyer.etexvillagers.features.objects.etexVillagerTrade;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import static me.uranusdestroyer.etexvillagers.etexVillagers.etexCoreAPI;
import static me.uranusdestroyer.etexvillagers.features.VillagerManager.activeVillagers;

public class VillagerGui {
    public static Gui VillagerGui(etexVillager ev) {

        YamlConfiguration gui_template = ConfigFiles.getGuiTemplate(ev.getGuiTemplateString());
        YamlConfiguration trade_template = ConfigFiles.getTradeTemplate(ev.getTradeTemplateString());

        Gui gui = Gui.gui()
                .title(etexCoreAPI.getMessageUtils().componentFromString(gui_template.getString("title").replace("%xp%", String.valueOf(ev.getLevel()))))
                .rows(6)
                .create();

        gui.setCloseGuiAction(inventoryCloseEvent -> {
            activeVillagers.remove(ev.getVillager().getUniqueId());
        });


        int index = 0;
        for (etexVillagerTrade trade : ev.getTradeData()) {
            int sId = trade.getSlotId();
            String tId = trade.getTradeId();

            ConfigurationSection section = trade_template.getConfigurationSection("trades." + tId);

            GuiItem tradeItem = ItemBuilder.from(etexCoreAPI.getItemManager().getItemStack(section.getString("trade-display"))).asGuiItem(event -> {
                event.setCancelled(true);
            });

            gui.updateItem(index, tradeItem);
            index += 1;
        }
        return gui;
    }

}
