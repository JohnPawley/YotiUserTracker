package com.usertracker.domain;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    @Test
    public void aUserShouldBeAbleToConnectToAnotherUser() throws Exception {
        // given
        User user = new User();
        UUID potentialConnection = UUID.randomUUID();

        // when
        user.connectTo(potentialConnection);

        // then
        assertThat(user.getConnections().size()).isEqualTo(1);
        assertThat(user.getConnections()).contains(potentialConnection);
    }
}