package com.readutf.matchmaker.shared.server;

import com.readutf.matchmaker.shared.match.MatchData;
import com.readutf.matchmaker.shared.packet.utils.EasyByteReader;
import com.readutf.matchmaker.shared.packet.utils.EasyByteWriter;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Heartbeat data sent by a server to the orchestration server
 */
@AllArgsConstructor @NoArgsConstructor @Getter
public class ServerHeartbeat {

    private UUID serverId;
    private int activeGames;
    private List<MatchData> matches;

    /**
     * Encodes the heartbeat data into a byte buffer
     * Total size: 24 bytes
     */
    public static void encode(ByteBuf byteBuf, ServerHeartbeat serverHeartbeat) {
        new EasyByteWriter(byteBuf)
                .writeUUID(serverHeartbeat.getServerId())
                .writeInt(serverHeartbeat.getActiveGames())
                .writeInt(serverHeartbeat.getMatches().size())
                .peek(byteBuf1 -> {
                    for (MatchData match : serverHeartbeat.getMatches()) {
                        MatchData.encode(byteBuf1, match);
                    }
                });
    }

    /**
     * Decodes the heartbeat data from a byte buffer
     */
    public static ServerHeartbeat decode(ByteBuf byteBuf) {
        EasyByteReader reader = new EasyByteReader(byteBuf);
        UUID serverId = reader.readUUID();
        int activeGames = byteBuf.readInt();
        int size = byteBuf.readInt();

        List<MatchData> matches = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MatchData matchData = MatchData.decode(byteBuf);
            matches.add(matchData);
        }

        return new ServerHeartbeat(serverId, activeGames, matches);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ServerHeartbeat that)) return false;
        return activeGames == that.activeGames && Objects.equals(serverId, that.serverId) && Objects.equals(matches, that.matches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId, activeGames, matches);
    }

    @Override
    public String toString() {
        return "ServerHeartbeat{" +
                "serverId=" + serverId +
                ", activeGames=" + activeGames +
                ", matches=" + matches +
                '}';
    }
}
