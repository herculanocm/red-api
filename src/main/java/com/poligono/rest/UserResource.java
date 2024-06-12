package com.poligono.rest;


import java.util.Set;

import com.poligono.domain.repository.UserRepository;
import com.poligono.rest.dto.ResponseError;
import com.poligono.rest.dto.User;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    private UserRepository userRepository;

    private Validator validator;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createUser(User u) {

        Set<ConstraintViolation<User>> violations = validator.validate(u);
        if (!violations.isEmpty()) {
            ResponseError rerr= ResponseError.createFromValidation(violations);

            return Response.status(Response.Status.BAD_REQUEST).entity(rerr).build();
        }

        com.poligono.domain.model.User user = new com.poligono.domain.model.User();
        user.setAge(u.getAge());
        user.setName(u.getName());

        userRepository.persist(user);

        return Response.ok(user).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllUsers() {
        PanacheQuery<com.poligono.domain.model.User> query = userRepository.findAll();

        return Response.ok(query.list()).build();
    }
}
