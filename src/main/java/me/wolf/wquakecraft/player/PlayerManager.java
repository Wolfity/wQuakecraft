package me.wolf.wquakecraft.player;

import java.util.*;

public class PlayerManager {

    private final Map<UUID, QuakePlayer> quakePlayers = new HashMap<>();


    public void createQuakePlayer(final UUID uuid) {
        quakePlayers.put(uuid, new QuakePlayer(uuid));

    }
    public void removeQuakePlayer(final UUID uuid) {
        quakePlayers.remove(uuid);
    }

    public QuakePlayer getQuakePlayer(final UUID uuid) {
        return quakePlayers.get(uuid);
    }

    public Map<UUID, QuakePlayer> getQuakePlayers() {
        return quakePlayers;
    }
}
