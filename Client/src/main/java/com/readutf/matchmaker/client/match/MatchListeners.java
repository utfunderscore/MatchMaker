package com.readutf.matchmaker.client.match;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.packet.annotations.PacketHandler;
import com.readutf.matchmaker.packet.packets.MatchRequestPacket;
import com.readutf.matchmaker.packet.packets.MatchResponsePacket;
import io.netty.channel.Channel;

public class MatchListeners {

    int i = 0;

    @PacketHandler
    public void onMatchRequest(Channel channel, MatchRequestPacket packet) {

        MatchRequest matchRequest = packet.getMatchRequest();

        i++;

        if(i % 3 == 0) {
            channel.writeAndFlush(new MatchResponsePacket(MatchResponse.success(matchRequest.getRequestId())));
        } else {
            channel.writeAndFlush(new MatchResponsePacket(MatchResponse.failure(matchRequest.getRequestId(), "Testing failure")));
        }


    }

}
