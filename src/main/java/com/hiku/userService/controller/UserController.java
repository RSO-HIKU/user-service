package com.hiku.userService.controller;

import com.hiku.userService.model.User;
import com.hiku.userService.model.Follow;

import com.hiku.userService.repository.UserRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    // Instantiate manually
    private final UserRepository repo = new UserRepository();

    @GET
    public List<User> list() {
        return repo.findAll();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        User u = repo.find(id);
        if (u == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(u).build();
    }

    @POST
    public Response create(User user) {
        User created = repo.create(user);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, User user) {
        user.setId(id);
        User updated = repo.update(user);
        if (updated == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(updated).build();
    }
    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchUser(@PathParam("id") Long id, User partial) {
        try {
            User existing = repo.find(id);
            if (existing == null) return Response.status(Response.Status.NOT_FOUND).build();

            // Only update fields that are not null
            if (partial.getUsername() != null) existing.setUsername(partial.getUsername());
            if (partial.getEmail() != null) existing.setEmail(partial.getEmail());
            if (partial.getAge() != null) existing.setAge(partial.getAge());
            if (partial.getBio() != null) existing.setBio(partial.getBio());

            User updated = repo.update(existing);
            return Response.ok(updated).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean removed = repo.delete(id);
        if (!removed) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }

      @POST
    @Path("{id}/follow/{targetId}")
    public Response followUser(@PathParam("id") Long followerId, @PathParam("targetId") Long followingId) {
        User follower = repo.find(followerId);
        User following = repo.find(followingId);
        
        if (follower == null || following == null) 
            return Response.status(Response.Status.NOT_FOUND).build();
        
        if (repo.isFollowing(followerId, followingId))
            return Response.status(Response.Status.CONFLICT).entity("Already following").build();

        repo.follow(follower, following);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("{id}/follow/{targetId}")
    public Response unfollowUser(@PathParam("id") Long followerId, @PathParam("targetId") Long followingId) {
        boolean removed = repo.unfollow(followerId, followingId);
        if (!removed) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }

    @GET
    @Path("{id}/followers")
    public Response getFollowers(@PathParam("id") Long userId) {
        User u = repo.find(userId);
        if (u == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(repo.getFollowers(userId)).build();
    }

    @GET
    @Path("{id}/following")
    public Response getFollowing(@PathParam("id") Long userId) {
        User u = repo.find(userId);
        if (u == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(repo.getFollowing(userId)).build();
    }

    @GET
    @Path("{id}/follower-count")
    public Response getFollowerCount(@PathParam("id") Long userId) {
        User u = repo.find(userId);
        if (u == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok("{\"followerCount\": " + repo.getFollowerCount(userId) + "}").build();
    }

    @GET
    @Path("{id}/following-count")
    public Response getFollowingCount(@PathParam("id") Long userId) {
        User u = repo.find(userId);
        if (u == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok("{\"followingCount\": " + repo.getFollowingCount(userId) + "}").build();
    }

    @GET
    @Path("{id}/is-following/{targetId}")
    public Response isFollowing(@PathParam("id") Long followerId, @PathParam("targetId") Long followingId) {
        return Response.ok("{\"following\": " + repo.isFollowing(followerId, followingId) + "}").build();
    }
}
