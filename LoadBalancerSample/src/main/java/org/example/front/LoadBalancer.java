package org.example.front;

import org.example.back.loadbalancer.LoadBalancerAlgorithmType;
import org.example.back.loadbalancer.LoadBalancerStrategy;
import org.example.back.loadbalancer.RandomLoadBalancerStrategy;
import org.example.back.registery.Server;
import org.example.back.registery.ServerRegistryFactory;

public class LoadBalancer {
    private final LoadBalancerStrategy balancerStrategy;

    public LoadBalancer() {
        this.balancerStrategy = new RandomLoadBalancerStrategy(ServerRegistryFactory.getServerRegistry());
    }
    public LoadBalancer(LoadBalancerAlgorithmType algorithmType) {
        balancerStrategy = algorithmType.getStrategy();
    }
    public Server getDestinationServer(String routeKey) {
        return balancerStrategy.getServer(routeKey);
    }

}
