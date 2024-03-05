package back;

import org.example.back.registery.Server;
import org.example.back.registery.ServerRegistry;
import org.example.back.registery.ServerRegistryImpl;
import org.example.back.exception.ExceededServerRecordThresholdException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ServerRegistryImplTest {
    private static final int RECORD_THRESHOLD = 10;
    private static final String SERVICE_NAME = "myservice";
    private static final String SCHEMA = "http";
    private ServerRegistry serviceRegistry;
    @Before
    public void setUp() {
        serviceRegistry = new ServerRegistryImpl();
    }

    @Test
    public void testSuccessfullyAddServer() {
        var server = getServer(SERVICE_NAME);
        serviceRegistry.add(server);
        var selectedServers = serviceRegistry.find(SERVICE_NAME);
        assertThat(selectedServers.size()).isEqualTo(1);
        assertThat(selectedServers.get(0)).isEqualTo(server);
    }
    @Test
    public void testSuccessfullyAddServerForDifferentServices() {
        String anotherServiceName = "ANOTHER";
        var server1 = getServer(SERVICE_NAME);
        var server2 = getServer(anotherServiceName);
        serviceRegistry.add(server1);
        serviceRegistry.add(server2);
        var selectedServers1 = serviceRegistry.find(SERVICE_NAME);
        var selectedServers2 = serviceRegistry.find(anotherServiceName);

        assertThat(selectedServers1.size()).isEqualTo(1);
        assertThat(selectedServers1.get(0)).isEqualTo(server1);

        assertThat(selectedServers2.size()).isEqualTo(1);
        assertThat(selectedServers2.get(0)).isEqualTo(server2);
    }
    @Test
    public void testSuccessfullyAddMultipleServerForService() {
        var servers = getServers(SERVICE_NAME);
        for(var server:servers) {
            serviceRegistry.add(server);
        }

        var selectedServers = serviceRegistry.find(SERVICE_NAME);
        assertThat(selectedServers.size()).isEqualTo(servers.size());
    }
    @Test
    public void testNoAddingDuplicateServer() {
        var server1 = getServer(SERVICE_NAME);
        var server2 = getServer(SERVICE_NAME);
        serviceRegistry.add(server1);
        serviceRegistry.add(server2);
        var selectedServers = serviceRegistry.find(SERVICE_NAME);
        assertThat(selectedServers.size()).isEqualTo(1);
    }
    @Test
    public void testFailAddingMoreThanThresholdServerForService() {
        for(int i=0;i<RECORD_THRESHOLD;i++) {
            var port = 80 + i;
            serviceRegistry.add(getServer(SERVICE_NAME,port));
        }
        assertThatThrownBy(()-> serviceRegistry.add(getServer(SERVICE_NAME,100))).isInstanceOf(ExceededServerRecordThresholdException.class);
    }
    @Test
    public void testSuccessfullyRemovedServer() {
        var server = getServer(SERVICE_NAME,80);
        serviceRegistry.add(server);
        var removalServer =  getServer(SERVICE_NAME,80);
        serviceRegistry.remove(removalServer);
        var serverLists =  serviceRegistry.find(SERVICE_NAME);
        assertThat(serverLists.size()).isEqualTo(0);
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        // Create a ServiceRegistryImpl instance
        int taskCount = 100000;

        // Create an ExecutorService with multiple threads
        ExecutorService executorService = Executors.newFixedThreadPool(16);

        AtomicInteger portA = new AtomicInteger(1);
        AtomicInteger portB = new AtomicInteger(1);
        var serviceNameA = SERVICE_NAME+"A";
        var serviceNameB = SERVICE_NAME+"B";

        // Submit tasks to the executor
        for (int i = 0; i < taskCount; i++) {
            executorService.execute(() -> {
                // Each task simulates concurrent access to the ServiceRegistry methods
                // Add some servers
                var port = portA.getAndIncrement();
                try {

                    try {
                        serviceRegistry.add(getServer(serviceNameA,port));
                        serviceRegistry.add(getServer(serviceNameB,portB.getAndIncrement()));

                    } catch (ExceededServerRecordThresholdException ignored) {
                        //Ignored to prevent from inconsistent behaviour in test
                    }
                    serviceRegistry.remove(getServer(serviceNameA,port));
                    // Find servers
                    serviceRegistry.find(serviceNameB);
                } catch (Exception e) {
                    Assert.fail("Exception occurred during concurrent access: " + e.getMessage());
                }
            });
        }

        // Shutdown the executor and wait for all tasks to complete
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);


        // Assert something to demonstrate that the ServiceRegistry is functioning correctly
        assertThat(serviceRegistry.find(serviceNameA).size()).isEqualTo(0);
        assertThat(serviceRegistry.find(serviceNameB).size()).isEqualTo(RECORD_THRESHOLD );

    }

    private Server getServer(String serviceName) {
        return Server.builder().host("localhost").port(80).context(serviceName).schema(SCHEMA).build();
    }

    private Server getServer(String serviceName,int port) {
        return Server.builder().host("localhost").port(port).context(serviceName).schema(SCHEMA).build();
    }

    private List<Server> getServers(String serviceName) {
        return List.of(Server.builder().host("localhost").port(80).context(serviceName).schema(SCHEMA).build(),
                Server.builder().host("localhost").port(81).context(serviceName).schema(SCHEMA).build(),
                Server.builder().host("localhost").port(82).context(serviceName).schema(SCHEMA).build());
    }
}
