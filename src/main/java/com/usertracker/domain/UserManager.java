package com.usertracker.domain;

import com.usertracker.domain.exceptions.InvalidUserIdException;

import java.util.*;

public class UserManager {
    private UserRepository repository;

    public UserManager(UserRepository repository) {
        this.repository = repository;
    }

    public boolean registerUser(UUID userId) {
        if (repository.getUser(userId).isPresent()) {
            return false;
        }

        repository.registerUser(userId, new User());
        return true;
    }

    public boolean registerAdminUser(UUID adminId) {
        if (repository.adminUserAlreadyRegistered(adminId)) {
            return false;
        }

        repository.registerAdminUser(adminId);
        return true;
    }

    public Set<UUID> getUsers(UUID userId) throws InvalidUserIdException {
        if (!repository.getUser(userId).isPresent()) {
            throw new InvalidUserIdException(String.format("%s user is not present in system.", userId));
        }

        return repository.getUsers();
    }

    public Set<UUID> getUsersConnections(UUID adminId, UUID userId)
            throws InvalidUserIdException {

        if (!repository.adminUserAlreadyRegistered(adminId)) {
            throw new InvalidUserIdException(String.format("%s is not a valid admin", adminId));
        }

        return optionalUserConnections(userId);
    }

    private Set<UUID> optionalUserConnections(UUID userId)
            throws InvalidUserIdException {

        Optional<User> user = repository.getUser(userId);

        if (!user.isPresent()) {
            throw new InvalidUserIdException(String.format("%s is not a valid user", userId));
        }

        return user.get().getConnections();
    }

    public void connectUsers(UUID userId, UUID connectionId) throws InvalidUserIdException {
        Optional<User> user = repository.getUser(userId);

        if (!user.isPresent()) {
            throw new InvalidUserIdException(String.format("%s is not a valid user.", userId));
        } else if (!repository.getUser(connectionId).isPresent()) {
            throw new InvalidUserIdException(String.format("%s is not a valid user.", connectionId));
        }

        user.get().connectTo(connectionId);
    }
}
