package org.example.back.registery;

import org.example.back.exception.ExceededServerRecordThresholdException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerRegistryImpl implements ServerRegistry {
    private static final int THRESHOLD = 10;
    private  final ConcurrentHashMap<String, ServerCountAndList> serverMap = new ConcurrentHashMap<>();
    private static class ServerCountAndList {
        private final AtomicInteger count = new AtomicInteger(0);
        private final CopyOnWriteArrayList<Server> servers = new CopyOnWriteArrayList<>();
    }
    public List<Server> find(String key) {
        var serverCountAndList = serverMap.get(key);
        return serverCountAndList!=null ? Collections.unmodifiableList(serverCountAndList.servers):Collections.emptyList();
    }

    public void add(Server server) {
        serverMap.compute(server.getContext(), (key, serverCountAndList) -> {
            if (serverCountAndList == null) {
                serverCountAndList = new ServerCountAndList();
            }
            if (serverCountAndList.count.incrementAndGet() > THRESHOLD) {
                throw new ExceededServerRecordThresholdException();
            }
            serverCountAndList.servers.addIfAbsent(server);
            return serverCountAndList;
        });
    }

    public void remove(Server server) {
        serverMap.computeIfPresent(server.getContext(), (key,serverCountAndList) -> {

            boolean removed = serverCountAndList.servers.remove(server);

            if(removed) {
                serverCountAndList.count.decrementAndGet();
            }

            return serverCountAndList;
        });
    }
}
