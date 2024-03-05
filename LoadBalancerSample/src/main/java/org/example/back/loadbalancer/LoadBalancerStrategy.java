package org.example.back.loadbalancer;

import org.example.back.registery.Server;

public interface LoadBalancerStrategy {
    Server getServer(String routeKey);
}
