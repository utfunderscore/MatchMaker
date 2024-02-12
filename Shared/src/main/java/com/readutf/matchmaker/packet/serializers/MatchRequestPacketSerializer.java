package com.readutf.matchmaker.packet.serializers;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.packet.Serializer;
import com.readutf.matchmaker.packet.packets.MatchRequestPacket;
import com.readutf.matchmaker.packet.utils.EasyByteReader;
import com.readutf.matchmaker.packet.utils.EasyByteWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchRequestPacketSerializer implements Serializer<MatchRequestPacket> {

    @Override
    public int getPacketId() {
        return 4;
    }

    @Override
    public ByteBuf encode(MatchRequestPacket data) {
        ByteBuf buffer = Unpooled.buffer();
        MatchRequest matchRequest = data.getMatchRequest();

        UUID requestId = matchRequest.getRequestId();
        EasyByteWriter easyByteWriter = new EasyByteWriter(buffer)
                .writeUUID(requestId)
                .writeString(matchRequest.getQueueId());

        int numOfTeams = matchRequest.getTeams().size();
        easyByteWriter.writeInt(numOfTeams);
        for (List<UUID> team : matchRequest.getTeams()) {
            int numOfPlayers = team.size();
            easyByteWriter.writeInt(numOfPlayers);
            for (UUID player : team) {
                easyByteWriter.writeUUID(player);
            }
        }

        return buffer;
    }

    @Override
    public MatchRequestPacket decode(ByteBuf byteBuf) {
        EasyByteReader reader = new EasyByteReader(byteBuf);

        UUID requestId = reader.readUUID();
        String queueId = reader.readString();
        int numOfTeams = byteBuf.readInt();
        List<List<UUID>> teams = new ArrayList<>();
        for (int i = 0; i < numOfTeams; i++) {
            int numOfPlayers = byteBuf.readInt();
            List<UUID> team = new ArrayList<>();
            for (int j = 0; j < numOfPlayers; j++) {
                team.add(reader.readUUID());
            }
            teams.add(team);
        }

        return new MatchRequestPacket(new MatchRequest(requestId, queueId, teams));
    }
}
