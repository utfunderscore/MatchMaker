package com.readutf.matchmaker.server.listeners;

import com.readutf.matchmaker.packet.annotations.PacketHandler;
import com.readutf.matchmaker.packet.packets.ChannelClosePacket;
import com.readutf.matchmaker.packet.packets.ServerHeartbeatPacket;
import com.readutf.matchmaker.packet.packets.ServerRegisterPacket;
import com.readutf.matchmaker.packet.packets.ServerUnregisterPacket;
import com.readutf.matchmaker.server.RegisteredServer;
import com.readutf.matchmaker.server.Server;
import com.readutf.matchmaker.server.ServerHeartbeat;
import com.readutf.matchmaker.server.ServerManager;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ServerListeners {

    private final ServerManager serverManager;

    @PacketHandler
    public void onServerRegister(Channel channel, ServerRegisterPacket packet) {
        for (Server availableServer : packet.getAvailableServers()) {
            serverManager.registerServer(channel, availableServer);
        }
    }

    @PacketHandler
    public void onServerHeartbeat(ServerHeartbeatPacket heartbeatPacket) {
        List<ServerHeartbeat> serverHeartbeats = heartbeatPacket.getServerHeartbeats();

        for (ServerHeartbeat serverHeartbeat : serverHeartbeats) {
            serverManager.handleHeartbeat(serverHeartbeat.getServerId(), serverHeartbeat);
        }

    }

    @PacketHandler
    public void onServerUnregister(ServerUnregisterPacket packet) {

        List<UUID> serverIds = packet.getServerIds();
        for (UUID serverId : serverIds) {
            serverManager.unregisterServer(serverId);
        }

    }


    @PacketHandler
    public void onChannelClose(Channel channel, ChannelClosePacket closePacket) {
        List<RegisteredServer> servers = serverManager.getServers(channel);

        for (RegisteredServer server : new ArrayList<>(servers)) {
            serverManager.unregisterServer(server);
        }
    }

}
