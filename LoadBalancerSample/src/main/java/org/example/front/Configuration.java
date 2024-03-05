package org.example.front;

import lombok.Getter;
import org.example.back.loadbalancer.LoadBalancerAlgorithmType;

public class Configuration {
    @Getter
    private final static LoadBalancerAlgorithmType algorithmType = LoadBalancerAlgorithmType.WeightedRoundRobin;

}
