package net.nighthawkempires.survival;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import net.nighthawkempires.core.NECore;
import net.nighthawkempires.core.file.FileFolder;
import net.nighthawkempires.survival.listener.PlayerListener;
import net.nighthawkempires.survival.scoreboard.SurvivalScoreboards;
import net.nighthawkempires.survival.user.registry.FUserRegistry;
import net.nighthawkempires.survival.user.registry.MUserRegistry;
import net.nighthawkempires.survival.user.registry.UserRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NESurvival extends JavaPlugin {

    private static Plugin plugin;
    private static NESurvival instance;

    private static PluginManager pluginManager;

    private static MongoDatabase mongoDatabase;

    private static UserRegistry userRegistry;

    public void onEnable() {
        plugin = this;
        instance = this;

        pluginManager = Bukkit.getPluginManager();

        if (NECore.getSettings().mongoEnabledGuilds) {
            try {
                String hostname = NECore.getSettings().mongoHostnameGuilds;
                String username = NECore.getSettings().mongoUsernameGuilds;
                String password = NECore.getSettings().mongoPasswordGuilds;
                ServerAddress address = new ServerAddress(hostname, 27017);
                MongoCredential credential =
                        MongoCredential.createCredential("survival", "ne_survival", password.toCharArray());
                mongoDatabase =
                        new MongoClient(address, credential, new MongoClientOptions.Builder().build())
                                .getDatabase("ne_survival");
                userRegistry = new MUserRegistry(mongoDatabase);
                NECore.getLoggers().info(this, "MongoDB enabled.");
            } catch (Exception oops) {
                oops.printStackTrace();
                NECore.getLoggers().warn("MongoDB connection failed. Disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        } else {
            userRegistry = new FUserRegistry(FileFolder.PLAYER_PATH.getPath());
        }

        NECore.getScoreboardManager().addScoreboard(new SurvivalScoreboards());

        registerCommands();
        registerListeners();

        getUserRegistry().loadAllFromDb();
    }

    public void onDisable() {

    }

    private void registerCommands() {

    }

    private void registerListeners() {
        getPluginManager().registerEvents(new PlayerListener(), this);
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static NESurvival getInstance() {
        return instance;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static UserRegistry getUserRegistry() {
        return userRegistry;
    }
}
