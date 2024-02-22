package com.readutf.matchmaker.matches;

import com.readutf.matchmaker.shared.match.MatchRequest;
import com.readutf.matchmaker.shared.match.MatchResponse;
import com.readutf.matchmaker.matches.packet.MatchResponseListener;
import com.readutf.matchmaker.shared.packet.PacketManager;
import com.readutf.matchmaker.shared.packet.packets.MatchRequestPacket;
import com.readutf.matchmaker.shared.queue.events.QueueResultEvent;
import com.readutf.matchmaker.server.RegisteredServer;
import com.readutf.matchmaker.shared.server.Server;
import com.readutf.matchmaker.server.ServerManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

@Getter
public class MatchManager {

    private final ServerManager serverManager;
    private final Map<UUID, CompletableFuture<MatchResponse>> matchRequests;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MatchManager(PacketManager packetManager, ServerManager serverManager) {
        this.serverManager = serverManager;
        this.matchRequests = new HashMap<>();
        packetManager.registerListeners(new MatchResponseListener(this));
    }

    public CompletableFuture<QueueResultEvent> requestMatch(String queueId, Predicate<Server> filter, List<List<UUID>> teams, int maxAttempts) {
        UUID requestId = UUID.randomUUID();
        Collection<RegisteredServer> servers = serverManager.getServers();
        CompletableFuture<QueueResultEvent> responsesFuture = new CompletableFuture<>();

        executor.submit(() -> {

            List<Server> alreadyTried = new ArrayList<>();

            List<MatchResponse> responses = new ArrayList<>();

            for (int i = 0; i < maxAttempts; i++) {

                Optional<RegisteredServer> optimalServer = findOptimalServer(filter, servers, alreadyTried);

                // If no servers are available, return a failed response
                if (optimalServer.isEmpty()) {
                    responsesFuture.completeExceptionally(new IllegalStateException("No servers available"));
                    return;
                }
                RegisteredServer server = optimalServer.get();

                if (requestServer(queueId, teams, server, requestId, responses, responsesFuture)) return;
            }
            responsesFuture.complete(new QueueResultEvent(queueId, null, null, responses));
        });

        return responsesFuture;
    }

    @NotNull
    private static Optional<RegisteredServer> findOptimalServer(Predicate<Server> filter, Collection<RegisteredServer> servers, List<Server> alreadyTried) {
        // Find the server with the lowest load percentage and that matches the filter
        Optional<RegisteredServer> optimalServer = servers.stream()
                .filter(registeredServer -> !alreadyTried.contains(registeredServer))
                .filter(registeredServer -> registeredServer.getActiveGames() < registeredServer.getMaxGames())
                .filter(filter)
                .min(Comparator.comparingDouble(Server::getLoadPercentage));

        if (optimalServer.isEmpty() && !alreadyTried.isEmpty()) {
            // If no server is found, try again with the servers that were already tried
            return findOptimalServer(filter, servers, new ArrayList<>());
        }

        return optimalServer;
    }

    private boolean requestServer(String queueId, List<List<UUID>> teams,
                                  RegisteredServer server, UUID requestId, List<MatchResponse> responses,
                                  CompletableFuture<QueueResultEvent> responsesFuture) {

        MatchRequest matchRequest = new MatchRequest(UUID.randomUUID(), queueId, teams);
        server.getChannel().writeAndFlush(new MatchRequestPacket(matchRequest));
        CompletableFuture<MatchResponse> future = new CompletableFuture<>();
        matchRequests.put(matchRequest.getRequestId(), future);

        MatchResponse response = null;
        try {
            response = future.get(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            response = MatchResponse.failure(requestId, "Failed to get response from server");
        }

        responses.add(response);
        if (response.isSuccessful()) {
            server.setActiveGames(server.getActiveGames() + 1);
            responsesFuture.complete(new QueueResultEvent(queueId, server, matchRequest, responses));
            return true;
        }
        return false;
    }

}
