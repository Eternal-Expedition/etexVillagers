package me.uranusdestroyer.etexvillagers;

import lombok.Getter;
import me.uranusdestroyer.etexcoreplugin.api.API;
import me.uranusdestroyer.etexcoreplugin.etexCorePlugin;
import me.uranusdestroyer.etexvillagers.backend.ConfigFiles;
import me.uranusdestroyer.etexvillagers.commands.MainCommand;
import me.uranusdestroyer.etexvillagers.features.VillagerManager;
import me.uranusdestroyer.etexvillagers.listeners.PlayerInteractEntityListener;
import me.uranusdestroyer.etexvillagers.listeners.VillagerCareerChangeListener;
import me.uranusdestroyer.etexvillagers.listeners.VillagerMoveListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class etexVillagers extends JavaPlugin {

    @Getter
    private static etexVillagers instance;

    @Getter
    public static API etexCoreAPI;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin is starting up!");

        instance = this;

        // Setting up the mandatory dependency etexCore
        etexCorePlugin etexCore = (etexCorePlugin) Bukkit.getServer().getPluginManager().getPlugin("etexCore");
        if (etexCore == null) {
            getLogger().severe("The plugin etexCore is not found, shutting down the server.");
            Bukkit.getServer().shutdown();
        } else {
            etexCoreAPI = etexCore.api;
            if ("pong".equals(etexCoreAPI.ping())) {
                getLogger().info("Pong! The etexCore API is hooked successfully!");
            }
        }

        // Load config files
        ConfigFiles.load(instance);

        // Vanilla villagers revamp setup
        getLogger().info("Vanilla villagers disabled: " + getConfig().getBoolean("disable-vanilla-villagers"));
        if(getConfig().getBoolean("disable-vanilla-villagers")) {
            new VillagerManager(instance);
            getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(), instance);
            getServer().getPluginManager().registerEvents(new VillagerCareerChangeListener(), instance);
            getServer().getPluginManager().registerEvents(new VillagerMoveListener(), instance);
            getLogger().info("Revamped villagers enabled!");
        }

        // Command register
        //getCommand("etexvillagers").setExecutor(new MainCommand(instance));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin is shutting down!");



        getLogger().info("Plugin died!");
    }

}
