package com.readutf.matchmaker.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PacketDecoder extends ReplayingDecoder<Packet> {

    private static final Logger logger = LoggerFactory.getLogger(PacketDecoder.class);
    private final PacketManager packetManager;

    public PacketDecoder(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) {


        try {

            byte packetId = in.readByte();
            if(packetId == 0) return;

            short length = in.readShort();

            if(in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            ByteBuf byteBuf = in.readBytes(length);

            Serializer<?> encoder = packetManager.getPacketSerializer(packetId);
            if(encoder == null) {
                logger.warn("No packet encoder found for id: " + packetId);
                return;
            }

            Object decoded = encoder.decode(byteBuf);

            list.add(decoded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
