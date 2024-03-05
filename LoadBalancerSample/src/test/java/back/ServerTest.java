package back;

import org.example.back.registery.Server;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerTest {
    private static final String CONTEXT = "myapp";
    private static final String HOST = "myhost";
    private static final String SCHEMA = "https";
    private static final Integer PORT = 6060;

    @Test
    public void testGetAddressSuccessfullyWhenServerHasPort() {
        Server server = Server.builder().schema(SCHEMA).host(HOST).port(PORT).context(CONTEXT).build();
        String expectedUri = SCHEMA + "://" + HOST + ":" + PORT + "/" + CONTEXT + "/";
        assertThat(server.getAddress()).isEqualTo(expectedUri);
    }

    @Test
    public void testGetAddressSuccessfullyWhenServerNotHavePort() {
        Server server = Server.builder().schema(SCHEMA).host(HOST).context(CONTEXT).build();
        String expectedUri = SCHEMA + "://" + HOST + "/" + CONTEXT + "/";
        assertThat(server.getAddress()).isEqualTo(expectedUri);
    }
}
