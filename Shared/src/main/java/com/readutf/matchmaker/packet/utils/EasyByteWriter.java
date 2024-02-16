package com.readutf.matchmaker.packet.utils;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Consumer;

public class EasyByteWriter {

    private final ByteBuf byteBuf;

    public EasyByteWriter(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public void write(byte[] bytes) {
        byteBuf.writeBytes(bytes);
    }

    /**
     * Writes a UUID to the buffer
     * Size of 16 bytes
     * @param uuid - the UUID to write
     */
    public EasyByteWriter writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }
    /**
     * Writes an integer to the buffer
     * Size of 4 bytes
     * @param i - the integer to write
     * @return - the EasyByteWriter instance
     */
    public EasyByteWriter writeInt(int i) {
        byteBuf.writeInt(i);
        return this;
    }

    public EasyByteWriter writeLong(long l) {
        byteBuf.writeLong(l);
        return this;
    }

    public EasyByteWriter writeBoolean(boolean b) {
        byteBuf.writeBoolean(b);
        return this;
    }

    public EasyByteWriter peek(Consumer<ByteBuf> internalBuf) {
        internalBuf.accept(byteBuf);
        return this;
    }

    public EasyByteWriter writeString(String s) {
        byteBuf.writeInt(s.length());
        write(s.getBytes());
        return this;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }


}
