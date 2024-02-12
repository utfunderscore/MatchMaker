package com.readutf.matchmaker.client.match;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.packet.annotations.PacketHandler;
import com.readutf.matchmaker.packet.packets.MatchRequestPacket;
import com.readutf.matchmaker.packet.packets.MatchResponsePacket;
import io.netty.channel.Channel;

public class MatchListeners {

    @PacketHandler
    public void onMatchRequest(Channel channel, MatchRequestPacket packet) {

        MatchRequest matchRequest = packet.getMatchRequest();

        channel.writeAndFlush(new MatchResponsePacket(MatchResponse.success(matchRequest.getRequestId())));
    }

}
