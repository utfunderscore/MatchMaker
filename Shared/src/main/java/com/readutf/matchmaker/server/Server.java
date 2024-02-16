package com.readutf.matchmaker.server;

import com.readutf.matchmaker.attribute.Attributes;
import com.readutf.matchmaker.match.MatchData;
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
@AllArgsConstructor
public class Server implements Serializable, Attributes {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private final UUID id;
    private final String address;
    private final int port;
    private final String category;

    private List<MatchData> matches;
    private Map<String, ServerAttribute> attributes;
    private long lastHeartbeat;
    private int activeGames;
    private int maxGames;

    public boolean isUnreachable() {
        return System.currentTimeMillis() - getLastHeartbeat() > 10000;
    }

    public boolean handleHeartbeat(ServerHeartbeat heartbeat) {

        boolean changed = !heartbeat.getMatches().equals(getMatches()) || heartbeat.getActiveGames() != getActiveGames();

        setLastHeartbeat(System.currentTimeMillis());
        setActiveGames(heartbeat.getActiveGames());
        setMatches(heartbeat.getMatches());

        return changed;
    }

    public double getLoadPercentage() {
        return (double) activeGames / maxGames;
    }

    public String getShortId() {
        return "{s:" + id.toString().substring(0, 8) + "}";
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

        easyByteWriter.writeUUID(server.getId())
                .writeInt(server.getActiveGames())
                .writeInt(server.getMaxGames())
                .writeString(server.getAddress())
                .writeString(server.getCategory())
                .writeInt(server.getPort())
                .writeLong(server.getLastHeartbeat())
                .peek(byteBuffer -> server.serializeAttributes(byteBuf))
                .peek(byteBuffer -> {
                    byteBuffer.writeInt(server.getMatches().size());
                    server.getMatches().forEach(server1 -> MatchData.encode(byteBuffer, server1));
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

        Map<String, ServerAttribute> attributes = Attributes.deserializeAttributes(byteBuf);

        int matches = byteBuf.readInt();
        List<MatchData> matchData = new ArrayList<>();
        for (int i = 0; i < matches; i++) {
            matchData.add(MatchData.decode(byteBuf));
        }

        return new Server(
                serverId,
                address,
                port,
                category,
                matchData,
                attributes,
                lastHeartbeat,
                activeGames,
                maxGames
        );
    }

}
