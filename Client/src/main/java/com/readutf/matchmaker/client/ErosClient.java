package com.readutf.matchmaker.client;

import com.readutf.matchmaker.client.match.MatchListeners;
import com.readutf.matchmaker.client.match.MatchRequestHandler;
import com.readutf.matchmaker.client.network.NetworkManager;
import com.readutf.matchmaker.client.server.HeartbeatTask;
import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.packet.Packet;
import com.readutf.matchmaker.packet.PacketManager;
import com.readutf.matchmaker.packet.Serializer;
import com.readutf.matchmaker.server.Server;
import io.netty.channel.Channel;
import lombok.Getter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
public class ErosClient {

    private static Logger logger = LoggerFactory.getLogger(ErosClient.class);

    private final PacketManager packetManager;
    private final NetworkManager networkManager;
    private final MatchListeners matchListeners;
    private final MatchRequestHandler matchRequestHandler;
    private final Channel channel;

    public ErosClient(Supplier<Server> serversSupplier, MatchRequestHandler matchRequestHandler) {
        this.packetManager = setupPacketManager();
        this.networkManager = new NetworkManager(Executors.newCachedThreadPool(), "localhost", 4254, packetManager);
        this.channel = networkManager.startConnection();
        this.matchListeners = new MatchListeners(matchRequestHandler);
        this.matchRequestHandler = matchRequestHandler;

        startHeartbeat(new Timer(), serversSupplier);
        packetManager.registerListeners(matchListeners);
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

    public void startHeartbeat(Timer timer, Supplier<Server> serverSupplier) {
        timer.scheduleAtFixedRate(new HeartbeatTask(packetManager, serverSupplier), 0, 1000);
    }

}
