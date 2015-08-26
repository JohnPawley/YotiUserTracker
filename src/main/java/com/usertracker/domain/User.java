package com.usertracker.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    private Set<UUID> connections;

    public User() {
        connections = new HashSet<UUID>();
    }

    public void connectTo(UUID potentialConnection) {
        connections.add(potentialConnection);
    }

    public Set<UUID> getConnections() {
        return connections;
    }
}
