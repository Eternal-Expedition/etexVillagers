package me.uranusdestroyer.etexvillagers.features.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import me.uranusdestroyer.etexcoreplugin.api.API;
import me.uranusdestroyer.etexvillagers.backend.ConfigFiles;
import me.uranusdestroyer.etexvillagers.etexVillagers;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Type;
import java.util.*;

import static me.uranusdestroyer.etexvillagers.etexVillagers.etexCoreAPI;


public class etexVillager {

    // TODO: I knew once what each part of this code was doing
    // TODO: But I no longer remember, so dont touch it or it will break probably :)

    private NamespacedKey SET_KEY;
    private NamespacedKey GUI_TEMPLATE_VERSION;
    private NamespacedKey GUI_TEMPLATE;
    private NamespacedKey EXPIRE_TIMESTAMP;
    private NamespacedKey XP;
    private NamespacedKey TRADE_TEMPLATE;
    private NamespacedKey TRADE_DATA;


    private final etexVillagers instance;

    @Getter
    private Villager villager;

    private Villager.Profession profession;
    private Villager.Type type;

    private static final Gson GSON = new Gson();
    private static final Type TRADE_LIST_TYPE = new TypeToken<List<etexVillagerTrade>>(){}.getType();

    public etexVillager(etexVillagers instance, Villager villager) {
        this.instance = instance;
        this.villager = villager;

        this.SET_KEY = new NamespacedKey(instance, "set");
        this.GUI_TEMPLATE_VERSION = new NamespacedKey(instance, "gtv");
        this.GUI_TEMPLATE = new NamespacedKey(instance, "gt");
        this.EXPIRE_TIMESTAMP = new NamespacedKey(instance, "ets");
        this.XP = new NamespacedKey(instance, "xp");
        this.TRADE_TEMPLATE = new NamespacedKey(instance, "tt");
        this.TRADE_DATA = new NamespacedKey(instance, "td");

        this.type = villager.getVillagerType();

        check();
    }

    // Villager stuff

    public void check() {
        if (!villager.getPersistentDataContainer().has(SET_KEY, PersistentDataType.BOOLEAN)) {
            // If not, generate valid data
            make(true);
            return;
        }

        // Check timestamp and expiration
        Long expire_timestamp = villager.getPersistentDataContainer().get(EXPIRE_TIMESTAMP, PersistentDataType.LONG);
        long currentTime = System.currentTimeMillis();
        // Check if still valid
        if (expire_timestamp == null || expire_timestamp < currentTime) {
            make(false);
            return;
        }

        YamlConfiguration gui_template = ConfigFiles.getGuiTemplate(getGuiTemplateString());
        if (villager.getPersistentDataContainer().get(GUI_TEMPLATE_VERSION, PersistentDataType.INTEGER) < gui_template.getInt("template-version")) {
            make(false);
            return;
        }

        String trade_template_right = gui_template.getString("trade-templates-per-levels."+getLevel());
        String trade_template = villager.getPersistentDataContainer().get(TRADE_TEMPLATE, PersistentDataType.STRING);

        if (trade_template_right != null && trade_template != null && !trade_template_right.equalsIgnoreCase(trade_template)) {
            make(false);
        }
    }

    private void make(boolean firstTime) {
        if (firstTime) {
            System.out.println("first time");
            villager.getPersistentDataContainer().set(XP, PersistentDataType.INTEGER, 0);
            villager.setProfession(getRandomProfession());
            villager.setVillagerExperience(1);
        }
        profession = villager.getProfession();
        String profString = profession.name().toLowerCase();
        String biomeString = villager.getVillagerType().toString().toLowerCase();

        Section section = ConfigFiles.getConfig().getSection("vanilla-villagers-config.professions." + profString);

        String gui_template;
        if (section.getString("biomes." + biomeString) != null) {
            gui_template = section.getString("biomes." + biomeString);
        } else {
            gui_template = section.getString("default-gui-template");
        }

        YamlConfiguration gui_template_conf = ConfigFiles.getGuiTemplate(gui_template);

        int gui_template_version = gui_template_conf.getInt("template-version");
        long expire_time = gui_template_conf.getLong("refresh-rate");

        System.out.println(gui_template);
        villager.getPersistentDataContainer().set(GUI_TEMPLATE, PersistentDataType.STRING, gui_template);
        villager.getPersistentDataContainer().set(GUI_TEMPLATE_VERSION, PersistentDataType.INTEGER, gui_template_version);
        villager.getPersistentDataContainer().set(EXPIRE_TIMESTAMP, PersistentDataType.LONG, System.currentTimeMillis() + expire_time * 60 * 1000);

        // Trade data system
        generateTradeData();

        villager.getPersistentDataContainer().set(SET_KEY, PersistentDataType.BOOLEAN, true);
    }

    public void getVillagerDebug() {
        System.out.println("Clicked on villager: " + villager.getUniqueId());
        System.out.println("Villager prof and type:" + villager.getProfession() + " : " + villager.getType() );

        System.out.println(villager.getPersistentDataContainer().get(SET_KEY, PersistentDataType.BOOLEAN));
        System.out.println(villager.getPersistentDataContainer().get(GUI_TEMPLATE_VERSION, PersistentDataType.INTEGER));
        System.out.println(villager.getPersistentDataContainer().get(GUI_TEMPLATE, PersistentDataType.STRING));

    }


    private Villager.Profession getRandomProfession() {

        List<String> proflist = new ArrayList<>();
        Section professions = ConfigFiles.getConfig().getSection("vanilla-villagers-config.professions");
        assert professions != null;
        for (Object key : professions.getKeys()) {
            String keyString = (String) key;
            if(professions.getBoolean(keyString + ".enabled")) {

                int chance = professions.getInt(key + ".chance");
                for (int i = 0; i < chance; i++) {
                    proflist.add(keyString);
                }
            }

        }
        String randomProfession = proflist.get(new Random().nextInt(proflist.size()));
        return Villager.Profession.valueOf(randomProfession.toUpperCase());
    }

    public String getGuiTemplateString() {
        System.out.println(villager.getPersistentDataContainer().get(GUI_TEMPLATE, PersistentDataType.STRING));
        return villager.getPersistentDataContainer().get(GUI_TEMPLATE, PersistentDataType.STRING);
    }
    public String getTradeTemplateString() {
        return villager.getPersistentDataContainer().get(TRADE_TEMPLATE, PersistentDataType.STRING);
    }


    public int getLevel() {
        YamlConfiguration gui_template = ConfigFiles.getGuiTemplate(getGuiTemplateString());
        int xp = villager.getPersistentDataContainer().get(XP, PersistentDataType.INTEGER);
        List<Integer> levels = gui_template.getIntegerList("levels");
        int currentLevel = 0;
        for (int levelXp : levels) {
            if (xp >= levelXp) {
                currentLevel++;
            } else {
                break;}}
        return currentLevel;
    }

    // Trading stuff

    public List<etexVillagerTrade> getTradeData() {
        String tradeJsonList = villager.getPersistentDataContainer().get(TRADE_DATA, PersistentDataType.STRING);

        if (tradeJsonList != null && !tradeJsonList.isEmpty()) {
            return GSON.fromJson(tradeJsonList, TRADE_LIST_TYPE);
        } else {
            return null;
        }
    }

    public void saveTradeData(List<etexVillagerTrade> trades) {
        String tradeJsonList = GSON.toJson(trades, TRADE_LIST_TYPE);
        villager.getPersistentDataContainer().set(TRADE_DATA, PersistentDataType.STRING, tradeJsonList);
    }

    private void generateTradeData() {
        YamlConfiguration gui_template = ConfigFiles.getGuiTemplate(getGuiTemplateString());
        String trade_template = gui_template.getString("trade-templates-per-levels."+getLevel());

        villager.getPersistentDataContainer().set(TRADE_TEMPLATE, PersistentDataType.STRING, trade_template);

        ConfigurationSection trade_template_section = ConfigFiles.getTradeTemplate(trade_template).getConfigurationSection("trades");
        List<String> tradeSelectList = new ArrayList<>();
        assert trade_template_section != null;
        for (String key: trade_template_section.getKeys(false)) {
            System.out.println(key);
            int chance = trade_template_section.getInt(key + ".chance");
            for (int i = 0; i < chance; i++) {
                tradeSelectList.add(key);
            }

        }
        System.out.println(tradeSelectList);
        List<etexVillagerTrade> tradeList = new ArrayList<>();
        for (int i = 0; i < Objects.requireNonNull(gui_template.getList("trade-slots")).size(); i++) {
            // TODO: more interesting randomness here
            if (!tradeSelectList.isEmpty()) {
                String randomTrade = tradeSelectList.get(new Random().nextInt(tradeSelectList.size()));
                tradeList.add(new etexVillagerTrade(i, randomTrade, 0));

                tradeSelectList.removeAll(Collections.singleton(randomTrade));
            }
        saveTradeData(tradeList);
        }
    }

    public void executeTrade(int slotId, Player player) {
        List<etexVillagerTrade> tradeData = getTradeData();
        for(etexVillagerTrade trade : tradeData) {
            if (trade.getSlotId() == slotId) {
                System.out.println("starting trade");
                String tId = trade.getTradeId();
                int used = trade.getUsed();

                String trade_template = getTradeTemplateString();
                ConfigurationSection section = ConfigFiles.getTradeTemplate(trade_template).getConfigurationSection("trades." + tId);
                int maxTrades = section.getInt("max-trades");

                if (used < maxTrades) {
                    List<String> items = section.getStringList("input-items");
                    boolean hasAllItems = true;
                    for (String item : items) {
                        if (!etexCoreAPI.getItemManager().hasItem(item, player, false)) {
                            hasAllItems = false;
                            break;
                        }
                    }
                    if (hasAllItems) {

                        for (String item : items) {
                            etexCoreAPI.getItemManager().hasItem(item, player, true);
                        }

                        for (String item : section.getStringList("output-items")) {
                            ItemStack itemStack = etexCoreAPI.getItemManager().getItemStack(item);
                            etexCoreAPI.getItemManager().stashAdd(player, itemStack);
                        }

                        int xp = villager.getPersistentDataContainer().get(XP, PersistentDataType.INTEGER);
                        xp += section.getInt("xp");
                        villager.getPersistentDataContainer().set(XP, PersistentDataType.INTEGER, xp);

                        trade.setUsed(used+1);
                        saveTradeData(tradeData);

                        player.sendMessage("Trade successful!");
                    } else {
                        player.sendMessage("not enough items");
                    }


                } else {
                    // TODO: implement messages.yml here
                    player.sendMessage("Moc tradujes");
                } return;
            }
            }
        }
}
