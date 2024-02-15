package com.readutf.matchmaker.democlient.game;

import com.readutf.matchmaker.match.MatchResponse;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager extends TimerTask {

    private final int maxGames;
    private final Map<UUID, Game> games;

    public GameManager(int maxGames) {
        this.maxGames = maxGames;
        this.games = new HashMap<>(maxGames);
        new Timer().scheduleAtFixedRate(this, 0, 1000);
    }

    public List<Game> getActiveGames() {
        return new ArrayList<>(games.values());
    }

    public Game startGame() throws Exception {
        UUID gameId = UUID.randomUUID();
        System.out.println("active games: " + games.size() + " max games: " + maxGames);
        if (games.size() >= maxGames) {
            throw new Exception("Max games reached");
        }

        Game game = new Game(gameId, System.currentTimeMillis() + (ThreadLocalRandom.current().nextLong(0, 3) * 1000 * 60));
        games.put(gameId, game);
        return game;
    }

    @Override
    public void run() {
        HashMap<UUID, Game> games = new HashMap<>(this.games);
        games.forEach((uuid, game) -> {
            if (game.getExpiresAt() < System.currentTimeMillis()) {
                this.games.remove(uuid);
            }
        });
    }
}
