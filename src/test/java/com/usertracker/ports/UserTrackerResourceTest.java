package com.usertracker.ports;

import com.usertracker.domain.UserManager;
import com.usertracker.domain.exceptions.InvalidUserIdException;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UserTrackerResourceTest {
    UserTrackerResource resource;
    private UserManager mockManager;

    @Before
    public void setUp() throws Exception {
        mockManager = mock(UserManager.class);
        resource = new UserTrackerResource(mockManager);
    }

    @Test
    public void createUserForANewUUIDShouldCallToManagerAndReturnCreatedResponse() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(mockManager.registerUser(userId)).willReturn(true);

        // when
        Response response = resource.createUser(userId.toString());

        // then
        verify(mockManager).registerUser(userId);
        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test
    public void createUserForAnOldUUIDShouldCallToManagerAndReturnOkResponse() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(mockManager.registerUser(userId)).willReturn(false);

        // when
        Response response = resource.createUser(userId.toString());

        // then
        verify(mockManager).registerUser(userId);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldReturnA400ResponseIfTheUUIDIsNotAValidOne() throws Exception {
        // when
        Response response = resource.createUser("INVALID");

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    public void shouldBeAbleToGetAllUsersWithAValidUserIdHeader() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        HashSet<UUID> users = new HashSet<UUID>();
        users.add(userId);
        given(mockManager.getUsers(userId)).willReturn(users);

        // when
        Response response = resource.getUsers(userId.toString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat((String) response.getEntity()).isEqualTo("[\"" + userId + "\"]");
    }

    @Test
    public void shouldReturnAnUnauthorisedResponseWhenUserIsNotRegistered() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(mockManager.getUsers(userId)).willThrow(new InvalidUserIdException(""));

        // when
        Response response = resource.getUsers(userId.toString());

        // then
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void shouldReturnABadRequestResponseIfTheUserIdHeaderIsNotAValidUUID() throws Exception {
        // when
        Response response = resource.getUsers("INVALID");

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    public void createAdminForANewUUIDShouldCallToManagerAndReturnCreatedResponse() throws Exception {
        // given
        UUID adminId = UUID.randomUUID();
        given(mockManager.registerAdminUser(adminId)).willReturn(true);

        // when
        Response response = resource.createAdmin(adminId.toString());

        // then
        verify(mockManager).registerAdminUser(adminId);
        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test
    public void createAdminForAnOldUUIDShouldCallToManagerAndReturnOkResponse() throws Exception {
        // given
        UUID adminId = UUID.randomUUID();
        given(mockManager.registerAdminUser(adminId)).willReturn(false);

        // when
        Response response = resource.createAdmin(adminId.toString());

        // then
        verify(mockManager).registerAdminUser(adminId);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldReturnA400ResponseIfTheUUIDIsNotAValidOneForAdmin() throws Exception {
        // when
        Response response = resource.createAdmin("INVALID");

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    public void shouldReturnA200WhenAskedToMakeAConnectionBetweenTwoValidUsers() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();

        // when
        Response response = resource.connectUsers(userId.toString(), connectionId.toString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        verify(mockManager).connectUsers(userId, connectionId);
    }

    @Test
    public void shouldReturnA400IfAnBadlyFormattedUserIdOrConnectionIdIsPassed() throws Exception {
        // when
        Response response = resource.connectUsers("INVALID", "INVALID");

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    public void shouldReturnA400IfTheMockManagerCannotConnectTheTwoUsers() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        doThrow(new InvalidUserIdException("")).when(mockManager).connectUsers(userId, connectionId);

        // when
        Response response = resource.connectUsers(userId.toString(), connectionId.toString());

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    public void getConnectionsShouldReturnA200AndTheCorrectConnectionsForASpecificUser() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        Set<UUID> connectionSet = new HashSet<UUID>();
        connectionSet.add(connectionId);

        given(mockManager.getUsersConnections(adminId, userId)).willReturn(connectionSet);

        // when
        Response response = resource.getConnections(userId.toString(), adminId.toString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat((String) response.getEntity()).isEqualTo("[\"" + connectionId + "\"]");
    }

    @Test
    public void getConnectionsShouldReturnA400IfEitherOfTheUUIDsCantBeParsed() throws Exception {
        // when
        Response response = resource.getConnections("INVALID", "INVALID");

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    public void getConnectionsShouldReturnA401IfManagerThrowsAnException() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        given(mockManager.getUsersConnections(adminId, userId))
                .willThrow(new InvalidUserIdException(""));

        // when
        Response response = resource.getConnections(userId.toString(), adminId.toString());

        // then
        assertThat(response.getStatus()).isEqualTo(401);
    }
}