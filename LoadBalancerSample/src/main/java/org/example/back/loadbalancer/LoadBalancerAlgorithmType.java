package org.example.back.loadbalancer;

import lombok.Getter;
import org.example.back.registery.ServerRegistryFactory;

@Getter
public enum LoadBalancerAlgorithmType {
    Random(new RandomLoadBalancerStrategy(ServerRegistryFactory.getServerRegistry())),
    RoundRobin(new RoundRobinLoadBalancerStrategy(ServerRegistryFactory.getServerRegistry())),
    WeightedRoundRobin(new WeightedRoundRobinLoadBalancerStrategy(ServerRegistryFactory.getServerRegistry()));

    private final LoadBalancerStrategy strategy;

    LoadBalancerAlgorithmType(LoadBalancerStrategy strategy) {
        this.strategy = strategy;
    }
}
