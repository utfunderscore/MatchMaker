package com.readutf.matchmaker.client;

import com.readutf.matchmaker.server.Server;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ErosDemo {

    public static void main(String[] args) {


        List<Server> servers = List.of(new Server(UUID.randomUUID(), 0, 10, "test", "test", 0,
                System.currentTimeMillis(), new HashMap<>()));

        new ErosClient(() -> servers,
                (matchRequest) -> {

                });
    }

}
