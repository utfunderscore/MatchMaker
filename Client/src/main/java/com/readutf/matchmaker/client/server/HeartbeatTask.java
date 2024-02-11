package com.readutf.matchmaker.client.server;

import com.readutf.matchmaker.packet.PacketManager;
import com.readutf.matchmaker.packet.packets.ServerHeartbeatPacket;
import com.readutf.matchmaker.packet.packets.ServerRegisterPacket;
import com.readutf.matchmaker.packet.packets.ServerUnregisterPacket;
import com.readutf.matchmaker.server.Server;
import com.readutf.matchmaker.server.ServerHeartbeat;

import java.util.*;
import java.util.function.Supplier;

public class HeartbeatTask extends TimerTask {

    private final PacketManager packetManager;
    private final Supplier<List<Server>> serverSupplier;

    private List<Server> previousServers = new ArrayList<>();

    public HeartbeatTask(PacketManager packetManager, Supplier<List<Server>> serverSupplier) {
        this.packetManager = packetManager;
        this.serverSupplier = serverSupplier;
    }

    @Override
    public void run() {

        List<Server> servers = serverSupplier.get();

        List<UUID> deleted = new ArrayList<>();
        List<ServerHeartbeat> existing = new ArrayList<>();
        List<Server> created = new ArrayList<>();

        for (Server server : servers) {
            if (!previousServers.contains(server)) {
                created.add(server);
            } else {
                existing.add(new ServerHeartbeat(server.getId(), server.getPlayerCount()));
            }
        }

        for (Server previousServer : previousServers) {
            if (servers.stream().noneMatch(server -> server.getId() == previousServer.getId())) {
                deleted.add(previousServer.getId());
            }
        }

        this.previousServers = servers;

        ServerRegisterPacket registerNew = new ServerRegisterPacket(created);
        ServerHeartbeatPacket heartbeat = new ServerHeartbeatPacket(existing);
        ServerUnregisterPacket unregister = new ServerUnregisterPacket(deleted);

        if(!created.isEmpty()) packetManager.sendPacket(registerNew);
        if (!existing.isEmpty()) packetManager.sendPacket(heartbeat);
        if (!deleted.isEmpty()) packetManager.sendPacket(unregister);
    }

}
