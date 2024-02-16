package com.readutf.matchmaker.attribute;

import com.readutf.matchmaker.match.MatchData;
import com.readutf.matchmaker.packet.Serializer;
import com.readutf.matchmaker.packet.utils.EasyByteReader;
import com.readutf.matchmaker.packet.utils.EasyByteWriter;
import com.readutf.matchmaker.server.ServerAttribute;
import io.netty.buffer.ByteBuf;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public interface Attributes {

    Map<String, ServerAttribute> getAttributes();

    default <T> void addAttribute(String key, T attributeData, Serializer<T> serializer) {
        ByteBuf encode = serializer.encode(attributeData);
        byte[] bytes = new byte[encode.readableBytes()];
        encode.readBytes(bytes);
        getAttributes().put(key, new ServerAttribute(Base64.getEncoder().encodeToString(bytes), serializer.getType()));
    }

    default boolean hasAttribute(String key) {
        return getAttributes().containsKey(key);
    }

    default <T> T getAttribute(String key, Serializer<T> serializer) {
        if (getAttributes() == null) return null;
        ServerAttribute serverAttribute = getAttributes().get(key);

        if (serverAttribute.getType().equals(serializer.getType())) {
            byte[] bytes = Base64.getDecoder().decode(serverAttribute.getEncoded());
            ByteBuf byteBuf = serializer.encode(serializer.getType().cast(bytes));
            return serializer.decode(byteBuf);
        } else {
            throw new RuntimeException("Attribute type does not match");
        }

    }

    default void serializeAttributes(ByteBuf byteBuf) {
        byteBuf.writeInt(getAttributes().size());
        EasyByteWriter byteWriter = new EasyByteWriter(byteBuf);
        getAttributes().forEach((key, attribute) -> {
            byteWriter.writeString(key)
                    .writeString(attribute.getEncoded())
                    .writeString(attribute.getType().getName());
        });
    }

    static Map<String, ServerAttribute> deserializeAttributes(ByteBuf byteBuf) {
        EasyByteReader reader = new EasyByteReader(byteBuf);
        HashMap<String, ServerAttribute> attributes = new HashMap<>();
        int size = byteBuf.readInt();
        for (int i = 0; i < size; i++) {
            String key = reader.readString();
            String encoded = reader.readString();
            String type = reader.readString();
            try {
                attributes.put(key, new ServerAttribute(encoded, Class.forName(type)));
            } catch (ClassNotFoundException e) {
                LoggerFactory.getLogger("Attributes").warn("Could not find class " + type + " for attribute " + key);
            }
        }
        return attributes;
    }

}
