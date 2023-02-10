package net.jandie1505.bungeeaccesscontrol;

import net.jandie1505.bungeeaccesscontrol.config.ConfigManager;
import net.jandie1505.bungeeaccesscontrol.config.DefaultConfigValues;
import net.jandie1505.bungeeaccesscontrol.database.DatabaseManager;
import net.jandie1505.bungeeaccesscontrol.database.managers.MySQLDatabaseManager;
import net.jandie1505.bungeeaccesscontrol.managers.BanManager;
import net.jandie1505.bungeeaccesscontrol.managers.data.Ban;
import net.md_5.bungee.api.plugin.Plugin;

public class AccessControl extends Plugin {
    private static AccessControl accessControl;

    // PLUGIN

    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private BanManager banManager;
    private boolean lockdown;
    private boolean maintenance;

    @Override
    public void onEnable() {
        this.lockdown = false;
        this.maintenance = false;

        this.configManager = new ConfigManager(this, DefaultConfigValues.getConfig(), "config.json");
        this.databaseManager = new MySQLDatabaseManager(this);
        this.banManager = new BanManager(this);

        accessControl = this;
    }

    public boolean isLockdown() {
        return this.lockdown;
    }

    public void setLockdown(boolean lockdown) {
        this.lockdown = lockdown;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
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

    // STATIC

    public static AccessControl getInstance() {
        return accessControl;
    }
}
