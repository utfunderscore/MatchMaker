package com.readutf.matchmaker.packet.annotations;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * A method that handles incoming packets
 */
public interface PacketListener {

    /**
     * Handle an incoming packet
     * @param ctx - The channel context
     * @param packet - The packet
     */
    void handlePacket(ChannelHandlerContext ctx, Object packet);

}
