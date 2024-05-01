package org.example.back.loadbalancer;

import lombok.Getter;
import org.example.back.registery.ServiceRegistryFactory;

@Getter
public enum LoadBalancerAlgorithmType {
    Random(new RandomLoadBalancerStrategy(ServiceRegistryFactory.getServerRegistry())),
    RoundRobin(new RoundRobinLoadBalancerStrategy(ServiceRegistryFactory.getServerRegistry())),
    WeightedRoundRobin(new WeightedRoundRobinLoadBalancerStrategy(ServiceRegistryFactory.getServerRegistry()));

    private final LoadBalancerStrategy strategy;

    LoadBalancerAlgorithmType(LoadBalancerStrategy strategy) {
        this.strategy = strategy;
    }
}
