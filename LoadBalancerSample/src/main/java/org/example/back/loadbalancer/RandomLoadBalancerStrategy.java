package org.example.back.loadbalancer;

import org.example.back.registery.Server;
import org.example.back.registery.ServerRegistry;

import java.util.Random;

public class RandomLoadBalancerStrategy implements LoadBalancerStrategy {

    private final Random random;
    private final ServerRegistry serverMap;
    public RandomLoadBalancerStrategy(ServerRegistry serverMap) {
        this.serverMap = serverMap;
        random = new Random();
    }
    @Override
    public Server getServer(String routeKey) {
        var serverList = serverMap.find(routeKey);
        if (serverList == null ||  serverList.isEmpty()) {
            return null;
        }
        var selectedIndex = random.nextInt(serverList.size());
        return serverList.get(selectedIndex);
    }
}
