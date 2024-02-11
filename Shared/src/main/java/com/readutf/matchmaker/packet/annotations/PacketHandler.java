package com.readutf.matchmaker.packet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a packet handler
 * Used to scan a class for methods that handle packets
 * Parameters on a method must be either a ChannelHandlerContext or a subclass of Packet
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PacketHandler {
}
