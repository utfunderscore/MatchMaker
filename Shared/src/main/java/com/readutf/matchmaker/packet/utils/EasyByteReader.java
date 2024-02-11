package com.readutf.matchmaker.packet.utils;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.util.UUID;

public class EasyByteReader {

    private final ByteBuf in;

    public EasyByteReader(ByteBuf byteBuf) {
        this.in = byteBuf;
    }

    public String readString() {
        int i = in.readInt();
        System.out.println("Reading string of length " + i);
        byte[] bytes = new byte[i];
        in.readBytes(bytes);
        return new String(bytes);
    }

    public UUID readUUID() {
        long uuidMost = in.readLong();
        long uuidLeast = in.readLong();
        return new UUID(uuidMost, uuidLeast);
    }


}
