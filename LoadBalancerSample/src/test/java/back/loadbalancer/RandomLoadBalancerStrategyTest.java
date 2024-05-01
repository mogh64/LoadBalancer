package back.loadbalancer;

import org.example.back.loadbalancer.RandomLoadBalancerStrategy;
import org.example.back.Server;
import org.example.back.registery.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RandomLoadBalancerStrategyTest {
    private static final String CONTEXT = "myapp";
    private RandomLoadBalancerStrategy randomLoadBalancerStrategy;
    @Mock
    private ServiceRegistry serverMap;
    private AutoCloseable autoCloseable;

    @Before
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        randomLoadBalancerStrategy = new RandomLoadBalancerStrategy(serverMap);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testSuccessfullServerSelection() {
        when(serverMap.getServers(anyString())).thenReturn(getServerList());
        var server =  randomLoadBalancerStrategy.getServer(CONTEXT);
        assertThat(server).isNotNull();
    }

    @Test
    public void testFailedServerSelection() {
        when(serverMap.getServers(anyString())).thenReturn(Collections.emptyList());
        var server =  randomLoadBalancerStrategy.getServer("None");
        assertThat(server).isNull();
    }
    private List<Server> getServerList() {
        String host = "localhost";

        return List.of(Server.builder().host(host).port(80).context(CONTEXT).build(),
                Server.builder().host(host).port(81).context(CONTEXT).build(),
                Server.builder().host(host).port(82).context(CONTEXT).build(),
                Server.builder().host(host).port(83).context(CONTEXT).build());
    }
}
