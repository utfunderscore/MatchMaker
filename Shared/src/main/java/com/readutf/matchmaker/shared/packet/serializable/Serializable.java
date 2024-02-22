package com.readutf.matchmaker.shared.packet.serializable;

import io.netty.buffer.ByteBuf;

/**
 * Represents a class that can be serialized and deserialized
 * Useful for serializer objects within packet
 */
public interface Serializable {

    /**
     * Encodes the object into a byte buffer
     * @param byteBuf the byte buffer to encode into
     */
    void encode(ByteBuf byteBuf);

    /**
     * Decodes the object from a byte buffer
     * @param byteBuf the byte buffer to decode from
     */
    void decode(ByteBuf byteBuf);

}
