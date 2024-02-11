package com.readutf.matchmaker.client;

import com.readutf.matchmaker.client.network.NetworkManager;
import com.readutf.matchmaker.client.server.HeartbeatTask;
import com.readutf.matchmaker.packet.PacketManager;
import com.readutf.matchmaker.packet.serializers.ServerHeartbeatSerializer;
import com.readutf.matchmaker.packet.serializers.ServerRegisterSerializer;
import com.readutf.matchmaker.packet.serializers.ServerUnregisterSerializer;
import com.readutf.matchmaker.server.Server;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class ErosClient {

    private final PacketManager packetManager;
    private final NetworkManager networkManager;
    private final Channel channel;

    public ErosClient(Supplier<List<Server>> serversSupplier) {
        this.packetManager = setupPacketManager();
        this.networkManager = new NetworkManager(Executors.newCachedThreadPool(), "localhost", 4254, packetManager);
        this.channel = networkManager.startConnection();

        startHeartbeat(new Timer(), serversSupplier);
    }

    public PacketManager setupPacketManager() {
        PacketManager packetManager = new PacketManager();

        packetManager.registerPacketEncoder(new ServerRegisterSerializer());
        packetManager.registerPacketEncoder(new ServerHeartbeatSerializer());
        packetManager.registerPacketEncoder(new ServerUnregisterSerializer());

        return packetManager;
    }

    public void startHeartbeat(Timer timer, Supplier<List<Server>> serverSupplier) {
        timer.scheduleAtFixedRate(new HeartbeatTask(packetManager, serverSupplier), 0, 1000);
    }

}
