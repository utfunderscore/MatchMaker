package com.readutf.matchmaker.packet.serializers;

import com.readutf.matchmaker.packet.Serializer;
import com.readutf.matchmaker.packet.packets.ServerUnregisterPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerUnregisterSerializer implements Serializer<ServerUnregisterPacket> {

    @Override
    public int getPacketId() {
        return 3;
    }

    @Override
    public ByteBuf encode(ServerUnregisterPacket data) {
        ByteBuf buffer = Unpooled.buffer((data.getServerIds().size() * 16) + 4);

        buffer.writeInt(data.getServerIds().size());
        for (UUID serverId : data.getServerIds()) {
            buffer.writeLong(serverId.getMostSignificantBits());
            buffer.writeLong(serverId.getLeastSignificantBits());
        }

        return buffer;
    }

    @Override
    public ServerUnregisterPacket decode(ByteBuf byteBuf) {

        List<UUID> ids = new ArrayList<>();
        int servers = byteBuf.readInt();
        for (int i = 0; i < servers; i++) {
            long most = byteBuf.readLong();
            long least = byteBuf.readLong();
            ids.add(new UUID(most, least));
        }

        return new ServerUnregisterPacket(ids);
    }
}
