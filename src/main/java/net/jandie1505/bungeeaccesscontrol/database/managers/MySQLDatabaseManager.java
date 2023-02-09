package net.jandie1505.bungeeaccesscontrol.database.managers;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.database.DatabaseManager;
import net.jandie1505.bungeeaccesscontrol.database.data.BanData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MySQLDatabaseManager implements DatabaseManager {
    private final AccessControl accessControl;
    private Connection connection;

    public MySQLDatabaseManager(AccessControl accessControl) {
        this.accessControl = accessControl;

        try {

        } catch (SQLException e) {
            this.errorHandler(e);
        }
    }

    private void errorHandler(Exception e) {
        this.accessControl.setLockdown(true);
        this.accessControl.getLogger().warning("MySQL Exception: EXCEPTION=" + e + ";STACKTRACE=" + Arrays.toString(e.getStackTrace()) + ";MESSAGE=" + e.getMessage() + ";");
    }

    @Override
    public List<BanData> getBans() {
        return null;
    }

    @Override
    public List<BanData> getBans(UUID uuid) {
        return null;
    }

    @Override
    public BanData getBan(long id) {
        return null;
    }

    @Override
    public long addBan(UUID player, Long endTime, String reason, String additional) {
        return 0;
    }

    @Override
    public boolean deleteBan(long id) {
        return false;
    }

    @Override
    public boolean clearBans(UUID player) {
        return false;
    }

    @Override
    public boolean editBan(BanData banData) {
        return false;
    }
}
