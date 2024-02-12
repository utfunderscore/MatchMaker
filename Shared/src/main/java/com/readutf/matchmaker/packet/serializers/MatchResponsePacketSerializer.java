package com.readutf.matchmaker.packet.serializers;

import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.packet.Serializer;
import com.readutf.matchmaker.packet.packets.MatchResponsePacket;
import com.readutf.matchmaker.packet.utils.EasyByteReader;
import com.readutf.matchmaker.packet.utils.EasyByteWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
            return byteWriter.writeBoolean(true).writeUUID(response.getRequestId()).getByteBuf();
        } else {
            return byteWriter.writeBoolean(false).writeUUID(response.getRequestId()).writeString(response.getFailureReason()).getByteBuf();
        }
    }

    @Override
    public MatchResponsePacket decode(ByteBuf byteBuf) {
        EasyByteReader reader = new EasyByteReader(byteBuf);
        if (byteBuf.readBoolean()) {
            return new MatchResponsePacket(MatchResponse.success(reader.readUUID()));
        } else {
            return new MatchResponsePacket(MatchResponse.failure(reader.readUUID(), reader.readString()));
        }
    }
}
