package com.readutf.matchmaker.matches;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.matches.api.MatchEndpoints;
import com.readutf.matchmaker.matches.packet.MatchResponseListener;
import com.readutf.matchmaker.packet.PacketManager;
import com.readutf.matchmaker.packet.packets.MatchRequestPacket;
import com.readutf.matchmaker.packet.packets.MatchResponsePacket;
import com.readutf.matchmaker.server.RegisteredServer;
import com.readutf.matchmaker.server.ServerManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class MatchManager {

    private final ServerManager serverManager;
    private final MatchEndpoints matchEndpoints;
    private final Map<UUID, CompletableFuture<MatchResponse>> matchRequests;

    public MatchManager(PacketManager packetManager, ServerManager serverManager) {
        this.serverManager = serverManager;
        this.matchEndpoints = new MatchEndpoints(this);
        this.matchRequests = new HashMap<>();
        packetManager.registerListeners(new MatchResponseListener(this));
    }

    public CompletableFuture<MatchResponse> requestMatch(String queueId, List<List<UUID>> teams) {

        UUID requestId = UUID.randomUUID();
        Collection<RegisteredServer> servers = serverManager.getServers();
        Optional<RegisteredServer> optimalServer = servers.stream().filter(registeredServer -> registeredServer.getActiveGames() < registeredServer.getMaxGames()).min(Comparator.comparingDouble(value -> value.getActiveGames() / ((double) value.getMaxGames())));

        if(optimalServer.isEmpty()) {
            return CompletableFuture.completedFuture(new MatchResponse(requestId, false, "No servers available."));
        }

        RegisteredServer server = optimalServer.get();
        MatchRequest matchRequest = new MatchRequest(requestId, queueId, teams);

        server.getChannel().writeAndFlush(new MatchRequestPacket(matchRequest));

        CompletableFuture<MatchResponse> future = new CompletableFuture<>();
        matchRequests.put(requestId, future);
        return future;
    }

}
