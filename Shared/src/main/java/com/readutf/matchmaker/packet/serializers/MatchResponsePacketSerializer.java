package com.readutf.matchmaker.packet.serializers;

import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.packet.Serializer;
import com.readutf.matchmaker.packet.packets.MatchResponsePacket;
import com.readutf.matchmaker.packet.utils.EasyByteReader;
import com.readutf.matchmaker.packet.utils.EasyByteWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

public class MatchResponsePacketSerializer implements Serializer<MatchResponsePacket> {

    @Override
    public int getPacketId() {
        return 5;
    }

    @Override
    public ByteBuf encode(MatchResponsePacket data) {
        EasyByteWriter byteWriter = new EasyByteWriter(Unpooled.buffer());

        MatchResponse response = data.getMatchResponse();

        if(response.isSuccessful()) {
            return byteWriter.writeBoolean(true) // success
                    .writeUUID(response.getRequestId()) // request id
                    .writeUUID(response.getMatchId()) // match id
                    .getByteBuf();
        } else {
            return byteWriter.writeBoolean(false) // failure
                    .writeUUID(response.getRequestId()) // request id
                    .writeString(response.getFailureReason()) // reason
                    .getByteBuf();
        }
    }

    @Override
    public MatchResponsePacket decode(ByteBuf byteBuf) {
        EasyByteReader reader = new EasyByteReader(byteBuf);
        if (byteBuf.readBoolean()) {
            UUID requestId = reader.readUUID();
            UUID matchId = reader.readUUID();
            return new MatchResponsePacket(MatchResponse.success(requestId, matchId));
        } else {
            UUID requestId = reader.readUUID();
            String reason = reader.readString();
            return new MatchResponsePacket(MatchResponse.failure(requestId, reason));
        }
    }
}
