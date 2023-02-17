package net.jandie1505.bungeeaccesscontrol.database.managers;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.database.DatabaseManager;
import net.jandie1505.bungeeaccesscontrol.database.data.BanData;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;

public class MySQLDatabaseManager implements DatabaseManager {
    private final AccessControl accessControl;
    private Connection connection;

    public MySQLDatabaseManager(AccessControl accessControl) {
        this.accessControl = accessControl;

        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://"
                            + this.accessControl.getConfigManager().getConfig().optJSONObject("mysql", new JSONObject()).optString("host", "")
                            + ":"
                            + this.accessControl.getConfigManager().getConfig().optJSONObject("mysql", new JSONObject()).optInt("port", 3306)
                            + "/"
                            + this.accessControl.getConfigManager().getConfig().optJSONObject("mysql", new JSONObject()).optString("database", "bungeeaccesscontrol"),
                    this.accessControl.getConfigManager().getConfig().optJSONObject("mysql", new JSONObject()).optString("username", ""),
                    this.accessControl.getConfigManager().getConfig().optJSONObject("mysql", new JSONObject()).optString("password", "")
            );

            this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS cache (" +
                            "uuid VARCHAR(255) PRIMARY KEY NOT NULL," +
                            "name VARCHAR(255)" +
                            ");"
            ).execute();

            this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS bans (" +
                            "id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                            "player VARCHAR(255) NOT NULL," +
                            "endTime BIGINT," +
                            "reason VARCHAR(255)," +
                            "cancelled BOOLEAN NOT NULL DEFAULT false," +
                            "additional VARCHAR(1000)" +
                            ");"
            ).execute();
        } catch (Exception e) {
            this.errorHandler(e);
        }
    }

    private void errorHandler(Exception e) {
        this.accessControl.setLockdown(true);
        this.accessControl.getLogger().warning("DatabaseManager Exception: EXCEPTION=" + e + ";STACKTRACE=" + Arrays.toString(e.getStackTrace()) + ";MESSAGE=" + e.getMessage() + ";");
    }

    private List<BanData> createBanData(ResultSet rs) throws SQLException {
        List<BanData> returnList = new ArrayList<>();

        while (rs.next()) {
            long id = rs.getLong("id");
            UUID player = UUID.fromString(rs.getString("player"));

            Long endTime = rs.getLong("endTime");

            if (rs.wasNull()) {
                endTime = null;
            }

            String reason = null;

            if (rs.getString("reason") != null) {
                reason = rs.getString("reason");
            }

            boolean cancelled = rs.getBoolean("cancelled");

            String additional = null;

            if (rs.getString("additional") != null) {
                additional = rs.getString("additional");
            }

            returnList.add(new BanData(id, player, endTime, reason, cancelled, additional));
        }

        return List.copyOf(returnList);
    }

    // PLAYER CACHE

    @Override
    public Map<UUID, String> getCachedPlayers() {
        Map<UUID, String> returnMap = new HashMap<>();

        try {

            ResultSet rs = this.connection.prepareStatement("SELECT * FROM cache;").executeQuery();

            while (rs.next()) {
                try {
                    returnMap.put(UUID.fromString(rs.getString("uuid")), rs.getString("name"));
                } catch (IllegalArgumentException ignored) {
                    // ignored
                }
            }

        } catch (Exception e) {
            this.errorHandler(e);
        }

        return Map.copyOf(returnMap);
    }

    @Override
    public String getCachedPlayerName(UUID uuid) {
        try {

            PreparedStatement statement = this.connection.prepareStatement("SELECT name FROM cache WHERE uuid = ?;");
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            } else {
                return null;
            }

        } catch (Exception e) {
            this.errorHandler(e);
            return null;
        }
    }

    @Override
    public UUID getCachedPlayerUUID(String name) {
        try {

            PreparedStatement statement = this.connection.prepareStatement("SELECT name FROM cache WHERE name = ?;");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                try {
                    return UUID.fromString(rs.getString("uuid"));
                } catch (IllegalArgumentException e) {
                    return null;
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            this.errorHandler(e);
            return null;
        }
    }

    @Override
    public boolean cachePlayer(UUID uuid, String name) {
        try {

            PreparedStatement statement1 = this.connection.prepareStatement("SELECT * FROM cache WHERE uuid = ? OR player = ?;");
            statement1.setString(1, uuid.toString());
            statement1.setString(2, name);
            ResultSet rs1 = statement1.executeQuery();

            Map<UUID, String> cachedEntries = new HashMap<>();
            while (rs1.next()) {
                try {
                    cachedEntries.put(UUID.fromString(rs1.getString("uuid")), rs1.getString("name"));
                } catch (IllegalArgumentException ignored) {
                    // ignored
                }
            }

            for (UUID cachedUUID : Map.copyOf(cachedEntries).keySet()) {

                if (!cachedUUID.equals(uuid)) {
                    PreparedStatement statement2 = this.connection.prepareStatement("DELETE FROM cache WHERE uuid = ?;");
                    statement2.setString(1, cachedUUID.toString());
                    statement2.executeUpdate();
                }

                if (!cachedEntries.get(cachedUUID).equals(name)) {
                    PreparedStatement statement2 = this.connection.prepareStatement("DELETE FROM cache WHERE uuid = ?;");
                    statement2.setString(1, cachedUUID.toString());
                    statement2.executeUpdate();
                }

            }

            PreparedStatement statement2 = this.connection.prepareStatement("INSERT INTO cache (uuid, name) VALUES (?,?);");
            statement2.setString(1, uuid.toString());
            statement2.setString(2, name);
            return statement2.executeUpdate() != 0;

        } catch (Exception e) {
            this.errorHandler(e);
            return false;
        }
    }

    @Override
    public boolean deleteCachedPlayer(UUID uuid) {
        try {

            PreparedStatement statement = this.connection.prepareStatement("DELETE FROM cache WHERE uuid = ?;");
            statement.setString(1, uuid.toString());
            return statement.executeUpdate() != 0;

        } catch (Exception e) {
            this.errorHandler(e);
            return false;
        }
    }

    @Override
    public boolean clearCachedPlayers() {
        try {

            return this.connection.prepareStatement("DELETE FROM cache;").executeUpdate() != 0;

        } catch (Exception e) {
            this.errorHandler(e);
            return false;
        }
    }

    // BANS

    @Override
    public List<BanData> getBans() {
        try {
            return this.createBanData(this.connection.prepareStatement("SELECT * FROM bans;").executeQuery());
        } catch (Exception e) {
            this.errorHandler(e);
        }

        return List.of();
    }

    @Override
    public List<BanData> getBans(UUID uuid) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM bans WHERE player = ?;");
            statement.setString(1, uuid.toString());
            return this.createBanData(statement.executeQuery());
        } catch (Exception e) {
            this.errorHandler(e);
        }

        return List.of();
    }

    @Override
    public BanData getBan(long id) {
        List<BanData> returnList = new ArrayList<>();

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM bans WHERE id = ?;");
            statement.setLong(1, id);
            returnList.addAll(this.createBanData(statement.executeQuery()));
        } catch (Exception e) {
            this.errorHandler(e);
        }

        if (!returnList.isEmpty()) {
            return returnList.get(0);
        }

        return null;
    }

    @Override
    public long addBan(UUID player, Long endTime, String reason, String additional) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO bans (player, endTime, reason, additional)" +
                            "VALUES (?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            if (player == null) {
                return -1;
            }

            statement.setString(1, player.toString());

            if (endTime != null) {
                statement.setLong(2, endTime);
            } else {
                statement.setNull(2, Types.BIGINT);
            }

            if (reason != null) {
                statement.setString(3, reason);
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            if (additional != null) {
                statement.setString(4, additional);
            } else {
                statement.setNull(4, Types.VARCHAR);
            }

            if (statement.executeUpdate() != 0) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            this.errorHandler(e);
            return -1;
        }
    }

    @Override
    public boolean deleteBan(long id) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("DELETE FROM bans WHERE id = ?;");
            statement.setLong(1, id);
            return statement.executeUpdate() != 0;
        } catch (Exception e) {
            this.errorHandler(e);
            return false;
        }
    }

    @Override
    public boolean clearBans(UUID uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM bans WHERE player = ?");
            statement.setString(1, uuid.toString());
            return statement.executeUpdate() != 0;
        } catch (Exception e) {
            this.errorHandler(e);
            return false;
        }
    }

    @Override
    public boolean editBan(BanData banData) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE bans SET player = ?, endTime = ?, reason = ?, cancelled = ?, additional = ? WHERE id = ?;"
            );

            if (banData.getPlayer() == null) {
                return false;
            }

            statement.setString(1, banData.getPlayer().toString());

            if (banData.getEndTime() != null) {
                statement.setLong(2, banData.getEndTime());
            } else {
                statement.setNull(2, Types.BIGINT);
            }

            if (banData.getReason() != null) {
                statement.setString(3, banData.getReason());
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            statement.setBoolean(4, banData.isCancelled());

            if (banData.getAdditional() != null) {
                statement.setString(5, banData.getAdditional());
            } else {
                statement.setNull(5, Types.VARCHAR);
            }

            statement.setLong(6, banData.getId());

            return statement.executeUpdate() != 0;
        } catch (Exception e) {
            this.errorHandler(e);
            return false;
        }
    }
}
