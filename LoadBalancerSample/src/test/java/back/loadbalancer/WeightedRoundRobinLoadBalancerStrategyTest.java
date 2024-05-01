package back.loadbalancer;

import org.example.back.loadbalancer.WeightedRoundRobinLoadBalancerStrategy;
import org.example.back.Server;
import org.example.back.registery.ServiceRegistry;
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

public class WeightedRoundRobinLoadBalancerStrategyTest {
    private static final String CONTEXT = "myapp";
    private WeightedRoundRobinLoadBalancerStrategy loadBalancerStrategy;
    @Mock
    private ServiceRegistry serverMap;
    private AutoCloseable autoCloseable;

    @Before
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        loadBalancerStrategy = new WeightedRoundRobinLoadBalancerStrategy(serverMap);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testSuccessfullWeightedRoundRobinSelection() {
        var serverLists = getServerList(CONTEXT);
        when(serverMap.getServers(anyString())).thenReturn(serverLists);
        for (Server server : serverLists) {
            for (int j = 0; j < server.getCapacity(); j++) {
                var selectedServer = loadBalancerStrategy.getServer(CONTEXT);
                assertThat(server).isEqualTo(selectedServer);
            }
        }
        for (Server server : serverLists) {
            for (int j = 0; j < server.getCapacity(); j++) {
                var selectedServer = loadBalancerStrategy.getServer(CONTEXT);
                assertThat(server).isEqualTo(selectedServer);
            }
        }
    }

    @Test
    public void testSuccessfullWeightedRoundRobinServerSelectionForDifferentServiceName() {
        String anotherServiceName = "ANOTHER";
        var serverList1 = getServerList(CONTEXT);
        var serverList2 = getServerList(anotherServiceName);
        when(serverMap.getServers(CONTEXT)).thenReturn(serverList1);
        when(serverMap.getServers(anotherServiceName)).thenReturn(getServerList(anotherServiceName));
        var server1 =  loadBalancerStrategy.getServer(CONTEXT);
        var server2 = loadBalancerStrategy.getServer(anotherServiceName);
        assertThat(server1).isEqualTo(serverList1.get(0));
        assertThat(server2).isEqualTo(serverList2.get(0));
    }

    @Test
    public void testFailedWhenServerListIsEmpty() {
        when(serverMap.getServers(anyString())).thenReturn(Collections.emptyList());
        var server =  loadBalancerStrategy.getServer("None");
        assertThat(server).isNull();
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        var serverList = getServerList(CONTEXT);

        final int threadCount = 100 ;
        final int iterationsPerThread = 1000 * serverList.size() * serverList.stream().mapToInt(Server::getCapacity).sum();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);


        // Mock serverMap behavior
        when(serverMap.getServers(anyString())).thenReturn(serverList);

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


        var expectedServer = serverList.get(0);
        var selectedServer = loadBalancerStrategy.getServer(CONTEXT);
        assertThat(expectedServer).isEqualTo(selectedServer);

    }
    private List<Server> getServerList(String context) {
        String host = "localhost";

        return List.of(Server.builder().host(host).port(80).context(context).capacity(1).build(),
                Server.builder().host(host).port(81).context(context).capacity(2).build(),
                Server.builder().host(host).port(82).context(context).capacity(3).build(),
                Server.builder().host(host).port(83).context(context).capacity(4).build());
    }
}
