package net.jandie1505.bungeeaccesscontrol.managers;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.UUID;

public class PlayerCacheManager {
    private final AccessControl accessControl;

    public PlayerCacheManager(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    /**
     * Get the UUID of a cached player name
     * @param name player name
     * @return cached uuid of the specified name (null if not cached)
     */
    public UUID getCachedPlayer(String name) {
        Map<UUID, String> cachedPlayers = this.accessControl.getDatabaseManager().getCachedPlayers(name);

        if (cachedPlayers.size() == 1) {
            for (UUID uuid : cachedPlayers.keySet()) {
                if (cachedPlayers.get(uuid).equals(name)) {
                    return uuid;
                }
            }
        }

        return null;
    }

    /**
     * Get the player name of a cached uuid
     * @param uuid uuid
     * @return cached player name (null if not cached)
     */
    public String getCachedPlayer(UUID uuid) {
        return this.accessControl.getDatabaseManager().getCachedPlayers(uuid);
    }

    /**
     * Get a map of all cached uuids and their names
     * @return map of uuids and player names
     */
    public Map<UUID, String> getCachedPlayers() {
        return Map.copyOf(this.accessControl.getDatabaseManager().getCachedPlayers());
    }

    /**
     * This method is for player input fields.
     * 1. If the input is a valid UUID, this UUID will be returned (else continue at 2).
     * 2. If the input is a name of an online player, the UUID of that player will be returned (else continue at 3).
     * 3. If the input is a name of a cached player name, the UUID of that player will be returned (else continue at 4).
     * 4. Null will be returned.
     * @param string player name or UUID
     * @return UUID or null (see description)
     */
    public UUID getPlayerUUIDFromString(String string) {
        try {
            return UUID.fromString(string);
        } catch (IllegalArgumentException e) {
            ProxiedPlayer player = this.accessControl.getProxy().getPlayer(string);

            if (player != null) {
                return player.getUniqueId();
            } else {
                return this.getCachedPlayer(string);
            }
        }
    }

    /**
     * Cache a player.
     * @param uuid player UUID
     * @param name player name
     * @return success
     */
    public boolean cachePlayer(UUID uuid, String name) {
        String nameBefore = this.accessControl.getDatabaseManager().getCachedPlayers(uuid);

        if (nameBefore != null) {
            if (!this.accessControl.getDatabaseManager().deleteCachedPlayer(uuid)) {
                return false;
            }
        }

        Map<UUID, String> uuidsBefore = this.accessControl.getDatabaseManager().getCachedPlayers(name);

        for (UUID iuuid : uuidsBefore.keySet()) {
            if (!this.accessControl.getDatabaseManager().deleteCachedPlayer(iuuid)) {
                return false;
            }
        }

        return this.accessControl.getDatabaseManager().cachePlayer(uuid, name);
    }

    /**
     * Delete a cached player by uuid.
     * @param uuid cached uuid
     * @return success
     */
    public boolean deleteCachedPlayer(UUID uuid) {
        return this.accessControl.getDatabaseManager().deleteCachedPlayer(uuid);
    }

    /**
     * Delete all cached players
     * @return success
     */
    public boolean clearCachedPlayers() {
        return this.accessControl.getDatabaseManager().clearCachedPlayers();
    }
}
