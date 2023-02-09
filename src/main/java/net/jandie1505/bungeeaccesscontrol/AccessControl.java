package net.jandie1505.bungeeaccesscontrol;

import net.jandie1505.bungeeaccesscontrol.config.ConfigManager;
import net.jandie1505.bungeeaccesscontrol.config.DefaultConfigValues;
import net.md_5.bungee.api.plugin.Plugin;

public class AccessControl extends Plugin {
    private static AccessControl accessControl;

    // PLUGIN

    private ConfigManager configManager;
    private boolean lockdown;

    @Override
    public void onEnable() {
        this.lockdown = false;

        this.configManager = new ConfigManager(this, DefaultConfigValues.getConfig(), "config.json");

        accessControl = this;
    }

    public boolean isLockdown() {
        return this.lockdown;
    }

    public void setLockdown(boolean lockdown) {
        this.lockdown = lockdown;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    // STATIC

    public static AccessControl getInstance() {
        return accessControl;
    }
}
