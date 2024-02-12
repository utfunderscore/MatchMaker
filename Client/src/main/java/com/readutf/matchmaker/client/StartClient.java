package com.readutf.matchmaker.client;

import com.readutf.matchmaker.server.Server;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class StartClient {

    @SneakyThrows
    public static void main(String[] args) {


        UUID serverId1 = UUID.randomUUID();
        UUID serverId2 = UUID.randomUUID();
        UUID serverId3 = UUID.randomUUID();
        List<Server> servers = List.of(
                new Server(serverId1, 0, "localhost", "hub", 4254, System.currentTimeMillis(), new HashMap<>()),
                new Server(serverId2, 0, "localhost", "hub", 4254, System.currentTimeMillis(), new HashMap<>()),
                new Server(serverId3, 0, "localhost", "hub", 4254, System.currentTimeMillis(), new HashMap<>())
        );
        new ErosClient(() -> servers.stream().peek(server -> server.setActiveGames(ThreadLocalRandom.current().nextInt(0, 5))).toList());

    }
}