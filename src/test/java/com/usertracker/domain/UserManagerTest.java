package com.usertracker.domain;

import com.usertracker.domain.exceptions.InvalidUserIdException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserManagerTest {

    private UserManager userManager;
    private UserRepository mockRepository;

    @Before
    public void setUp() throws Exception {
        mockRepository = mock(UserRepository.class);
        userManager = new UserManager(mockRepository);
    }

    @Test
    public void theUserManagerShouldRegisterANewUserAndReturnTrueWhenNotAlreadyPresent() throws Exception {
        // given
        UUID uuid = UUID.randomUUID();
        when(mockRepository.getUser(uuid)).thenReturn(Optional.<User>empty());

        // when
        boolean result = userManager.registerUser(uuid);

        // then
        assertThat(result).isTrue();
        verify(mockRepository).registerUser(eq(uuid), any(User.class));
    }

    @Test
    public void theUserManagerShouldNotRegisterANewUserAndReturnFalseWhenAlreadyPresent() throws Exception {
        // given
        UUID uuid = UUID.randomUUID();
        when(mockRepository.getUser(uuid)).thenReturn(Optional.of(new User()));

        // when
        boolean result = userManager.registerUser(uuid);

        // then
        assertThat(result).isFalse();
        verify(mockRepository, never()).registerUser(eq(uuid), any(User.class));
    }

    @Test
    public void theUserManagerShouldBeAbleToRegisterAdminUsers() throws Exception {
        // given
        UUID adminUUID = UUID.randomUUID();
        when(mockRepository.adminUserAlreadyRegistered(adminUUID)).thenReturn(false);

        // when
        boolean result = userManager.registerAdminUser(adminUUID);

        // then
        assertThat(result).isTrue();
        verify(mockRepository).registerAdminUser(adminUUID);
    }

    @Test
    public void theUserManagerShouldNotBeAbleToRegisterAdminUsersIfTheyAlreadyExist() throws Exception {
        // given
        UUID adminUUID = UUID.randomUUID();
        when(mockRepository.adminUserAlreadyRegistered(adminUUID)).thenReturn(true);

        // when
        boolean result = userManager.registerAdminUser(adminUUID);

        // then
        assertThat(result).isFalse();
        verify(mockRepository, never()).registerAdminUser(adminUUID);
    }

    @Test
    public void aValidAdminUserShouldBeAbleToGetTheConnectionsOfAUser() throws Exception {
        // given
        UUID adminUUID = UUID.randomUUID();
        UUID userUUID = UUID.randomUUID();
        User user = new User();
        UUID connectionUUID = UUID.randomUUID();
        user.connectTo(connectionUUID);

        when(mockRepository.adminUserAlreadyRegistered(adminUUID)).thenReturn(true);
        when(mockRepository.getUser(userUUID)).thenReturn(Optional.of(user));

        // when
        Set<UUID> results = userManager.getUsersConnections(adminUUID, userUUID);

        // then
        assertThat(results).isEqualTo(user.getConnections());
    }

    @Test(expected = InvalidUserIdException.class)
    public void aValidAdminUserShouldGetAnInvalidUserIdExceptionIfTheUserPassedIsNotRegistered() throws Exception {
        // given
        UUID adminUUID = UUID.randomUUID();
        UUID userUUID = UUID.randomUUID();

        when(mockRepository.adminUserAlreadyRegistered(adminUUID)).thenReturn(true);
        when(mockRepository.getUser(userUUID)).thenReturn(Optional.<User>empty());

        // when
        userManager.getUsersConnections(adminUUID, userUUID);
    }

    @Test(expected = InvalidUserIdException.class)
    public void anInvalidAdminUserShouldThrowAnInvalidAdminException() throws Exception {
        // given
        UUID adminUUID = UUID.randomUUID();
        when(mockRepository.adminUserAlreadyRegistered(adminUUID)).thenReturn(false);

        // when
        userManager.getUsersConnections(adminUUID, UUID.randomUUID());
    }

    @Test
    public void shouldReturnAllUsersAsAnOptionalIfAnyAreInRepositoryAndAValidUserIdPassed() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        when(mockRepository.getUser(userId)).thenReturn(Optional.of(new User()));
        HashSet<UUID> users = new HashSet<UUID>();
        users.add(userId);
        when(mockRepository.getUsers()).thenReturn(users);

        // when
        Set<UUID> returnedUsers = userManager.getUsers(userId);

        // then
        assertThat(returnedUsers.size()).isEqualTo(1);
        assertThat(returnedUsers).contains(userId);
    }

    @Test(expected = InvalidUserIdException.class)
    public void shouldThrowAnInvalidUserIdExceptionIfUserIsNotRegistered() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        when(mockRepository.getUser(userId)).thenReturn(Optional.<User>empty());

        // when
        userManager.getUsers(userId);
    }

    @Test
    public void shouldConnectTwoUsersTogetherWhenValidIdsArePassed() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        User user = new User();

        when(mockRepository.getUser(userId)).thenReturn(Optional.of(user));
        when(mockRepository.getUser(connectionId)).thenReturn(Optional.of(new User()));

        // when
        userManager.connectUsers(userId, connectionId);

        // then
        assertThat(user.getConnections()).contains(connectionId);
    }

    @Test(expected = InvalidUserIdException.class)
    public void throwsAnInvalidUserIdExceptionIfTheUserIdIsNotPresentInRepository() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        when(mockRepository.getUser(userId)).thenReturn(Optional.<User>empty());

        // then
        userManager.connectUsers(userId, UUID.randomUUID());
    }

    @Test(expected = InvalidUserIdException.class)
    public void throwsAnInvalidUserIdExceptionIfTheConnectionIdIsNotPresentInRepository() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        when(mockRepository.getUser(userId)).thenReturn(Optional.of(new User()));
        when(mockRepository.getUser(connectionId)).thenReturn(Optional.<User>empty());

        // then
        userManager.connectUsers(userId, connectionId);
    }
}