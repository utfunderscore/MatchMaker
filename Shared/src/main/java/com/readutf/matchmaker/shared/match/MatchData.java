package com.readutf.matchmaker.shared.match;

import com.readutf.matchmaker.shared.attribute.Attributes;
import com.readutf.matchmaker.shared.packet.utils.EasyByteReader;
import com.readutf.matchmaker.shared.packet.utils.EasyByteWriter;
import com.readutf.matchmaker.shared.server.ServerAttribute;
import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor @EqualsAndHashCode @ToString
public class MatchData implements Attributes {

    private final UUID matchId;
    private final int ingamePlayers;
    private final String state;
    private final Map<String, ServerAttribute> attributes;

    public MatchData(UUID matchId, int ingamePlayers, String state) {
        this.matchId = matchId;
        this.ingamePlayers = ingamePlayers;
        this.state = state;
        this.attributes = new HashMap<>();
    }

    public static void encode(ByteBuf byteBuf, MatchData matchData) {

        new EasyByteWriter(byteBuf)
                .writeUUID(matchData.matchId)
                .writeInt(matchData.ingamePlayers)
                .writeString(matchData.state)
                .peek(byteBuffer -> matchData.serializeAttributes(byteBuf));

    }

    public static MatchData decode(ByteBuf byteBuf) {
        EasyByteReader reader = new EasyByteReader(byteBuf);
        UUID matchId = reader.readUUID();
        int ingamePlayers = byteBuf.readInt();
        String state = reader.readString();
        Map<String, ServerAttribute> attributes = Attributes.deserializeAttributes(byteBuf);
        return new MatchData(matchId, ingamePlayers, state, attributes);
    }


    @Override
    public Map<String, ServerAttribute> getAttributes() {
        return attributes;
    }
}
