package net.jandie1505.bungeeaccesscontrol.config;

import org.json.JSONObject;

public final class DefaultConfigValues {

    private DefaultConfigValues() {}

    public static JSONObject getConfig() {
        JSONObject config = new JSONObject();

        JSONObject mysqlConfig = new JSONObject();
        mysqlConfig.put("host", "");
        mysqlConfig.put("port", 3306);
        mysqlConfig.put("username", "");
        mysqlConfig.put("password", "");
        mysqlConfig.put("database", "bungeeaccesscontrol");
        config.put("mysql", mysqlConfig);

        JSONObject disconnectScreensConfig = new JSONObject();
        disconnectScreensConfig.put("lockdown", "This network is currently under lockdown");
        disconnectScreensConfig.put("maintenance", "This network is currently under maintenance");
        disconnectScreensConfig.put("bannedPermanently", "You are permanently banned!\nReason: {reason}");
        disconnectScreensConfig.put("bannedTemporary", "You are temporarily banned!\nReason: {reason}\nUntil: {until}");
        config.put("disconnectScreens", disconnectScreensConfig);

        JSONObject permissionsConfig = new JSONObject();
        permissionsConfig.put("bypassLockdown", "accesscontrol.lockdown");
        permissionsConfig.put("bypassMaintenance", "accesscontrol.maintenance");
        permissionsConfig.put("unbannable", "accesscontrol.unbannable");
        config.put("permissions", permissionsConfig);

        return config;
    }
}
