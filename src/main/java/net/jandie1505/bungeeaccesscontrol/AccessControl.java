package net.jandie1505.bungeeaccesscontrol;

import net.jandie1505.bungeeaccesscontrol.commands.ACCommand;
import net.jandie1505.bungeeaccesscontrol.config.ConfigManager;
import net.jandie1505.bungeeaccesscontrol.config.DefaultConfigValues;
import net.jandie1505.bungeeaccesscontrol.database.DatabaseManager;
import net.jandie1505.bungeeaccesscontrol.database.managers.MySQLDatabaseManager;
import net.jandie1505.bungeeaccesscontrol.events.EventListener;
import net.jandie1505.bungeeaccesscontrol.managers.BanManager;
import net.jandie1505.bungeeaccesscontrol.managers.MaintenanceManager;
import net.jandie1505.bungeeaccesscontrol.managers.PlayerCacheManager;
import net.jandie1505.bungeeaccesscontrol.managers.data.Ban;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AccessControl extends Plugin {
    public static final String VERSION = "1.0";
    private static AccessControl accessControl;

    // PLUGIN

    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private BanManager banManager;
    private PlayerCacheManager playerCacheManager;
    private MaintenanceManager maintenanceManager;
    private boolean lockdown;

    @Override
    public void onEnable() {

        // START MESSAGE

        this.getLogger().info("Enabling BungeeAccessControl...");

        // LOCKDOWN STATUS

        this.lockdown = false;

        // MANAGER OBJECTS

        this.configManager = new ConfigManager(this, DefaultConfigValues.getConfig(), "config.json");
        this.configManager.reloadConfig();
        this.databaseManager = new MySQLDatabaseManager(this);
        this.banManager = new BanManager(this);
        this.playerCacheManager = new PlayerCacheManager(this);
        this.maintenanceManager = new MaintenanceManager(this);

        // LISTENERS AND COMMANDS

        this.getProxy().getPluginManager().registerListener(this, new EventListener(this));
        this.getProxy().getPluginManager().registerCommand(this, new ACCommand(this));

        // REPEATING TASKS

        int time = this.getConfigManager().getConfig().optJSONObject("enforce", new JSONObject()).optInt("time", 60);

        if (time <= 0 || time > 3600) {
            time = 60;
        }

        this.getProxy().getScheduler().schedule(this, () -> {

            for (ProxiedPlayer player : List.copyOf(this.getProxy().getPlayers())) {
                if (player != null) {

                    if (this.isLockdown() && !player.hasPermission(this.getConfigManager().getConfig().optJSONObject("permissions", new JSONObject()).optString("bypassLockdown", "accesscontrol.bypass.lockdown"))) {
                        player.disconnect(this.getConfigManager().getConfig().optJSONObject("disconnectScreens", new JSONObject()).optString("lockdown", "This network is currently under lockdown"));
                        continue;
                    }

                    if (this.getConfigManager().getConfig().optJSONObject("enforce", new JSONObject()).optBoolean("maintenance", true) && this.getMaintenanceManager().getMaintenanceStatus() && !player.hasPermission(this.getConfigManager().getConfig().optJSONObject("permissions", new JSONObject()).optString("bypassMaintenance", "accesscontrol.bypass.maintenance"))) {
                        player.disconnect(this.getMaintenanceManager().generateMaintenanceScreen());
                        continue;
                    }

                    if (this.getConfigManager().getConfig().optJSONObject("enforce", new JSONObject()).optBoolean("bans", true) && !player.hasPermission(this.getConfigManager().getConfig().optJSONObject("permissions", new JSONObject()).optString("unbannable", "accesscontrol.unbannable"))) {
                        Ban ban = this.getBanManager().getLongestBan(player.getUniqueId());

                        if (ban != null) {
                            player.disconnect(this.getBanManager().generateBanScreen(ban));
                        }
                    }

                }
            }

        }, 0, time, TimeUnit.SECONDS);

        // STATIC REFERENCE

        accessControl = this;

        // FINISHED ENABLING MESSAGE

        this.getLogger().info("BungeeAccessControl " + AccessControl.VERSION + " by jandie1505 was successfully enabled");
    }

    public boolean isLockdown() {
        return this.lockdown;
    }

    public void setLockdown(boolean lockdown) {
        this.lockdown = lockdown;

        this.getLogger().info("Lockdown status updated: " + this.lockdown);
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public BanManager getBanManager() {
        return this.banManager;
    }

    public PlayerCacheManager getPlayerCacheManager() {
        return this.playerCacheManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return this.maintenanceManager;
    }

    // STATIC

    public static AccessControl getInstance() {
        return accessControl;
    }
}
