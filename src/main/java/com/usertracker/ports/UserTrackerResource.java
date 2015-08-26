package com.usertracker.ports;

import com.google.gson.Gson;
import com.usertracker.domain.UserManager;
import com.usertracker.domain.exceptions.InvalidUserIdException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Set;
import java.util.UUID;

@Path("/UserTracker")
public class UserTrackerResource {

    private final UserManager manager;

    public UserTrackerResource(UserManager manager) {
        this.manager = manager;
    }

    @POST
    @Path("/users/{userId}")
    public Response createUser(@PathParam("userId") String userId) {
        UUID inputId;
        try {
            inputId = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return Response.status(400).build();
        }

        if (manager.registerUser(inputId)) {
            return Response.status(201).build();
        } else {
            return Response.ok().build();
        }
    }

    @GET
    @Path("/users")
    public Response getUsers(@HeaderParam("User-Id") String userId) {
        UUID inputId;
        Set<UUID> users;

        try {
            inputId = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return Response.status(400).build();
        }

        try {
            users = manager.getUsers(inputId);
        } catch (InvalidUserIdException e) {
            return Response.status(401).build();
        }

        return Response.ok(new Gson()
                .toJson(users)).build();
    }

    @PUT
    @Path("/users/{userId}/connections/{connectionId}")
    public Response connectUsers(@PathParam("userId") String userId,
                                 @PathParam("connectionId") String connectionId) {
        UUID inputUserId;
        UUID inputConnectionId;

        try {
            inputUserId = UUID.fromString(userId);
            inputConnectionId = UUID.fromString(connectionId);
        } catch (IllegalArgumentException e) {
            return Response.status(400).build();
        }

        try {
            manager.connectUsers(inputUserId, inputConnectionId);
        } catch (InvalidUserIdException e) {
            return Response.status(400).build();
        }

        return Response.ok().build();
    }

    @POST
    @Path("/admins/{adminId}")
    public Response createAdmin(@PathParam("adminId") String adminId) {
        UUID inputId;
        try {
            inputId = UUID.fromString(adminId);
        } catch (IllegalArgumentException e) {
            return Response.status(400).build();
        }

        if (manager.registerAdminUser(inputId)) {
            return Response.status(201).build();
        } else {
            return Response.ok().build();
        }
    }

    @GET
    @Path("/users/{userId}/connections")
    public Response getConnections(@PathParam("userId") String userId,
                                   @HeaderParam("Admin-Id") String adminId) {
        UUID inputUserId;
        UUID inputAdminId;

        try {
            inputUserId = UUID.fromString(userId);
            inputAdminId = UUID.fromString(adminId);
        } catch (IllegalArgumentException e) {
            return Response.status(400).build();
        }

        Set<UUID> usersConnections;

        try {
            usersConnections = manager.getUsersConnections(inputAdminId, inputUserId);
        } catch (InvalidUserIdException e) {
            return Response.status(401).build();
        }

        return Response.ok(new Gson().toJson(usersConnections)).build();
    }
}
