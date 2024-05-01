package me.uranusdestroyer.etexvillagers.guis;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.uranusdestroyer.etexcoreplugin.api.API;
import me.uranusdestroyer.etexcoreplugin.backend.MessageUtils;
import me.uranusdestroyer.etexcoreplugin.features.currenciesbank.CurrencyUtils;
import me.uranusdestroyer.etexcoreplugin.features.itemmanager.ItemHandler;
import me.uranusdestroyer.etexvillagers.backend.ConfigFiles;
import me.uranusdestroyer.etexvillagers.etexVillagers;
import me.uranusdestroyer.etexvillagers.features.objects.etexVillager;
import me.uranusdestroyer.etexvillagers.features.objects.etexVillagerTrade;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.uranusdestroyer.etexvillagers.etexVillagers.etexCoreAPI;
import static me.uranusdestroyer.etexvillagers.features.VillagerManager.activeVillagers;

public class VillagerGui {
    public static Gui VillagerGui(etexVillager ev, Player p) {

        YamlConfiguration gui_template = ConfigFiles.getGuiTemplate(ev.getGuiTemplateString());
        YamlConfiguration trade_template = ConfigFiles.getTradeTemplate(ev.getTradeTemplateString());

        Gui gui = Gui.gui()
                .title(etexCoreAPI.getMessageUtils().componentFromString(gui_template.getString("title").replace("%xp%", String.valueOf(ev.getLevel()))))
                .rows(6)
                .disableItemTake()
                .create();


        gui.setCloseGuiAction(inventoryCloseEvent -> {
            activeVillagers.remove(ev.getVillager().getUniqueId());
        });


        int index = 0;
        for (etexVillagerTrade trade : ev.getTradeData()) {
            int sId = trade.getSlotId();
            String tId = trade.getTradeId();
            System.out.println(sId + " : " + tId);
            ConfigurationSection section = trade_template.getConfigurationSection("trades." + tId);

            ItemStack itemStack = etexCoreAPI.getItemManager().getItemStack(section.getString("trade-display"));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(getTradeItemLore(gui_template, section, p, ev, sId));
            itemStack.setItemMeta(itemMeta);

            GuiItem tradeItem = ItemBuilder.from(itemStack).asGuiItem(event -> {
                event.setCancelled(true);
                ev.executeTrade(sId, (Player) event.getWhoClicked());
                gui.update();
                VillagerGui.VillagerGui(ev, p).open(p);
            });

            gui.updateItem(index, tradeItem);
            index += 1;
        }

        if(gui_template.getBoolean("fill-item.enabled")){
            ItemStack fillItemStack = ItemHandler.itemStackFromString(gui_template.getString("fill-item.display-item"));
            ItemMeta itemMeta = fillItemStack.getItemMeta();
            itemMeta.setDisplayName(gui_template.getString("fill-item.name"));
            fillItemStack.setItemMeta(itemMeta);
            GuiItem guiItem = ItemBuilder.from(fillItemStack).asGuiItem(event -> event.setCancelled(true));
            gui.getFiller().fill(guiItem);
        }

        return gui;
    }

    private static List<String> getTradeItemLore(YamlConfiguration gui_template, ConfigurationSection trade, Player p, etexVillager ev, int sId) {
        List<String> lore = new ArrayList<>();

        lore.addAll(gui_template.getStringList("lore.custom-text-up"));
        lore.add(gui_template.getString("lore.input-items"));

        for (String inputItemLore : trade.getStringList("input-items")) {
            ItemStack itemStack = etexCoreAPI.getItemManager().getItemStack(inputItemLore);
            String hasItem = String.valueOf(etexCoreAPI.getItemManager().hasItem(inputItemLore, p, false));

            lore.add(gui_template.getString("lore.input-items-style")
                    .replace("%has_item%", hasItem)
                    .replace("%item_count%", String.valueOf(itemStack.getAmount()))
                    .replace("%item_name%", itemStack.getItemMeta().getDisplayName())
            );
        }

        lore.add(gui_template.getString("lore.output-items"));
        for (String inputItemLore : trade.getStringList("output-items")) {
            ItemStack itemStack = etexCoreAPI.getItemManager().getItemStack(inputItemLore);
            String hasItem = String.valueOf(etexCoreAPI.getItemManager().hasItem(inputItemLore, p, false));

            lore.add(gui_template.getString("lore.input-items-style")
                    .replace("%has_item%", hasItem)
                    .replace("%item_count%", String.valueOf(itemStack.getAmount()))
                    .replace("%item_name%", itemStack.getItemMeta().getDisplayName())
            );
        }

        lore.add(gui_template.getString("lore.trade-count")
                .replace("%trade_count%", String.valueOf(ev.getTradeData().get(sId).getUsed()))
                .replace("%trade_max%", String.valueOf(trade.getInt("max-trades"))));

        lore.addAll(gui_template.getStringList("lore.custom-text-down"));

        return MessageUtils.legacyMinimessageStringList(lore);

    }

}
