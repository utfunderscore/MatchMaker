package com.readutf.matchmaker.server;

import com.readutf.matchmaker.packet.Serializer;
import com.readutf.matchmaker.packet.utils.EasyByteReader;
import com.readutf.matchmaker.packet.utils.EasyByteWriter;
import io.netty.buffer.ByteBuf;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a server/game that can be joined
 * Contains serializable methods to encode and decode the server
 * Make sure to change the encode and decode methods if you modify the class
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Server implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private UUID id;
    private int activeGames;
    private int maxGames;
    private String address, category;
    private int port;
    private long lastHeartbeat;
    private Map<String, ServerAttribute> attributes;

    public String getShortId() {
        return "{s:" + id.toString().substring(0, 8) + "}";
    }

    public <T> void addAttribute(String key, T attributeData, Serializer<T> serializer) {
        ByteBuf encode = serializer.encode(attributeData);
        byte[] bytes = new byte[encode.readableBytes()];
        encode.readBytes(bytes);
        attributes.put(key, new ServerAttribute(Base64.getEncoder().encodeToString(bytes), serializer.getType()));
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public <T> T getAttribute(String key, Serializer<T> serializer) {
        ServerAttribute serverAttribute = attributes.get(key);
        if (serverAttribute == null) return null;


        if (serverAttribute.getType().equals(serializer.getType())) {
            byte[] bytes = Base64.getDecoder().decode(serverAttribute.getEncoded());
            ByteBuf byteBuf = serializer.encode(serializer.getType().cast(bytes));
            return serializer.decode(byteBuf);
        } else {
            throw new RuntimeException("Attribute type does not match");
        }

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Server server = (Server) object;
        return activeGames == server.activeGames && maxGames == server.maxGames && port == server.port
                && lastHeartbeat == server.lastHeartbeat && Objects.equals(id, server.id)
                && Objects.equals(address, server.address) && Objects.equals(category, server.category)
                && Objects.equals(attributes, server.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, activeGames, maxGames, address, category, port, lastHeartbeat, attributes);
    }

    public static void encodeServer(Server server, ByteBuf byteBuf) {
        EasyByteWriter easyByteWriter = new EasyByteWriter(byteBuf);

        easyByteWriter.writeUUID(server.getId());
        easyByteWriter.writeInt(server.getActiveGames());
        easyByteWriter.writeInt(server.getMaxGames());
        easyByteWriter.writeString(server.getAddress());
        easyByteWriter.writeString(server.getCategory());
        easyByteWriter.writeInt(server.getPort());
        easyByteWriter.writeLong(server.getLastHeartbeat());

        easyByteWriter.writeInt(server.getAttributes().size());
        server.getAttributes().forEach((key, attribute) -> {
            easyByteWriter.writeString(key);
            easyByteWriter.writeString(attribute.getEncoded());
            easyByteWriter.writeString(attribute.getType().getName());
        });

    }

    public static Server decodeServer(ByteBuf byteBuf) {
        EasyByteReader easyByteReader = new EasyByteReader(byteBuf);

        UUID serverId = easyByteReader.readUUID();
        int activeGames = byteBuf.readInt();
        int maxGames = byteBuf.readInt();
        String address = easyByteReader.readString();
        String category = easyByteReader.readString();
        int port = byteBuf.readInt();
        long lastHeartbeat = byteBuf.readLong();

        Map<String, ServerAttribute> attributes = new HashMap<>();
        int attributeCount = byteBuf.readInt();
        for (int i = 0; i < attributeCount; i++) {
            String key = easyByteReader.readString();
            String encoded = easyByteReader.readString();
            String type = easyByteReader.readString();
            try {
                attributes.put(key, new ServerAttribute(encoded, Class.forName(type)));
            } catch (ClassNotFoundException e) {
                logger.error("Could not find class for attribute type: " + type);
                continue;
            }
        }

        return new Server(
                serverId,
                activeGames,
                maxGames,
                address,
                category,
                port,
                lastHeartbeat,
                attributes
        );
    }

}
