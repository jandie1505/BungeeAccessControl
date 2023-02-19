package net.jandie1505.bungeeaccesscontrol.managers;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.managers.data.MaintenanceScreen;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MaintenanceManager {
    private final AccessControl accessControl;
    private final Map<String, MaintenanceScreen> maintenanceScreens;
    private static final MaintenanceScreen DEFAULT_MAINTENANCE_SCREEN = () -> "This network is currently under maintenance";

    public MaintenanceManager(AccessControl accessControl) {
        this.accessControl = accessControl;
        this.maintenanceScreens = new HashMap<>();
    }

    // MAINTENANCE MANAGEMENT

    /**
     * Enable maintenance
     * @return success
     */
    public boolean enableMaintenance() {
        boolean success = this.accessControl.getDatabaseManager().setMaintenanceStatus(true);

        if (success) {
            this.accessControl.getLogger().info("Maintenance mode enabled");
        }

        return success;
    }

    /**
     * Disable maintenance
     * @return success
     */
    public boolean disableMaintenance() {
        boolean success = this.accessControl.getDatabaseManager().setMaintenanceStatus(false);

        if (success) {
            this.accessControl.getLogger().info("Maintenance mode disabled");
        }

        return success;
    }

    /**
     * Get maintenance status
     * @return maintenance status
     */
    public boolean getMaintenanceStatus() {
        return this.accessControl.getDatabaseManager().getMaintenanceStatus();
    }

    // MAINTENANCE SCREENS

    /**
     * Register a maintenance screen
     * @param name name of the maintenance screen
     * @param maintenanceScreen maintenance screen
     * @return success
     */
    public boolean registerMaintenanceScreen(String name, MaintenanceScreen maintenanceScreen) {
        if (name != null && maintenanceScreen != null && this.maintenanceScreens.containsKey(name) && !name.equalsIgnoreCase("default")) {
            this.maintenanceScreens.put(name, maintenanceScreen);
            this.accessControl.getLogger().info("Maintenance screen " + name + " was successfully registered");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return an unmodifiable copy of all maintenance screens
     * @return map of all maintenance screens
     */
    public Map<String, MaintenanceScreen> getMaintenanceScreens() {
        Map<String, MaintenanceScreen> map = new HashMap<>(this.maintenanceScreens);
        map.put("default", MaintenanceManager.DEFAULT_MAINTENANCE_SCREEN);
        return Map.copyOf(map);
    }

    /**
     * Get a maintenance screen with a specific name
     * @param name name of the maintenance screen
     * @return maintenance screen
     */
    public MaintenanceScreen getMaintenanceScreen(String name) {
        if (name.equalsIgnoreCase("default") && !this.maintenanceScreens.containsKey(name)) {
            return MaintenanceManager.DEFAULT_MAINTENANCE_SCREEN;
        } else {
            return this.maintenanceScreens.get(name);
        }
    }

    /**
     * Remove a maintenance screen
     * @param name name of the maintenance screen
     * @return removed maintenance screen
     */
    public MaintenanceScreen removeMaintenanceScreen(String name) {
        MaintenanceScreen deletedScreen = this.maintenanceScreens.remove(name);

        if (deletedScreen != null) {
            this.accessControl.getLogger().info("Maintenance screen " + name + " was successfully deleted");
        }

        return deletedScreen;
    }

    /**
     * Get the maintenance screen which is currently enabled
     * @return currently enabled maintenance screen
     */
    public MaintenanceScreen getEnabledMaintenanceScreen() {
        return this.getMaintenanceScreen(this.accessControl.getConfigManager().getConfig().optJSONObject("disconnectScreens", new JSONObject()).optString("maintenanceScreen", "default"));
    }

    /**
     * Get the finished maintenance screen string.
     * @return finished maintenance screen
     */
    public String generateMaintenanceScreen() {
        try {
            return this.getEnabledMaintenanceScreen().getMaintenanceScreen();
        } catch (Exception e) {
            try {
                this.accessControl.getLogger().warning("Exception in maintenance screen: EXCEPTION=" + e + ";STACKTRACE=" + Arrays.toString(e.getStackTrace()) + ";MESSAGE=" + e.getMessage());
                return MaintenanceManager.DEFAULT_MAINTENANCE_SCREEN.getMaintenanceScreen();
            } catch (Exception e2) {
                return "";
            }
        }
    }
}
