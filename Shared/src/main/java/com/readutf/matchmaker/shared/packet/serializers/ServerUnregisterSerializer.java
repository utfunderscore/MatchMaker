package com.readutf.matchmaker.shared.packet.serializers;

import com.readutf.matchmaker.shared.packet.Serializer;
import com.readutf.matchmaker.shared.packet.packets.ServerUnregisterPacket;
import com.readutf.matchmaker.shared.packet.utils.EasyByteReader;
import com.readutf.matchmaker.shared.packet.utils.EasyByteWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

public class ServerUnregisterSerializer implements Serializer<ServerUnregisterPacket> {

    @Override
    public int getPacketId() {
        return 3;
    }

    @Override
    public ByteBuf encode(ServerUnregisterPacket data) {
        ByteBuf buffer = Unpooled.buffer();

        new EasyByteWriter(buffer)
                .writeUUID(data.getServerId());

        return buffer;
    }

    @Override
    public ServerUnregisterPacket decode(ByteBuf byteBuf) {

        UUID uuid = new EasyByteReader(byteBuf).readUUID();

        return new ServerUnregisterPacket(uuid);
    }
}
