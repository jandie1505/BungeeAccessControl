package net.jandie1505.bungeeaccesscontrol;

import net.md_5.bungee.api.plugin.Plugin;

public class AccessControl extends Plugin {
    private static AccessControl accessControl;

    // PLUGIN

    private boolean lockdown;

    @Override
    public void onEnable() {
        this.lockdown = false;
    }

    public boolean isLockdown() {
        return this.lockdown;
    }

    public void setLockdown(boolean lockdown) {
        this.lockdown = lockdown;
    }

    // STATIC

    public static AccessControl getInstance() {
        return accessControl;
    }
}
