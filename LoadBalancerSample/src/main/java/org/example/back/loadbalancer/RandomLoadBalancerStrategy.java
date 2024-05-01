package org.example.back.loadbalancer;

import org.example.back.Server;
import org.example.back.registery.ServiceRegistry;

import java.util.Random;

public class RandomLoadBalancerStrategy implements LoadBalancerStrategy {

    private final Random random;
    private final ServiceRegistry serverMap;
    public RandomLoadBalancerStrategy(ServiceRegistry serverMap) {
        this.serverMap = serverMap;
        random = new Random();
    }
    @Override
    public Server getServer(String routeKey) {
        var serverList = serverMap.getServers(routeKey);
        if (serverList == null ||  serverList.isEmpty()) {
            return null;
        }
        var selectedIndex = random.nextInt(serverList.size());
        return serverList.get(selectedIndex);
    }
}
