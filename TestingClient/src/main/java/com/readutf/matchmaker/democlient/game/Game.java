package com.readutf.matchmaker.democlient.game;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Game {

    private final UUID gameId;
    private final long expiresAt;

    public Game(UUID gameId, long expiresAt) {
        this.gameId = gameId;
        this.expiresAt = expiresAt;
    }
}
