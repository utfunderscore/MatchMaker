package com.readutf.matchmaker.democlient;

import com.readutf.matchmaker.client.ErosClient;
import com.readutf.matchmaker.democlient.game.GameManager;
import com.readutf.matchmaker.democlient.game.GameRequestHandler;
import com.readutf.matchmaker.server.Server;

import java.util.HashMap;
import java.util.UUID;

public class DemoClient {

    private final ErosClient erosClient;
    private final GameManager gameManager;
    private final UUID serverId = UUID.randomUUID();

    public DemoClient(int maxGames) {

        this.gameManager = new GameManager(maxGames);
        this.erosClient = new ErosClient(() -> new Server(serverId, gameManager.getActiveGames().size(), maxGames, "",
                "demo_server", 1000, System.currentTimeMillis(), new HashMap<>()),
                new GameRequestHandler(gameManager));
    }
}

