import com.readutf.matchmaker.shared.match.MatchData;
import com.readutf.matchmaker.shared.packet.packets.ServerRegisterPacket;
import com.readutf.matchmaker.shared.packet.serializers.ServerRegisterSerializer;
import com.readutf.matchmaker.shared.server.Server;
import com.readutf.matchmaker.shared.server.ServerHeartbeat;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SerializationTests {

    @Test
    void serverSerialization() {

        Server server = new Server(UUID.randomUUID(), "localhost", 25, "test", List.of(), new HashMap<>(),
                System.currentTimeMillis(), 1, 1);

        ByteBuf byteBuf = Unpooled.buffer(20);
        Server.encodeServer(server, byteBuf);

        Server decoded = Server.decodeServer(byteBuf);

        Assertions.assertEquals(server, decoded);
    }

    @Test
    void serverRegisterSerialization() {

        ServerRegisterSerializer serializer = new ServerRegisterSerializer();

        Server server = new Server(UUID.randomUUID(), "localhost", 25, "test", List.of(), new HashMap<>(),
                System.currentTimeMillis(), 1, 1);

        ServerRegisterPacket data = new ServerRegisterPacket(server);

        ByteBuf encode = serializer.encode(data);
        ServerRegisterPacket decoded = serializer.decode(encode);

        Assertions.assertEquals(data, decoded);
    }

    @Test
    void heartbeatSerialization() {

        ServerHeartbeat heartbeat = new ServerHeartbeat(UUID.randomUUID(), 1, List.of(
                new MatchData(UUID.randomUUID(), 0, "test")
        ));

        ByteBuf buffer = Unpooled.buffer(20);
        ServerHeartbeat.encode(buffer, heartbeat);

        ServerHeartbeat decodedHeartbeat = ServerHeartbeat.decode(buffer);

        System.out.println(heartbeat);
        System.out.println(decodedHeartbeat);


        Assertions.assertEquals(heartbeat, decodedHeartbeat);

    }

}
