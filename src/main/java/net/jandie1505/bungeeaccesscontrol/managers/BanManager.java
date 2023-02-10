package net.jandie1505.bungeeaccesscontrol.managers;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.database.data.BanData;
import net.jandie1505.bungeeaccesscontrol.managers.data.Ban;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanManager {
    private final AccessControl accessControl;

    public BanManager(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    /**
     * Ban a player.
     * @param playerId the player that should be banned (null will return -1 and do nothing)
     * @param endTime the timestamp the player should be unbanned (null for permanent)
     * @param reason the reason the player should be banned for (null for no reason)
     * @return ban id
     */
    public long banPlayer(UUID playerId, Long endTime, String reason, JSONObject additionalInfos) {
        if (playerId == null) {
            return -1;
        }

        if (additionalInfos == null) {
            additionalInfos = new JSONObject();
        }

        long banId = this.accessControl.getDatabaseManager().addBan(playerId, endTime, reason, additionalInfos.toString());

        ProxiedPlayer proxiedPlayer = this.accessControl.getProxy().getPlayer(playerId);

        if (proxiedPlayer != null) {
            BanData banData = this.accessControl.getDatabaseManager().getBan(banId);

            if (banData != null) {
                proxiedPlayer.disconnect(this.accessControl.getConfigManager().getConfig().optJSONObject("disconnectScreens", new JSONObject()).optString("lockdown", "This network is currently under maintenance"));
            } else {
                proxiedPlayer.disconnect("You have been banned.\nThere was an error to get additional information.");
            }
        }

        return banId;
    }

    /**
     * Unbans a player.
     * This method will set one specific ban cancelled.
     * This will not delete the ban.
     * If there is more than one active ban for a player, the player will not be unbanned.
     * @param banId ban id (not player)
     * @return success
     */
    public boolean unbanId(long banId) {
        BanData banData = this.accessControl.getDatabaseManager().getBan(banId);

        if (banData == null) {
            return false;
        }

        banData.setCancelled(true);

        return this.accessControl.getDatabaseManager().editBan(banData);
    }

    /**
     * Unbans a player.
     * This method will set ALL BANS of the specified player cancelled.
     * This will not delete the bans.
     * @param playerId player UUID
     * @return success
     */
    public boolean unbanPlayer(UUID playerId) {
        List<BanData> bans = this.accessControl.getDatabaseManager().getBans(playerId);

        if (bans == null || bans.isEmpty()) {
            return false;
        }

        for (BanData banData : bans) {
            this.unbanId(banData.getId());
        }

        return true;
    }

    /**
     * Get a ban by id.
     * @param id ban id (not player uuid)
     * @return Ban
     */
    public Ban getBan(long id) {
        BanData banData = this.accessControl.getDatabaseManager().getBan(id);

        if (banData == null) {
            return null;
        }

        JSONObject additionalInfo;

        try {
            if (banData.getAdditional() != null) {
                additionalInfo = new JSONObject(banData.getAdditional());
            } else {
                additionalInfo = new JSONObject();
            }
        } catch (JSONException e) {
            additionalInfo = new JSONObject();
        }

        return new Ban(banData.getId(), banData.getPlayer(), banData.getEndTime(), banData.getReason(), banData.isCancelled(), additionalInfo);
    }

    /**
     * Get a list of all bans.
     * @return list of all bans
     */
    public List<Ban> getBans() {
        List<BanData> banDataList = this.accessControl.getDatabaseManager().getBans();
        List<Ban> banList = new ArrayList<>();

        for (BanData banData : banDataList) {
            banList.add(this.getBan(banData.getId()));
        }

        return List.copyOf(banList);
    }
    /**
     * Get a list of all bans of a specific player.
     * @param playerId player UUID
     * @return list of bans
     */
    public List<Ban> getBans(UUID playerId) {
        List<BanData> banDataList = this.accessControl.getDatabaseManager().getBans(playerId);
        List<Ban> banList = new ArrayList<>();

        for (BanData banData : banDataList) {
            banList.add(this.getBan(banData.getId()));
        }

        return List.copyOf(banList);
    }

    /**
     * Edit a ban.
     * @param ban ban object
     * @return success
     */
    public boolean editBan(Ban ban) {
        BanData banData = this.accessControl.getDatabaseManager().getBan(ban.getId());

        if (ban.getPlayer() == null) {
            return false;
        }

        banData.setPlayer(ban.getPlayer());
        banData.setEndTime(ban.getEndTime());
        banData.setReason(ban.getReason());
        banData.setCancelled(ban.isCancelled());
        banData.setAdditional(ban.exportAdditional().toString());

        return this.accessControl.getDatabaseManager().editBan(banData);
    }

    /**
     * Delete a ban.
     * @param id ban id
     * @return success
     */
    public boolean deleteBan(long id) {
        return this.accessControl.getDatabaseManager().deleteBan(id);
    }

    /**
     * Delete all bans of a specific player.
     * @param player player UUID
     * @return success
     */
    public boolean deleteBans(UUID player) {
        return this.accessControl.getDatabaseManager().clearBans(player);
    }
}
