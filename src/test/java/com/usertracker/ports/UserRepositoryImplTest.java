package com.usertracker.ports;

import com.usertracker.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryImplTest {

    private UserRepositoryImpl repository;

    @Before
    public void setUp() throws Exception {
        repository = new UserRepositoryImpl();
    }

    @Test
    public void beAbleToRegisterAUserAndRetrieveItAgain() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User();

        // when
        repository.registerUser(userId, user);

        // then
        Optional<User> returnedUser = repository.getUser(userId);
        assertThat(returnedUser.isPresent()).isTrue();
        assertThat(returnedUser.get()).isEqualTo(user);
    }

    @Test
    public void returnAnOptionalEmptyWhenUserIsNotRegistered() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when
        Optional<User> user = repository.getUser(userId);

        // then
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    public void beAbleToRegisterAnAdminUser() throws Exception {
        // given
        UUID adminId = UUID.randomUUID();

        // when
        repository.registerAdminUser(adminId);

        // then
        assertThat(repository.adminUserAlreadyRegistered(adminId)).isTrue();
    }

    @Test
    public void revealIfAnAdminUserIsNotRegistered() throws Exception {
        // when
        boolean result = repository.adminUserAlreadyRegistered(UUID.randomUUID());

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void exposeAllUsersWhenCalled() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        repository.registerUser(userId, new User());

        // when
        Set<UUID> users = repository.getUsers();

        // then
        assertThat(users.size()).isEqualTo(1);
        assertThat(users).contains(userId);
    }
}
