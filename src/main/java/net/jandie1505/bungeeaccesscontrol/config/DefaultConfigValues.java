package net.jandie1505.bungeeaccesscontrol.config;

import org.json.JSONArray;
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
        disconnectScreensConfig.put("banned", "You are permanently banned!\\nReason: {reason}\\nUntil: {until}\\nDuration: {duration}");
        config.put("disconnectScreens", disconnectScreensConfig);

        JSONObject permissionsConfig = new JSONObject();
        permissionsConfig.put("bypassLockdown", "accesscontrol.bypass.lockdown");
        permissionsConfig.put("bypassMaintenance", "accesscontrol.bypass.maintenance");
        permissionsConfig.put("unbannable", "accesscontrol.unbannable");
        config.put("permissions", permissionsConfig);

        JSONObject dateTimeConfig = new JSONObject();
        dateTimeConfig.put("datetime", "dd.MM.yyyy HH:mm:ss");
        dateTimeConfig.put("time", "HH:mm:ss");
        dateTimeConfig.put("date", "dd.MM.yyyy");
        dateTimeConfig.put("remaining", "{days}d, {hours}h, {minutes}m, {seconds}s");
        dateTimeConfig.put("permanentTime", "PERMANENT");
        config.put("dateTime", dateTimeConfig);

        JSONObject commandConfig = new JSONObject();
        commandConfig.put("command", "accesscontrol");
        JSONArray accessControlAliasesCommandConfig = new JSONArray();
        accessControlAliasesCommandConfig.put("ac");
        accessControlAliasesCommandConfig.put("bungeeaccesscontrol");
        commandConfig.put("aliases", accessControlAliasesCommandConfig);
        JSONObject permissionAccessControlCommandConfig = new JSONObject();
        permissionAccessControlCommandConfig.put("base", "accesscontrol.command");
        permissionAccessControlCommandConfig.put("ban", "accesscontrol.command.ban");
        permissionAccessControlCommandConfig.put("kick", "accesscontrol.command.kick");
        permissionAccessControlCommandConfig.put("maintenance", "accesscontrol.command.maintenance");
        permissionAccessControlCommandConfig.put("lockdown", "accesscontrol.command.lockdown");
        commandConfig.put("permissions", permissionAccessControlCommandConfig);
        config.put("command", commandConfig);

        JSONObject messagesConfig = new JSONObject();
        messagesConfig.put("noPermission", "No permission!");
        config.put("messages", messagesConfig);

        return config;
    }
}
