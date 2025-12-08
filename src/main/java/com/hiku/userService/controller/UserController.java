package com.hiku.userService.controller;

import com.hiku.userService.model.User;
import com.hiku.userService.dao.UserRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
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

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean removed = repo.delete(id);
        if (!removed) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }
}
