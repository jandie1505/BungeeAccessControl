package net.jandie1505.bungeeaccesscontrol.events;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.json.JSONObject;

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
    }
}
