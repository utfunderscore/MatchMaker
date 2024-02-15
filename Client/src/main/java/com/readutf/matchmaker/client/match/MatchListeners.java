package com.readutf.matchmaker.client.match;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.packet.PacketManager;
import com.readutf.matchmaker.packet.annotations.PacketHandler;
import com.readutf.matchmaker.packet.packets.MatchRequestPacket;
import com.readutf.matchmaker.packet.packets.MatchResponsePacket;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class MatchListeners {

    private final MatchRequestHandler matchRequestHandler;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PacketHandler
    public void onMatchRequest(Channel channel, MatchRequestPacket packet) {

        executorService.submit(() -> {
            MatchResponse response = matchRequestHandler.handleMatchRequest(packet.getMatchRequest());

            channel.writeAndFlush(new MatchResponsePacket(response));
        });


    }

}
