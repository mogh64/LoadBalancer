package org.example.back.registery;

import lombok.Getter;

public class ServerRegistryFactory {
    @Getter
    private static final ServerRegistry serverRegistry = new ServerRegistryImpl();

}
