package com.readutf.matchmaker.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private static Logger logger = LoggerFactory.getLogger(PacketEncoder.class);

    private final PacketManager packetManager;

    public PacketEncoder(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, @NotNull Packet packet, ByteBuf byteBuf) {


        try {
            int packetId = packetManager.getPacketId(packet.getClass());
            if(packetId == -1) {
                logger.warn("No packet id found for class: " + packet.getClass().getSimpleName());
                return;
            }

            Serializer<Packet> serializer = (Serializer<Packet>) packetManager.getPacketSerializer(packetId);
            if(serializer == null) {
                logger.warn("No packet serializer found for class: " + packet.getClass().getSimpleName());
                return;
            }

            ByteBuf encode = serializer.encode(packet);

            byteBuf.writeInt(packetId);
            byteBuf.writeShort(encode.readableBytes());
            byteBuf.writeBytes(encode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
