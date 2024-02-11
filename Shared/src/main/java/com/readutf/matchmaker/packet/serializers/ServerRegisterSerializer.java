package com.readutf.matchmaker.packet.serializers;

import com.readutf.matchmaker.packet.Serializer;
import com.readutf.matchmaker.packet.packets.ServerRegisterPacket;
import com.readutf.matchmaker.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;

public class ServerRegisterSerializer implements Serializer<ServerRegisterPacket> {

    @Override
    public int getPacketId() {
        return 1;
    }

    @Override
    public ServerRegisterPacket decode(ByteBuf byteBuf) {
        ArrayList<Server> servers = new ArrayList<>();

        int length = byteBuf.readShort();
        for (int i = 0; i < length; i++) {
            Server server = Server.decodeServer(byteBuf);
            servers.add(server);
        }

        return new ServerRegisterPacket(servers);
    }

    @Override
    public ByteBuf encode(ServerRegisterPacket data) {

        ByteBuf buffer = Unpooled.buffer(20);
        buffer.writeShort(data.getAvailableServers().size());
        for (Server server : data.getAvailableServers()) {
            Server.encodeServer(server, buffer);
        }

        return buffer;
    }

}