package net.jandie1505.bungeeaccesscontrol.events;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.database.data.BanData;
import net.jandie1505.bungeeaccesscontrol.managers.data.Ban;
import net.jandie1505.bungeeaccesscontrol.utilities.Utilities;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EventListener implements Listener {
    private final AccessControl accessControl;

    public EventListener(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLogin(PostLoginEvent event) {
        if (this.accessControl.isLockdown() && !event.getPlayer().hasPermission(this.accessControl.getConfigManager().getConfig().optJSONObject("permissions", new JSONObject()).optString("bypassLockdown", "accesscontrol.lockdown"))) {
            event.getPlayer().disconnect(this.accessControl.getConfigManager().getConfig().optJSONObject("disconnectScreens", new JSONObject()).optString("lockdown", "This network is currently under lockdown"));
        }

        if (this.accessControl.isMaintenance() && !event.getPlayer().hasPermission(this.accessControl.getConfigManager().getConfig().optJSONObject("permissions", new JSONObject()).optString("bypassMaintenance", "accesscontrol.maintenance"))) {
            event.getPlayer().disconnect(this.accessControl.getConfigManager().getConfig().optJSONObject("disconnectScreens", new JSONObject()).optString("lockdown", "This network is currently under maintenance"));
        }

        if (!event.getPlayer().hasPermission(this.accessControl.getConfigManager().getConfig().optJSONObject("permissions", new JSONObject()).optString("unbannable", "accesscontrol.unbannable"))) {
            List<Ban> activeBans = new ArrayList<>(this.accessControl.getBanManager().getActiveBans(event.getPlayer().getUniqueId()));

            if (!activeBans.isEmpty()) {
                activeBans.sort(null);

                Ban usedBan = activeBans.get(activeBans.size() - 1);

                try {
                    String untilString;
                    String durationString;

                    if (usedBan.getEndTime() == null) {
                        untilString = this.accessControl.getConfigManager().getConfig().optJSONObject("dateTime", new JSONObject()).optString("permanentTime", "permanent");
                        durationString = this.accessControl.getConfigManager().getConfig().optJSONObject("dateTime", new JSONObject()).optString("permanentTime", "permanent");
                    } else {
                        untilString = Utilities.createTimeString(usedBan.getEndTime(), this.accessControl.getConfigManager().getConfig().optJSONObject("dateTime", new JSONObject()).optString("datetime", "dd.MM.yyyy HH:mm:ss"));
                        durationString = Utilities.createRemainingTime((usedBan.getEndTime() - Instant.now().getEpochSecond()), this.accessControl.getConfigManager().getConfig().optJSONObject("dateTime", new JSONObject()).optString("remaining", "{days}d, {hours}h, {minutes}m, {seconds}s"));
                    }

                    event.getPlayer().disconnect(Utilities.replacePlaceholders(
                            this.accessControl.getConfigManager().getConfig().optJSONObject("disconnectScreens", new JSONObject()).optString("permanentlyBanned", "You are permanently banned\n--- CANNOT GET BAN SCREEN ---"),
                            Map.of(
                                    "reason", usedBan.getReason(),
                                    "until", untilString,
                                    "duration", durationString
                            )
                    ));
                } catch (Exception e) {
                    event.getPlayer().disconnect("You have been banned.\nThere was an error to get additional information.");
                }
            }
        }
    }
}
