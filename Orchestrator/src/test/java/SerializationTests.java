import com.readutf.matchmaker.packet.packets.ServerRegisterPacket;
import com.readutf.matchmaker.packet.serializers.ServerRegisterSerializer;
import com.readutf.matchmaker.server.Server;
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

        Server server = new Server(UUID.randomUUID(), 25, "localhost", "test", 25565, System.currentTimeMillis(), new HashMap<>());

        ByteBuf byteBuf = Unpooled.buffer(20);
        Server.encodeServer(server, byteBuf);

        Server decoded = Server.decodeServer(byteBuf);

        Assertions.assertEquals(server, decoded);
    }

    @Test
    void serverRegisterSerialization() {

        ServerRegisterSerializer serializer = new ServerRegisterSerializer();

        Server server = new Server(UUID.randomUUID(), 25, "localhost", "test", 25565, System.currentTimeMillis(), new HashMap<>());
        ServerRegisterPacket data = new ServerRegisterPacket(List.of(server));

        ByteBuf encode = serializer.encode(data);
        ServerRegisterPacket decoded = serializer.decode(encode);

        Assertions.assertEquals(data, decoded);
    }

}
