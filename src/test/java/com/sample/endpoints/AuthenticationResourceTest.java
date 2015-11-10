package com.sample.endpoints;

import com.sample.Application;
import com.sample.dao.BookDao;
import com.sample.dao.UserDao;
import com.sample.domain.Token;
import com.sample.domain.User;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.security.Key;

import static org.junit.Assert.*;

public class AuthenticationResourceTest extends JerseyTest {

    private BookDao bookDao;
    private UserDao userDao;

    private static User buildUser(Long id, String username, String email, String[] roles, String hashedPassword) {
        User user = new User();
        user.setEmail(email);
        user.setId(id);
        user.setUsername(username);
        user.setRoles(roles);
        user.setHashedPassword(hashedPassword);
        return user;
    }

    protected javax.ws.rs.core.Application configure() {
        userDao = new UserDao();
        bookDao = new BookDao();
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        final Key key = MacProvider.generateKey();
        User user = userDao.addUser(buildUser(2L, "bob", "bob@gmail.com", new String[]{"user"}, "aPassW0rd"));
        return new Application(bookDao, userDao, key);
    }

    @Test
    public void testUserThatExists() {
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
        formData.add("username", "bob");
        formData.add("password", "aPassW0rd");
        Response response = target("authentication").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(formData));
        assertEquals(200, response.getStatus());
        Token token = response.readEntity(Token.class);
        assertNotNull(token);
        assertTrue(token.getAuthToken().length() != 0);
    }

    @Test
    public void testUserThatExistsButBadPassword() {
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
        formData.add("username", "bob");
        formData.add("password", "xxxxx");
        Response response = target("authentication").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(formData));
        assertEquals(401, response.getStatus());
    }

    @Test
    public void testUserThatDoesNOTExists() {
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
        formData.add("username", "Chet");
        formData.add("password", "Faker");
        Response response = target("authentication").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(formData));
        assertEquals(401, response.getStatus());
    }
}
