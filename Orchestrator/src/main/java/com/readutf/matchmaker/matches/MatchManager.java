package com.readutf.matchmaker.matches;

import com.readutf.matchmaker.matches.packet.MatchResponseListener;
import com.readutf.matchmaker.server.RegisteredServer;
import com.readutf.matchmaker.server.ServerManager;
import com.readutf.matchmaker.shared.match.MatchRequest;
import com.readutf.matchmaker.shared.match.MatchResponse;
import com.readutf.matchmaker.shared.packet.PacketManager;
import com.readutf.matchmaker.shared.packet.packets.MatchRequestPacket;
import com.readutf.matchmaker.shared.queue.events.QueueResultEvent;
import com.readutf.matchmaker.shared.server.Server;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

@Getter
public class MatchManager {

    private static final Logger logger = LoggerFactory.getLogger(MatchManager.class);

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

                logger.info("Attempting to find a server for queue %s (attempt #%d)".formatted(queueId, i + 1));

                Optional<RegisteredServer> optimalServer = findOptimalServer(filter, servers, alreadyTried);

                // If no servers are available, return a failed response
                if (optimalServer.isEmpty()) {
                    responsesFuture.completeExceptionally(new IllegalStateException("No servers available"));
                    logger.warn("No servers available for queue %s".formatted(queueId));
                    return;
                }
                RegisteredServer server = optimalServer.get();

                QueueResultEvent queueResultEvent = requestServer(queueId, teams, server, requestId, responses);
                if (queueResultEvent != null) {
                    responsesFuture.complete(queueResultEvent);
                    return;
                } else alreadyTried.add(server);
            }


            logger.warn("Failed to find a server for queue %s after %d attempts".formatted(queueId, maxAttempts));
        });

        return responsesFuture;
    }

    @NotNull
    private static Optional<RegisteredServer> findOptimalServer(Predicate<Server> filter, Collection<RegisteredServer> servers, List<Server> alreadyTried) {
        // Find the server with the lowest load percentage and that matches the filter


        Optional<RegisteredServer> optimalServer = servers.stream()
                .filter(registeredServer -> {
                    boolean tried = !alreadyTried.contains(registeredServer);
                    System.out.println("alreadyTried: " + tried);
                    return tried;
                })
                .filter(registeredServer -> {
                    boolean tooManyGames = registeredServer.getActiveGames() < registeredServer.getMaxGames();
                    System.out.println("tooManyGames: " + tooManyGames);
                    return tooManyGames;
                })
                .filter(filter)
                .min(Comparator.comparingDouble(Server::getLoadPercentage));

        if (optimalServer.isEmpty() && !alreadyTried.isEmpty()) {
            // If no server is found, try again with the servers that were already tried
            return findOptimalServer(filter, servers, new ArrayList<>());
        }

        return optimalServer;
    }

    private QueueResultEvent requestServer(String queueId, List<List<UUID>> teams,
                                           RegisteredServer server, UUID requestId, List<MatchResponse> responses) {

        MatchRequest matchRequest = new MatchRequest(UUID.randomUUID(), queueId, teams);
        server.getChannel().writeAndFlush(new MatchRequestPacket(matchRequest));
        CompletableFuture<MatchResponse> future = new CompletableFuture<>();
        matchRequests.put(matchRequest.getRequestId(), future);

        MatchResponse response;
        long start = System.currentTimeMillis();
        try {
            response = future.get(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            response = MatchResponse.failure(requestId, "Failed to get response from server");
        }
        long end = System.currentTimeMillis();
        logger.debug("Match request took %dms".formatted(end - start));

        responses.add(response);
        if (response.isSuccessful()) {
            server.setActiveGames(server.getActiveGames() + 1);
            return new QueueResultEvent(queueId, server, matchRequest, responses);
        }
        return null;
    }

}
