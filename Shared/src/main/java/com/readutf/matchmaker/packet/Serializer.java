package com.readutf.matchmaker.packet;

import io.netty.buffer.ByteBuf;

public interface Serializer<T> {

    int getPacketId();

    T decode(ByteBuf byteBuf);

    ByteBuf encode(T data);

    @SuppressWarnings("unchecked")
    default Class<T> getType() {
        return (Class<T>) ((java.lang.reflect.ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

}