package com.readutf.matchmaker.matches;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.matches.api.MatchEndpoints;
import com.readutf.matchmaker.matches.packet.MatchResponseListener;
import com.readutf.matchmaker.packet.PacketManager;
import com.readutf.matchmaker.packet.packets.MatchRequestPacket;
import com.readutf.matchmaker.server.RegisteredServer;
import com.readutf.matchmaker.server.ServerManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class MatchManager {

    private final ServerManager serverManager;
    private final MatchEndpoints matchEndpoints;
    private final Map<UUID, CompletableFuture<MatchResponse>> matchRequests;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public MatchManager(PacketManager packetManager, ServerManager serverManager) {
        this.serverManager = serverManager;
        this.matchEndpoints = new MatchEndpoints(this);
        this.matchRequests = new HashMap<>();
        packetManager.registerListeners(new MatchResponseListener(this));
    }

    public CompletableFuture<MatchRequestResult> requestMatch(String queueId, List<List<UUID>> teams, int maxAttempts) {

        UUID requestId = UUID.randomUUID();
        Collection<RegisteredServer> servers = serverManager.getServers();
        Optional<RegisteredServer> optimalServer = servers.stream().filter(registeredServer -> registeredServer.getActiveGames() < registeredServer.getMaxGames()).min(Comparator.comparingDouble(value -> value.getActiveGames() / ((double) value.getMaxGames())));

        if(optimalServer.isEmpty()) {
            return CompletableFuture.completedFuture(new MatchRequestResult(null, null,
                    List.of(new MatchResponse(requestId, false, "No servers available."))));
        }

        RegisteredServer server = optimalServer.get();
        CompletableFuture<MatchRequestResult> responsesFuture = new CompletableFuture<>();

        executor.submit(() -> {

            List<MatchResponse> responses = new ArrayList<>();

            for (int i = 0; i < maxAttempts; i++) {
                MatchRequest matchRequest = new MatchRequest(UUID.randomUUID(), queueId, teams);
                CompletableFuture<MatchResponse> future = requestMatchFromServer(server, matchRequest);

                MatchResponse response = future.join();

                responses.add(response);
                if(response.isSuccessful()) {
                    responsesFuture.complete(new MatchRequestResult(server.getId(), matchRequest, responses));
                    return;
                }
            }
            responsesFuture.complete(new MatchRequestResult(null, null, responses));
        });

        return responsesFuture;
    }

    @NotNull
    private CompletableFuture<MatchResponse> requestMatchFromServer(RegisteredServer server, MatchRequest matchRequest) {
        server.getChannel().writeAndFlush(new MatchRequestPacket(matchRequest));
        CompletableFuture<MatchResponse> future = new CompletableFuture<>();
        matchRequests.put(matchRequest.getRequestId(), future);
        return future;
    }

}
