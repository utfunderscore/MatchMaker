package com.readutf.matchmaker.shared.packet.serializers;

import com.readutf.matchmaker.shared.packet.Serializer;
import com.readutf.matchmaker.shared.packet.packets.ServerRegisterPacket;
import com.readutf.matchmaker.shared.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ServerRegisterSerializer implements Serializer<ServerRegisterPacket> {

    @Override
    public int getPacketId() {
        return 1;
    }

    @Override
    public ServerRegisterPacket decode(ByteBuf byteBuf) {

        Server server = Server.decodeServer(byteBuf);

        return new ServerRegisterPacket(server);
    }

    @Override
    public ByteBuf encode(ServerRegisterPacket data) {

        ByteBuf buffer = Unpooled.buffer(20);
        Server.encodeServer(data.getServer(), buffer);

        return buffer;
    }

}