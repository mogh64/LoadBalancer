package org.example.back.loadbalancer;

import org.example.back.registery.Server;
import org.example.back.registery.ServerRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class WeightedRoundRobinLoadBalancerStrategy implements LoadBalancerStrategy {
    private static final int MAX_SERVICES = 100;
    private final ServerRegistry serverMap;
    private final Map<String,List<Server>> serverCapacityMap = new HashMap<>();;
    private final AtomicIntegerArray counters = new AtomicIntegerArray(MAX_SERVICES);
    public WeightedRoundRobinLoadBalancerStrategy(ServerRegistry serverMap) {
        this.serverMap = serverMap;
    }
    @Override
    public Server getServer(String routeKey) {
        var hasAvailableServerCapacity =  initServerCapacityMap(routeKey);

        if (!hasAvailableServerCapacity) {
            return null;
        }
        var servers = serverCapacityMap.get(routeKey);

        var currentIndex = counters.getAndIncrement(getCounterIndex(routeKey));
        var selectedIndex =  Math.abs(currentIndex % servers.size());
        return servers.get(selectedIndex);
    }

    private synchronized boolean initServerCapacityMap(String routeKey) {
        if (!serverCapacityMap.containsKey(routeKey)) {
            var serverList = serverMap.find(routeKey);
            if (serverList == null ||  serverList.isEmpty()) {
                return false;
            }
            List<Server> capacityList = new ArrayList<>();
            for (var server:serverList) {
                for(int i=0;i<server.getCapacity();i++)
                    capacityList.add(server);
            }
            serverCapacityMap.put(routeKey,capacityList);
        }
        return true;
    }
    private int getCounterIndex(String routeKey) {
        return Math.abs(routeKey.hashCode() % counters.length());
    }
}
