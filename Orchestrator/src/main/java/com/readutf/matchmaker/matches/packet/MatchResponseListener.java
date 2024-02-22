package com.readutf.matchmaker.matches.packet;

import com.readutf.matchmaker.shared.match.MatchResponse;
import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.shared.packet.annotations.PacketHandler;
import com.readutf.matchmaker.shared.packet.packets.MatchResponsePacket;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class MatchResponseListener {

    private final MatchManager matchManager;

    @PacketHandler
    public void onMatchResponse(MatchResponsePacket packet) {

        MatchResponse response = packet.getMatchResponse();
        CompletableFuture<MatchResponse> future = matchManager.getMatchRequests().get(response.getRequestId());
        if(future == null) {
            return;
        }

        future.complete(response);
    }

}
