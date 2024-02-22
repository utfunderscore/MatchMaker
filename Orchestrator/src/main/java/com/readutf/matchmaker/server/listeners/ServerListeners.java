package com.readutf.matchmaker.server.listeners;

import com.readutf.matchmaker.shared.packet.annotations.PacketHandler;
import com.readutf.matchmaker.shared.packet.packets.ChannelClosePacket;
import com.readutf.matchmaker.shared.packet.packets.ServerHeartbeatPacket;
import com.readutf.matchmaker.shared.packet.packets.ServerRegisterPacket;
import com.readutf.matchmaker.shared.packet.packets.ServerUnregisterPacket;
import com.readutf.matchmaker.server.RegisteredServer;
import com.readutf.matchmaker.shared.server.ServerHeartbeat;
import com.readutf.matchmaker.server.ServerManager;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ServerListeners {

    private final ServerManager serverManager;

    @PacketHandler
    public void onServerRegister(Channel channel, ServerRegisterPacket packet) {

        serverManager.registerServer(channel, packet.getServer());

    }

    @PacketHandler
    public void onServerHeartbeat(ServerHeartbeatPacket heartbeatPacket) {
        ServerHeartbeat serverHeartbeat = heartbeatPacket.getServerHeartbeat();

        serverManager.handleHeartbeat(serverHeartbeat.getServerId(), serverHeartbeat);
    }

    @PacketHandler
    public void onServerUnregister(ServerUnregisterPacket packet) {

        serverManager.unregisterServer(packet.getServerId());

    }


    @PacketHandler
    public void onChannelClose(Channel channel, ChannelClosePacket closePacket) {
        List<RegisteredServer> servers = serverManager.getServers(channel);

        for (RegisteredServer server : new ArrayList<>(servers)) {
            serverManager.unregisterServer(server);
        }
    }

}
