package net.jandie1505.bungeeaccesscontrol;

import net.jandie1505.bungeeaccesscontrol.managers.BanManager;
import net.jandie1505.bungeeaccesscontrol.managers.MaintenanceManager;
import net.jandie1505.bungeeaccesscontrol.managers.PlayerCacheManager;

public class AccessControlAPI {
    private final AccessControl accessControl;

    public AccessControlAPI(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    public BanManager getBanManager() {
        return this.accessControl.getBanManager();
    }

    public MaintenanceManager getMaintenanceManager() {
        return this.accessControl.getMaintenanceManager();
    }

    public PlayerCacheManager getPlayerCacheManager() {
        return this.accessControl.getPlayerCacheManager();
    }
}
