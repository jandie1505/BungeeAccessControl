package net.jandie1505.bungeeaccesscontrol.database.data;

import java.util.UUID;

public class BanData {
    private final long id;
    private UUID player;
    private Long endTime;
    private String reason;
    private boolean cancelled;
    private String additional;

    public BanData(long id, UUID player, Long endTime, String reason, boolean cancelled, String additional) {
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

    public Long getEndTime() {
        return endTime;
    }

    public String getReason() {
        return reason;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getAdditional() {
        return additional;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }
}
