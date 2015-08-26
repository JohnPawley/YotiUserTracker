package com.usertracker.ports;

import com.usertracker.domain.User;

import java.util.*;

public class UserRepositoryImpl implements com.usertracker.domain.UserRepository {
    Map<UUID, User> users;
    Set<UUID> admins;

    public UserRepositoryImpl() {
        users = new HashMap<UUID, User>();
        admins = new HashSet<UUID>();
    }

    @Override
    public void registerAdminUser(UUID adminId) {
        admins.add(adminId);
    }

    @Override
    public void registerUser(UUID userId, User user) {
        users.put(userId, user);
    }

    @Override
    public Optional<User> getUser(UUID userId) {
        if (!users.containsKey(userId)) {
            return Optional.empty();
        }

        return Optional.of(users.get(userId));
    }

    @Override
    public boolean adminUserAlreadyRegistered(UUID adminId) {
        if (admins.contains(adminId)) {
            return true;
        }

        return false;
    }

    @Override
    public Set<UUID> getUsers() {
        return users.keySet();
    }
}
