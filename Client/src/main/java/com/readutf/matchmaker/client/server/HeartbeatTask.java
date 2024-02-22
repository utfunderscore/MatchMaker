package com.readutf.matchmaker.client.server;

import com.readutf.matchmaker.shared.packet.PacketManager;
import com.readutf.matchmaker.shared.packet.packets.ServerHeartbeatPacket;
import com.readutf.matchmaker.shared.packet.packets.ServerRegisterPacket;
import com.readutf.matchmaker.shared.packet.packets.ServerUnregisterPacket;
import com.readutf.matchmaker.shared.server.Server;
import com.readutf.matchmaker.shared.server.ServerHeartbeat;

import java.util.TimerTask;
import java.util.function.Supplier;

public class HeartbeatTask extends TimerTask {

    private final PacketManager packetManager;
    private final Supplier<Server> serverSupplier;

    private Server previousServer;

    public HeartbeatTask(PacketManager packetManager, Supplier<Server> serverSupplier) {
        this.packetManager = packetManager;
        this.serverSupplier = serverSupplier;
    }

    @Override
    public void run() {

        Server server = serverSupplier.get();
        if(previousServer == null && server != null) {
           previousServer = server;
           packetManager.sendPacket(new ServerRegisterPacket(server));
           return;
       }

        if(server == null) {
            if(previousServer != null) {
                packetManager.sendPacket(new ServerUnregisterPacket(previousServer.getId()));
                previousServer = null;
            }
            return;
        }

        packetManager.sendPacket(new ServerHeartbeatPacket(new ServerHeartbeat(server.getId(), server.getActiveGames(), server.getMatches())));

    }

}
