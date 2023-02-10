package net.jandie1505.bungeeaccesscontrol.managers.data;

import net.jandie1505.bungeeaccesscontrol.database.data.BanData;
import net.md_5.bungee.api.plugin.Plugin;
import org.json.JSONObject;

import java.time.Instant;
import java.util.UUID;

public class Ban {
    private final long id;
    private UUID player;
    private Long endTime;
    private String reason;
    private boolean cancelled;
    private JSONObject additional;

    public Ban(long id, UUID player, Long endTime, String reason, boolean cancelled, JSONObject additional) {
        this.id = id;
        this.player = player;
        this.endTime = endTime;
        this.reason = reason;
        this.cancelled = cancelled;
        this.additional = additional;
    }

    public long getId() {
        return id;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public JSONObject getAdditionalData(String pluginName) {
        return this.additional.optJSONObject(pluginName, new JSONObject());
    }

    public void setAdditionalData(String pluginName, JSONObject additionalData) {
        this.additional.put(pluginName, additionalData);
    }

    public void deleteAdditionalData(String pluginName) {
        this.additional.remove(pluginName);
    }

    public JSONObject exportAdditional() {
        return this.additional;
    }

    /**
     * Returns if a ban is active or not
     * @return boolean
     */
    public boolean isActive() {
        return !this.cancelled && (this.endTime == null || this.endTime > Instant.now().getEpochSecond());
    }
}
