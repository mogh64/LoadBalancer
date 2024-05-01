package org.example.back.registery;

import org.example.back.Server;

import java.util.List;

public interface ServiceRegistry {
    List<Server> getServers(String key);
    void add(Server server);
    void remove(Server server);
}
