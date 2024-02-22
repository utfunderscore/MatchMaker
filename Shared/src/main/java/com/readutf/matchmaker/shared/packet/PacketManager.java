package com.readutf.matchmaker.shared.packet;

import com.readutf.matchmaker.shared.packet.annotations.PacketHandler;
import com.readutf.matchmaker.shared.packet.annotations.PacketListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

public class PacketManager {

    private static final Logger logger = LoggerFactory.getLogger(PacketManager.class);

    private @Setter Channel channel;
    private final Map<Integer, Serializer<?>> packetIdToSerializer;
    private final Map<Class<?>, Integer> packetTypeId;
    private final Map<Class<? extends Packet>, List<PacketListener>> packetListeners;


    public PacketManager() {
        this.packetIdToSerializer = new HashMap<>();
        this.packetTypeId = new HashMap<>();
        this.packetListeners = new HashMap<>();
    }

    public void sendPacket(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void sendPackets(Collection<Packet> packets) {
        for (Packet packet : packets) {
            sendPacket(packet);
        }
    }

    public void sendPacket(Packet... packet) {
        for (Packet value : packet) {
            sendPacket(value);
        }
    }

    public void handlePacket(ChannelHandlerContext ctx, Packet packet) {


        System.out.println("Handling packet: " + packet.getClass().getSimpleName() + " " + packet.toString());

        packetListeners.getOrDefault(packet.getClass(), new ArrayList<>())
                .forEach(packetListener -> packetListener.handlePacket(ctx, packet));

    }

    /**
     * Register a function that handles an incoming packet
     * @param packetType - The type of packet to handle
     * @param packetListener - The function that handles the packet
     */
    public void registerListener(Class<? extends Packet> packetType, PacketListener packetListener) {
        List<PacketListener> existingListeners = packetListeners.getOrDefault(packetType, new ArrayList<>());
        existingListeners.add(packetListener);
        packetListeners.put(packetType, existingListeners);
    }

    public void registerListeners(Object object) {
        logger.info("Registering listeners for " + object.getClass().getSimpleName());

        Class<?> aClass = object.getClass();
        for (Method method : aClass.getMethods()) {
            if (!method.isAnnotationPresent(PacketHandler.class)) continue;
            Class<?>[] parameterTypes = method.getParameterTypes();

            Class<? extends Packet> packetType = null;
            for (Class<?> type : parameterTypes) {
                if (Packet.class.isAssignableFrom(type)) {
                    packetType = type.asSubclass(Packet.class);
                    break;
                }
            }
            if (packetType == null) {
                logger.error("No packet type found for packet handler: " + method.getName());
                continue;
            }


            PacketListener packetListener = (ctx, packet) -> {

                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameters.length; i++) {
                    Class<?> type = parameterTypes[i];
                    if (Packet.class.isAssignableFrom(type)) {
                        parameters[i] = packet;
                    } else if (ChannelHandlerContext.class.isAssignableFrom(type)) {
                        parameters[i] = ctx;
                    } else if (Channel.class.isAssignableFrom(type)) {
                        parameters[i] = ctx.channel();
                    } else {
                        logger.error("Invalid parameter type for packet handler: " + type.getSimpleName());
                        return;
                    }
                }

                try {
                    method.invoke(object, parameters);
                } catch (Exception e) {
                    logger.error("Error invoking packet handler", e);
                }
            };

            registerListener(packetType.asSubclass(Packet.class), packetListener);
            logger.info("Registered packet listener for " + packetType.getSimpleName());
        }

    }

    public <T extends Packet> void registerPacketEncoder(Serializer<T> serializer) {
        Class<T> type = serializer.getType();
        int packetId = serializer.getPacketId();

        packetIdToSerializer.put(packetId, serializer);
        packetTypeId.put(type, packetId);
    }

    public @Nullable Serializer<?> getPacketSerializer(int packetId) {
        return packetIdToSerializer.get(packetId);
    }

    public int getPacketId(Class<?> packetType) {
        return packetTypeId.getOrDefault(packetType, -1);
    }

}
