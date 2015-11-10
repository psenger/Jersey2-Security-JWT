/**
 * Created by Philip A Senger on November 10, 2015
 */
package com.sample.endpoints;

import com.sample.dao.UserDao;
import com.sample.domain.EntityNotFoundException;
import com.sample.domain.User;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import java.util.Collection;
import java.util.logging.Logger;

@PermitAll
@Path("/users")
public class UserResource {

    private final static Logger logger = Logger.getLogger(UserResource.class.getName());
    /**
     * HK2 Injection.
     */
    @Context
    UserDao dao;

    @Context
    Request request;

    @Context
    SecurityContext securityContext;

    /**
     * What can be injected via @Context
     * ( * When Jersey is deployed in a servlet container )
     * -----------------------
     * Application
     * URLInfo
     * Request
     * HttpHeaders
     * SecurityContext
     * Providers
     * ServletConfig *
     * ServletContext *
     * HttpServletRequest *
     * HttpServletResponse *
     */
    @RolesAllowed({"admin"})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<User> getUsers() {
        return dao.getUsers();
    }

    @RolesAllowed({"user", "admin"})
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateOnExecution
    public User getUser(@NotNull @PathParam("id") long id) throws EntityNotFoundException {
        if (securityContext.isUserInRole("admin")) {
            return dao.getUser(id);
        } else {
            User user = dao.getUser(id);
            if (user.getUsername().equals(securityContext.getUserPrincipal().getName())) {
                return user;
            } else {
                throw new NotAllowedException("Not allowed ");
            }
        }
    }

    @RolesAllowed({"admin"})
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateOnExecution
    public User addUser(@Valid @NotNull User user) {
        return dao.addUser(user);
    }

    @RolesAllowed({"user", "admin"})
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateOnExecution
    public User updateUser(@NotNull @PathParam("id") long id, @Valid @NotNull User user) throws EntityNotFoundException {

        if (securityContext.isUserInRole("admin")) {
            User oldUser = dao.getUser(id);
            oldUser.setEmail(user.getEmail());
            oldUser.setHashedPassword(user.getHashedPassword());
            return dao.update(oldUser);
        } else {
            User oldUser = dao.getUser(id);
            if (oldUser.getUsername().equals(securityContext.getUserPrincipal().getName())) {
                oldUser.setEmail(user.getEmail());
                oldUser.setHashedPassword(user.getHashedPassword());
                return dao.update(oldUser);
            } else {
                throw new NotAllowedException("Not allowed ");
            }
        }
    }


}
