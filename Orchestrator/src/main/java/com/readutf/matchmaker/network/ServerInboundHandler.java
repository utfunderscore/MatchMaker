package com.readutf.matchmaker.network;

import com.readutf.matchmaker.packet.Packet;
import com.readutf.matchmaker.packet.PacketManager;
import com.readutf.matchmaker.packet.packets.ChannelClosePacket;
import com.readutf.matchmaker.packet.packets.ServerRegisterPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;

public class ServerInboundHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerInboundHandler.class);

    private final PacketManager packetManager;

    public ServerInboundHandler(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        packetManager.handlePacket(ctx, new ChannelClosePacket());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof Packet packet) {
            packetManager.handlePacket(ctx, packet);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(cause instanceof SocketException socketException && socketException.getMessage().equalsIgnoreCase("Connection reset")) {
            logger.info("Connection reset: " + ctx.channel().id().asShortText());
        }
    }
}
