package com.readutf.matchmaker.democlient;

import com.readutf.matchmaker.client.ErosClient;
import com.readutf.matchmaker.democlient.game.GameManager;
import com.readutf.matchmaker.democlient.game.GameRequestHandler;
import com.readutf.matchmaker.server.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DemoClient {

    private final ErosClient erosClient;
    private final GameManager gameManager;
    private final UUID serverId = UUID.randomUUID();

    public DemoClient(int maxGames) {

        this.gameManager = new GameManager(maxGames);
        this.erosClient = new ErosClient(() -> new Server(
                serverId,
                "localhost",
                8080,
                "hub",
                gameManager.getMatchData(),
                new HashMap<>(),
                System.currentTimeMillis(),
                0,
                maxGames
        ), new GameRequestHandler(gameManager));
    }
}

