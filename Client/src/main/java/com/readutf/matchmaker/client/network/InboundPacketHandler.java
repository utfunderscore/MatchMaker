package com.readutf.matchmaker.client.network;

import com.readutf.matchmaker.packet.Packet;
import com.readutf.matchmaker.packet.PacketManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InboundPacketHandler extends ChannelInboundHandlerAdapter {

    private final PacketManager packetManager;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("closed");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if(msg instanceof Packet packet) {
            packetManager.handlePacket(ctx, packet);
        }

    }
}
