package net.jandie1505.bungeeaccesscontrol.database;

import net.jandie1505.bungeeaccesscontrol.database.data.BanData;

import java.util.List;
import java.util.UUID;

public interface DatabaseManager {
    List<BanData> getBans();
    List<BanData> getBans(UUID uuid);

    BanData getBan(long id);

    long addBan(UUID player, Long endTime, String reason, boolean cancelled, String additional);

    boolean deleteBan(long id);

    boolean clearBans(UUID player);

    boolean editBan(BanData banData);
}
