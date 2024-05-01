package org.example.front;

import org.example.back.loadbalancer.LoadBalancerAlgorithmType;
import org.example.back.loadbalancer.LoadBalancerStrategy;
import org.example.back.loadbalancer.RandomLoadBalancerStrategy;
import org.example.back.Server;
import org.example.back.registery.ServiceRegistryFactory;

public class LoadBalancer {
    private final LoadBalancerStrategy balancerStrategy;

    public LoadBalancer() {
        this.balancerStrategy = new RandomLoadBalancerStrategy(ServiceRegistryFactory.getServerRegistry());
    }
    public LoadBalancer(LoadBalancerAlgorithmType algorithmType) {
        balancerStrategy = algorithmType.getStrategy();
    }
    public Server getDestinationServer(String routeKey) {
        return balancerStrategy.getServer(routeKey);
    }

}
