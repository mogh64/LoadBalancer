package org.example.back.loadbalancer;

import org.example.back.Server;

public interface LoadBalancerStrategy {
    Server getServer(String routeKey);
}
