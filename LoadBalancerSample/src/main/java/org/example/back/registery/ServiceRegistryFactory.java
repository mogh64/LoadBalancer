package org.example.back.registery;

import lombok.Getter;

public class ServiceRegistryFactory {
    @Getter
    private static final ServiceRegistry serverRegistry = new ServiceRegistryImpl();

}
