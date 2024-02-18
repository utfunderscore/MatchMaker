package com.readutf.matchmaker.matches.api;

import com.google.gson.reflect.TypeToken;
import com.readutf.matchmaker.api.annotation.PUT;
import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.matches.MatchRequestResult;
import com.readutf.matchmaker.server.ServerManager;
import com.readutf.matchmaker.utils.JavalinUtils;
import io.javalin.http.Handler;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MatchEndpoints {

    private final MatchManager matchManager;

    public MatchEndpoints(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @PUT
    public MatchRequestResult createMatch(String queueId, List<List<UUID>> teams, int maxAttempts) {
        CompletableFuture<MatchRequestResult> matchFuture = matchManager.requestMatch(queueId,server -> true, teams, maxAttempts);
        return matchFuture.join();
    }

}
