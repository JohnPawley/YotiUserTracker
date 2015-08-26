package com.usertracker.domain;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepository {

    public void registerAdminUser(UUID adminId);

    public void registerUser(UUID userId, User user);

    public Optional<User> getUser(UUID userId);

    public boolean adminUserAlreadyRegistered(UUID adminId);

    public Set<UUID> getUsers();
}
