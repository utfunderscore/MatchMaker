package com.readutf.matchmaker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.readutf.matchmaker.api.EndpointManager;
import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.network.NetworkManager;
import com.readutf.matchmaker.queue.QueueManager;
import com.readutf.matchmaker.server.ServerManager;
import com.readutf.matchmaker.server.socket.ServerUpdateManager;
import com.readutf.matchmaker.shared.packet.Packet;
import com.readutf.matchmaker.shared.packet.PacketManager;
import com.readutf.matchmaker.shared.packet.Serializer;
import com.readutf.matchmaker.shared.queue.QueueEvent;
import com.readutf.matchmaker.shared.server.ServerUpdate;
import io.netty.channel.Channel;
import lombok.Getter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Timer;
import java.util.concurrent.Executors;

@Getter
public class ErosServer {

    private static final Logger logger = LoggerFactory.getLogger(ErosServer.class);
    private static @Getter final Timer timer = new Timer();
    private static @Getter final Gson gson = setupGson();

    private final PacketManager packetManager;
    private final NetworkManager networkManager;
    private final ServerManager serverManager;
    private final EndpointManager endpointManager;
    private final ServerUpdateManager serverUpdateManager;
    private final MatchManager matchManager;
    private final QueueManager queueManager;
    private final Channel channel;
    private final String address;
    private final int port;

    public ErosServer(String address, int port) {
        this.port = port;
        this.address = address;
        File baseDir = new File(System.getProperty("user.dir"));
        this.packetManager = setupPacketManager();
        this.networkManager = new NetworkManager(Executors.newCachedThreadPool(), packetManager);
        this.serverUpdateManager = new ServerUpdateManager(gson, timer);
        this.queueManager = new QueueManager(baseDir);
        this.channel = networkManager.startConnection(address, port);
        logger.info("Orchestrator started on " + address + ":" + port);
        this.serverManager = new ServerManager(serverUpdateManager, packetManager);
        this.matchManager = new MatchManager(packetManager, serverManager);
        this.endpointManager = new EndpointManager(this);
        queueManager.startQueueTask(matchManager, endpointManager.getQueueSocket());
    }

    public PacketManager setupPacketManager() {
        PacketManager packetManager = new PacketManager();

        //register listeners and packets
        for (Class<? extends Serializer> aClass : new Reflections("com.readutf.matchmaker").getSubTypesOf(Serializer.class)) {
            try {
                Constructor<? extends Serializer> constructor = aClass.getConstructor();
                Serializer<Packet> serializer = (Serializer<Packet>) constructor.newInstance();
                packetManager.registerPacketEncoder(serializer);
                logger.info("Registered serializer: " + aClass.getSimpleName());
            } catch (Exception e) {
                logger.warn("Failed to register serializer: " + aClass.getSimpleName() + " - " + e.getMessage());
            }
        }

        return packetManager;
    }

    public static Gson setupGson() {
        return new GsonBuilder().registerTypeAdapterFactory(ServerUpdate.getAdapter()).registerTypeAdapterFactory(QueueEvent.getAdapter()).create();
    }


}
