package net.jandie1505.bungeeaccesscontrol.managers;

import net.jandie1505.bungeeaccesscontrol.AccessControl;

public class MaintenanceManager {
    private final AccessControl accessControl;


    public MaintenanceManager(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    /**
     * Enable maintenance
     * @return success
     */
    public boolean enableMaintenance() {
        return this.accessControl.getDatabaseManager().setMaintenanceStatus(true);
    }

    /**
     * Disable maintenance
     * @return success
     */
    public boolean disableMaintenance() {
        return this.accessControl.getDatabaseManager().setMaintenanceStatus(false);
    }

    /**
     * Get maintenance status
     * @return maintenance status
     */
    public boolean getMaintenanceStatus() {
        return this.accessControl.getDatabaseManager().getMaintenanceStatus();
    }
}
