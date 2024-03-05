package org.example.back.registery;

import java.util.List;

public interface ServerRegistry {
    List<Server> find(String key);
    void add(Server server);
    void remove(Server server);
}
