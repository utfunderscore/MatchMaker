package com.readutf.matchmaker.shared.packet.serializers;

import com.readutf.matchmaker.shared.packet.Serializer;
import com.readutf.matchmaker.shared.packet.packets.ServerHeartbeatPacket;
import com.readutf.matchmaker.shared.server.ServerHeartbeat;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ServerHeartbeatSerializer implements Serializer<ServerHeartbeatPacket> {

    @Override
    public int getPacketId() {
        return 2;
    }

    /**
     * Writes the amount of server heartbeats being sent
     * Then writes each encoded server heartbeat
     * @param data
     * @return
     */
    @Override
    public ByteBuf encode(ServerHeartbeatPacket data) {
        ByteBuf buffer = Unpooled.buffer();

        ServerHeartbeat.encode(buffer, data.getServerHeartbeat());

        return buffer;
    }

    /**
     * Reads the amount of server heartbeats being sent
     * Then reads each server heartbeat
     * @param byteBuf
     */
    @Override
    public ServerHeartbeatPacket decode(ByteBuf byteBuf) {

        ServerHeartbeat serverHeartbeat = ServerHeartbeat.decode(byteBuf);

        return new ServerHeartbeatPacket(serverHeartbeat);

    }
}
