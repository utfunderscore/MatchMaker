package com.readutf.matchmaker.shared.packet;

import io.netty.buffer.ByteBuf;

public interface Serializer<T> {

    int getPacketId();

    ByteBuf encode(T data);

    T decode(ByteBuf byteBuf);

    @SuppressWarnings("unchecked")
    default Class<T> getType() {
        return (Class<T>) ((java.lang.reflect.ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

}