package back;

import org.example.back.loadbalancer.RoundRobinLoadBalancerStrategy;
import org.example.back.registery.Server;
import org.example.back.registery.ServerRegistry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RoundRobinLoadBalancerStrategyTest {
    private static final String CONTEXT = "myapp";
    private RoundRobinLoadBalancerStrategy loadBalancerStrategy;
    @Mock
    private ServerRegistry serverMap;
    private AutoCloseable autoCloseable;

    @Before
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        loadBalancerStrategy = new RoundRobinLoadBalancerStrategy(serverMap);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testSuccessfullServerSelection() {
        when(serverMap.find(anyString())).thenReturn(getServerList(CONTEXT));
        var server =  loadBalancerStrategy.getServer(CONTEXT);
        assertThat(server).isNotNull();
    }

    @Test
    public void testSuccessfullRoundRobinServerSelectionForDifferentServiceName() {
        String anotherServiceName = "ANOTHER";
        var serverList1 = getServerList(CONTEXT);
        var serverList2 = getServerList(anotherServiceName);
        when(serverMap.find(CONTEXT)).thenReturn(serverList1);
        when(serverMap.find(anotherServiceName)).thenReturn(getServerList(anotherServiceName));
        var server1 =  loadBalancerStrategy.getServer(CONTEXT);
        var server2 = loadBalancerStrategy.getServer(anotherServiceName);
        assertThat(server1).isEqualTo(serverList1.get(0));
        assertThat(server2).isEqualTo(serverList2.get(0));
    }

    @Test
    public void testSuccessfullRoundRobinSelection() {
        var serverLists = getServerList(CONTEXT);
        when(serverMap.find(anyString())).thenReturn(serverLists);
        var server =  loadBalancerStrategy.getServer(CONTEXT);
        for(int i=0;i<serverLists.size();i++) {
            var server1 =  loadBalancerStrategy.getServer(CONTEXT);
            if (i==serverLists.size()-1) {
                assertThat(server1).isEqualTo(server);
                continue;
            }
            assertThat(server1).isNotEqualTo(server);
        }
    }

    @Test
    public void testSuccessfullRoundRobinSelectionWhenOnlyServerAvailable() {
        when(serverMap.find(anyString())).thenReturn(Collections.singletonList(getServerList(CONTEXT).get(0)));
        var server =  loadBalancerStrategy.getServer(CONTEXT);
        for (int i=0;i<10;i++) {
            var server1 =  loadBalancerStrategy.getServer(CONTEXT);
            assertThat(server1).isEqualTo(server);
        }
    }
    @Test
    public void testFailedWhenServerListIsEmpty() {
        when(serverMap.find(anyString())).thenReturn(Collections.emptyList());
        var server =  loadBalancerStrategy.getServer("None");
        assertThat(server).isNull();
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        final int threadCount = 100;
        final int iterationsPerThread = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        var serverList = getServerList(CONTEXT);
        // Mock serverMap behavior
        when(serverMap.find(anyString())).thenReturn(serverList);

        // Create and execute threads
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < iterationsPerThread; j++) {
                    try {
                        loadBalancerStrategy.getServer(CONTEXT);
                    } catch (Exception e) {
                        Assert.fail("Exception occurred during concurrent access: " + e.getMessage());
                    }
                }
            });
        }

        // Shutdown executor and wait for all threads to finish
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        var expectedServerIndex =  ((threadCount*iterationsPerThread)) % serverList.size();
        var expectedServer = serverList.get(expectedServerIndex);
        var selectedServer = loadBalancerStrategy.getServer(CONTEXT);
        assertThat(expectedServer).isEqualTo(selectedServer);

    }
    private List<Server> getServerList(String serviceName) {
        String host = "localhost";

        return List.of(Server.builder().host(host).port(80).context(serviceName).build(),
                Server.builder().host(host).port(81).context(serviceName).build(),
                Server.builder().host(host).port(82).context(serviceName).build(),
                Server.builder().host(host).port(83).context(serviceName).build());
    }
}
