package com.artingl.easyrg;

import com.artingl.easyrg.Commands.Commands;
import com.artingl.easyrg.Commands.Runners.RunnerRegistry;
import com.artingl.easyrg.Events.AsyncTickEvent;
import com.artingl.easyrg.Events.Events;
import com.artingl.easyrg.Events.TickEvent;
import com.artingl.easyrg.Logger.MWLogger;
import com.artingl.easyrg.Permissions.Permissions;
import com.artingl.easyrg.Storage.Storage;
import com.artingl.easyrg.misc.Database.DatabaseProvider;
import com.artingl.easyrg.misc.Database.Field.ModelsRegistry;
import com.artingl.easyrg.misc.Database.MySQL.MySQLProvider;
import com.artingl.easyrg.misc.Database.SQLite.SQLiteProvider;
import com.artingl.easyrg.misc.Protocol.PacketsRegistry;
import com.artingl.easyrg.misc.Regions.RegionsRegistry;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import com.artingl.easyrg.misc.Utilities.ConfigUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;


public class PluginMain extends JavaPlugin {
    private final File configDirectory = getDataFolder();
    private final PluginDescriptionFile pluginInfo = getDescription();

    public static PluginMain instance;
    public static MWLogger logger;
    public static Storage storage;

    private LuckPerms luckPerms;
    private FileConfiguration language;
    private TickEvent tickEvent;
    private AsyncTickEvent asyncTickEvent;
    private RegionsRegistry regionsRegistry;
    private DatabaseProvider databaseProvider;
    private String prefix;

    public PluginMain() {
        instance = this;
        logger = new MWLogger();
        storage = new Storage();
        regionsRegistry = new RegionsRegistry();
        tickEvent = new TickEvent();
        asyncTickEvent = new AsyncTickEvent();

        for (Material material: Material.values()) {
            ChatUtils.MATERIALS_LIST.add(material.getKey().toString());
        }
    }

    @Override
    public void onEnable() {
        String[] configFiles = {
                "en-messages.yml",
                "ru-messages.yml",
        };

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.luckPerms = provider.getProvider();
        } else {
            PluginMain.logger.warning("Unable to get luckperms provider.");
        }

        try {
            // Initialize plugin's data folder
            if (!configDirectory.isDirectory()) {
                if (!configDirectory.mkdir()) {
                    throw new Exception("Unable to create plugin's directory.");
                }
            }

            this.saveDefaultConfig();
            this.reloadConfig();
            this.prefix = PluginMain.instance.getConfig().getString("prefix");

            try {
                for (String fileName : configFiles) {
                    File file = new File(getDataFolder(), fileName);

                    if (!file.exists()) {
                        saveResource(fileName, false);
                    }
                }
            } catch (Exception e) {
                PluginMain.logger.log(Level.SEVERE, "Unable to create config file.", e);
                this.setEnabled(false);
                return;
            }

            // Connect to database
            ModelsRegistry.init();
            ConfigurationSection db = getConfig().getConfigurationSection("db");

            if (db.getBoolean("enable"))
                databaseProvider = new MySQLProvider(
                        db.getString("username"),
                        db.getString("password"),
                        db.getString("table-prefix"),
                        db.getString("database"),
                        db.getString("host"),
                        db.getInt("port")
                );
            else
                databaseProvider = new SQLiteProvider(new File(configDirectory.getAbsolutePath() + "/database.db"));

            if (!databaseProvider.connect()) {
                this.setEnabled(false);
                return;
            }

            // Initialize necessary parts of the plugin
            storage.clear();
            regionsRegistry.setDatabaseProvider(databaseProvider);
            tickEvent.runTaskTimer(this, 10L, 10L);
            asyncTickEvent.runTaskAsynchronously(this);

            reloadLanguage();
            Commands.register();
            ConfigUtils.load();
            Events.register();
            RunnerRegistry.init();
            PacketsRegistry.register();

            if (this.luckPerms != null) {
                for (Permissions permission : Permissions.values()) {
                    PermissionNode.builder(permission.getPermission()).build();
                }
            }

            logger.info("Loading completed.");
            this.setEnabled(true);



//            RegionModel model = new RegionModel();
//            Region region = model.get(
//                    ModelCondition.make(new ModelField<>("id"), ModelCondition.Conditions.EQUALS, 1));
//
//            System.out.println(region);

        } catch (Exception e) {
            PluginMain.logger.log(Level.SEVERE, "Unable to load the plugin.", e);
            this.setEnabled(false);
        }
    }

    public void reload() {
        logger.info("Reloading plugin...");

        reloadConfig();
        reloadLanguage();

        storage.clear();

        logger.info("Reloading completed.");
    }

    @Override
    public void onDisable() {
        logger.warning("The server might be shutting down or reloading.");
        logger.warning("In case of reloading, the plugin does not support it! Using this may cause issues!");

        PacketsRegistry.unregister();
        HandlerList.unregisterAll(PluginMain.instance);
        tickEvent.cancel();
        asyncTickEvent.cancel();
        storage.clear();
        databaseProvider.shutdown();
    }

    public PluginDescriptionFile getPluginInfo() {
        return pluginInfo;
    }

    public LuckPerms getLuckPerms() {
        return this.luckPerms;
    }

    public File getConfigDirectory() {
        return configDirectory;
    }

    public FileConfiguration getLanguage() {
        return language;
    }

    public RegionsRegistry getRegionsRegistry() {
        return regionsRegistry;
    }

    public DatabaseProvider getDatabaseProvider() {
        return databaseProvider;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isSoundEnabled() {
        return getConfig()
                .getConfigurationSection("global-settings")
                .getConfigurationSection("sounds")
                .getBoolean("enable");
    }

    public void playCommandErrorSound(Player player) {
        if (player == null || !isSoundEnabled())
            return;

        player.playSound(player.getLocation(), Sound.valueOf(getConfig()
                .getConfigurationSection("global-settings")
                .getConfigurationSection("sounds")
                .getString("command-error")), 1, 1);
    }

    private void reloadLanguage() {
        // Load the language for the plugin that has been set in config.yml
        File langFile = new File(getDataFolder().getAbsolutePath() + "/" + getConfig().get("lang") + "-messages.yml");
        if (!langFile.isFile()) {
            PluginMain.logger.log(Level.SEVERE, "Unable to load language.",
                    new FileNotFoundException(getConfig().get("lang") + "-messages.yml does not exist."));
            this.setEnabled(false);
            return;
        }

        this.language = YamlConfiguration.loadConfiguration(langFile);
        logger.info(Objects.requireNonNull(getConfig().getString("lang")).toUpperCase(Locale.ROOT) + " language loaded!");
    }
}