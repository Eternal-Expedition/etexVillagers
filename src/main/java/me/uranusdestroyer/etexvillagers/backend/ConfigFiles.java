package me.uranusdestroyer.etexvillagers.backend;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import me.uranusdestroyer.etexcoreplugin.etexCorePlugin;
import me.uranusdestroyer.etexvillagers.etexVillagers;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigFiles {

    @Getter
    private static YamlDocument config;
    @Getter
    private static YamlDocument messages;

    private static Map<String, YamlConfiguration> guiTemplates = new HashMap<>();
    private static Map<String, YamlConfiguration> tradeTemplates = new HashMap<>();

    public static void load(etexVillagers instance) {
        File configFile = new File(instance.getDataFolder(), "config.yml");

        try {
            config = initializeYmlFile(instance.getDataFolder(), "config", instance.getResource("config.yml"));
            messages = initializeYmlFile(instance.getDataFolder(), "messages", instance.getResource("messages.yml"));
            //YamlDocument test = YamlDocument.

            // Load the gui-templates directory
            File guiTemplatesFolder = new File(instance.getDataFolder(), "gui-templates");
            if (!guiTemplatesFolder.exists()) {
                guiTemplatesFolder.mkdir();
                instance.saveResource("gui-templates/example-gui.yml", false);
            }
            File[] guiTemplateFiles = guiTemplatesFolder.listFiles();
            for (File file : guiTemplateFiles) {
                String templateName = file.getName().replace(".yml", "");
                YamlConfiguration template = new YamlConfiguration();
                try {
                    template.load(file);
                } catch (IOException | InvalidConfigurationException e) {
                    instance.getLogger().severe("Error loading gui template file: " + e.getMessage());
                }
                guiTemplates.put(templateName, template);
            }

            // Load the trade-templates directory
            File tradeTemplatesFolder = new File(instance.getDataFolder(), "trade-templates");
            if (!tradeTemplatesFolder.exists()) {
                tradeTemplatesFolder.mkdir();
                instance.saveResource("trade-templates/example-trades.yml", false);
            }
            File[] tradeTemplateFiles = tradeTemplatesFolder.listFiles();
            for (File file : tradeTemplateFiles) {
                String templateName = file.getName().replace(".yml", "");
                YamlConfiguration template = new YamlConfiguration();
                try {
                    template.load(file);
                } catch (IOException | InvalidConfigurationException e) {
                    instance.getLogger().severe("Error loading trade template file: " + e.getMessage());
                }
                tradeTemplates.put(templateName, template);
            }


        } catch (IOException e) {
            instance.getLogger().severe("Failed to load configs!!! The plugin will now disable. " + e.getMessage());
            Bukkit.getScheduler().runTask(instance, Bukkit::shutdown);
        }
    }


    private static YamlDocument initializeYmlFile(File file, String fileName, InputStream resource) throws IOException {
        YamlDocument yamlDocument = YamlDocument.create(new File(file, fileName + ".yml"),
                Objects.requireNonNull(resource),
                GeneralSettings.DEFAULT,
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"))
                        .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build()
        );

        yamlDocument.update();
        yamlDocument.save();

        return yamlDocument;
    }

    public static YamlConfiguration getGuiTemplate(String name) {
        return guiTemplates.get(name);
    }
    public static YamlConfiguration getTradeTemplate(String name) {
        return tradeTemplates.get(name);
    }
}