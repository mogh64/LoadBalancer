package org.example.back.loadbalancer;

import org.example.back.registery.Server;
import org.example.back.registery.ServerRegistry;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class RoundRobinLoadBalancerStrategy implements LoadBalancerStrategy {
    private static final int MAX_SERVICES = 100;
    private final ServerRegistry serverMap;
    private final AtomicIntegerArray counters = new AtomicIntegerArray(MAX_SERVICES);
    public RoundRobinLoadBalancerStrategy(ServerRegistry serverMap) {

        this.serverMap = serverMap;
    }
    @Override
    public Server getServer(String routeKey) {

        var serverList = serverMap.find(routeKey);

        if (serverList == null ||  serverList.isEmpty()) {
            return null;
        }
        var currentIndex = counters.getAndIncrement(getCounterIndex(routeKey));

        var selectedIndex =  Math.abs(currentIndex % serverList.size());

        return serverList.get(selectedIndex);
    }
    private int getCounterIndex(String routeKey) {
        return Math.abs(routeKey.hashCode() % counters.length());
    }
}
