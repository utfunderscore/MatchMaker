package com.readutf.matchmaker.packet.serializers;

import com.readutf.matchmaker.packet.Serializer;
import com.readutf.matchmaker.packet.packets.ServerHeartbeatPacket;
import com.readutf.matchmaker.packet.utils.EasyByteWriter;
import com.readutf.matchmaker.server.ServerHeartbeat;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;

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
        EasyByteWriter easyByteWriter = new EasyByteWriter(buffer);
        easyByteWriter.writeInt(data.getServerHeartbeats().size());
        for (ServerHeartbeat serverHeartbeat : data.getServerHeartbeats()) {
            serverHeartbeat.encode(buffer);
        }
        return buffer;
    }

    /**
     * Reads the amount of server heartbeats being sent
     * Then reads each server heartbeat
     * @param byteBuf
     * @return
     */
    @Override
    public ServerHeartbeatPacket decode(ByteBuf byteBuf) {
        int servers = byteBuf.readInt();
        ArrayList<ServerHeartbeat> heartbeats = new ArrayList<>();

        for (int i = 0; i < servers; i++) {
            ServerHeartbeat serverHeartbeat = new ServerHeartbeat();
            serverHeartbeat.decode(byteBuf);
            heartbeats.add(serverHeartbeat);
        }

        return new ServerHeartbeatPacket(heartbeats);

    }
}
