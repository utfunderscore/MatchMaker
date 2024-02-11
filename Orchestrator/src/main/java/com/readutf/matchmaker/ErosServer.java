package com.readutf.matchmaker;

import com.readutf.matchmaker.api.EndpointManager;
import com.readutf.matchmaker.network.NetworkManager;
import com.readutf.matchmaker.packet.PacketManager;
import com.readutf.matchmaker.packet.serializers.ServerHeartbeatSerializer;
import com.readutf.matchmaker.packet.serializers.ServerRegisterSerializer;
import com.readutf.matchmaker.packet.serializers.ServerUnregisterSerializer;
import com.readutf.matchmaker.server.ServerManager;
import com.readutf.matchmaker.server.socket.ServerUpdateManager;
import io.netty.channel.Channel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.concurrent.Executors;

@Getter
public class ErosServer {

    private static final Logger logger = LoggerFactory.getLogger(ErosServer.class);

    private final PacketManager packetManager;
    private final NetworkManager networkManager;
    private final ServerManager serverManager;
    private final EndpointManager endpointManager;
    private final ServerUpdateManager serverUpdateManager;
    private final Timer timer;
    private final Channel channel;
    private final String address;
    private final int port;

    public ErosServer(String address, int port) {
        this.timer = new Timer();
        this.port = port;
        this.address = address;
        this.packetManager = setupPacketManager();
        this.networkManager = new NetworkManager(Executors.newCachedThreadPool(), packetManager);
        this.serverUpdateManager = new ServerUpdateManager(timer);
        this.endpointManager = new EndpointManager(serverUpdateManager);
        this.channel = networkManager.startConnection(address, port);
        logger.info("Orchestrator started on " + address + ":" + port);
        this.serverManager = new ServerManager(serverUpdateManager, packetManager);
    }

    public PacketManager setupPacketManager() {
        PacketManager packetManager = new PacketManager();

        //register listeners and packets
        packetManager.registerPacketEncoder(new ServerRegisterSerializer());
        packetManager.registerPacketEncoder(new ServerHeartbeatSerializer());
        packetManager.registerPacketEncoder(new ServerUnregisterSerializer());

        return packetManager;
    }

}
