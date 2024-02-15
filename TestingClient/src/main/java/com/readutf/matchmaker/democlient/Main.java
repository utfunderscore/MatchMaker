package com.readutf.matchmaker.democlient;

import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {

        int totalServers = 0;

        for (int i = 0; i < 1; i++) {
            int maxGames = ThreadLocalRandom.current().nextInt(1, 20);
            totalServers += maxGames;
            new DemoClient(maxGames);
        }

        System.out.println(totalServers);

    }
}