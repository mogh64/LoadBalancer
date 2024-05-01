package org.example.back.registery;

import org.example.back.Configuration;
import org.example.back.Server;
import org.example.back.exception.ExceededServerRecordThresholdException;


import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceRegistryImpl implements ServiceRegistry {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Server>> serverMap = new ConcurrentHashMap<>();
    @Override
    public void add(Server server) {
        serverMap.compute(server.getContext(), (key,servers) -> {
            if(servers == null) {
                servers = new CopyOnWriteArrayList<>();
            }
            if(!servers.contains(server)) {
                if(servers.size() + 1 > Configuration.SERVER_PER_SERVICE_THRESHOLD) {
                    throw new ExceededServerRecordThresholdException();
                }
                servers.add(server);
            }
            return servers;
        });

    }

    @Override
    public void remove(Server server) {
        serverMap.computeIfPresent(server.getContext(), (key,servers) -> {
            servers.remove(server);

            return servers;
        });
    }

    @Override
    public List<Server> getServers(String serviceName) {
        var servers = serverMap.get(serviceName);
        return servers != null ? Collections.unmodifiableList(servers) : Collections.emptyList() ;
    }
}
