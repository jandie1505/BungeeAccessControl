package net.jandie1505.bungeeaccesscontrol.events;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.managers.data.Ban;
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
        if (this.accessControl.isLockdown()) {

            if (event.getPlayer().hasPermission(this.accessControl.getConfigManager().getConfig().optJSONObject("permissions", new JSONObject()).optString("bypassLockdown", "accesscontrol.bypass.lockdown"))) {
                event.getPlayer().sendMessage("§cAutomatic network lockdown active. You are bypassing.");
            } else {
                event.getPlayer().disconnect(this.accessControl.getConfigManager().getConfig().optJSONObject("disconnectScreens", new JSONObject()).optString("lockdownMessage", "This network is currently under lockdown"));
                return;
            }

        }

        if (this.accessControl.getMaintenanceManager().getMaintenanceStatus() && !event.getPlayer().hasPermission(this.accessControl.getConfigManager().getConfig().optJSONObject("permissions", new JSONObject()).optString("bypassMaintenance", "accesscontrol.bypass.maintenance"))) {
            event.getPlayer().disconnect(this.accessControl.getMaintenanceManager().generateMaintenanceScreen());
            return;
        }

        if (!event.getPlayer().hasPermission(this.accessControl.getConfigManager().getConfig().optJSONObject("permissions", new JSONObject()).optString("unbannable", "accesscontrol.unbannable"))) {
            Ban ban = this.accessControl.getBanManager().getLongestBan(event.getPlayer().getUniqueId());

            if (ban != null) {
                event.getPlayer().disconnect(this.accessControl.getBanManager().generateBanScreen(ban));
            }
        }

        if (this.accessControl.getConfigManager().getConfig().optBoolean("playerCaching", false)) {
            this.accessControl.getPlayerCacheManager().cachePlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        }
    }
}
