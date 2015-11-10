package com.sample.endpoints;

import com.sample.dao.BookDao;
import com.sample.dao.UserDao;
import com.sample.domain.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class BookResourceTest extends JerseyTest {


    private static final String AdminUserName = "bob";
    private static final String AdminPassword = "aPassW0rd";
    private static final User AdminUser = buildUser(10L, AdminUserName, "bob@gmail.com", new String[]{"user", "admin"}, AdminPassword);

    private static final String NormalUserName = "larry";
    private static final String NormalPassword = "normalPassW0rd";
    private static final User NormalUser = buildUser(20L, NormalUserName, "larry@gmail.com", new String[]{"user"}, NormalPassword);

    private Token AdminToken;
    private Token NormalToken;

    private static final User buildUser(Long id, String username, String email, String[] roles, String hashedPassword) {
        User user = new User();
        user.setEmail(email);
        user.setId(id);
        user.setUsername(username);
        user.setRoles(roles);
        user.setHashedPassword(hashedPassword);
        return user;
    }

    protected javax.ws.rs.core.Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        final BookDao dao = new BookDao();
        final UserDao userDao = new UserDao();
        userDao.addUser(AdminUser);
        userDao.addUser(NormalUser);
        final Key key = MacProvider.generateKey();
        return new com.sample.Application(dao, userDao, key);
    }

    @Before
    public void getToken() {
        Response response;
        MultivaluedMap<String, String> formData;

        formData = new MultivaluedHashMap<String, String>();
        formData.add("username", AdminUserName);
        formData.add("password", AdminPassword);
        response = target("authentication").request().post(Entity.form(formData));
        assertEquals(200, response.getStatus());
        AdminToken = response.readEntity(Token.class);

        formData = new MultivaluedHashMap<String, String>();
        formData.add("username", NormalUserName);
        formData.add("password", NormalPassword);
        response = target("authentication").request().post(Entity.form(formData));
        assertEquals(200, response.getStatus());
        NormalToken = response.readEntity(Token.class);
    }

    @Test
    public void testAddBook() {
        Book book = new Book();
        String title = "The Renegade of the Enemy";
        String author = "Michael Van Der Van";
        book.setTitle(title);
        book.setAuthor(author);
        Date now = new Date();
        book.setPublished(now);
        book.setGenre(Genre.HISTORY);
        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("books").request().header("Authorization", "Bearer " + AdminToken.getAuthToken()).post(bookEntity);

        assertEquals(200, response.getStatus());

        Book responseBook = response.readEntity(Book.class);
        assertEquals(title, responseBook.getTitle());
        assertEquals(author, responseBook.getAuthor());
        assertEquals(now, responseBook.getPublished());
        assertEquals(Genre.HISTORY, responseBook.getGenre());
    }

    @Test
    public void testAddBookWithCorrectRole() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", "Seventh Door");
        map.put("author", "Aramazd Natanael");
        map.put("published", "2015-10-31T21:22:44.732 UTC");
        map.put("genre", "SCIFI");
        map.put("junk1", "this does not exist in the model");
        map.put("junk2", "this does not exist in the model");
        map.put("junk3", "this does not exist in the model");
        map.put("junk4", "this does not exist in the model");
        Entity<HashMap<String, Object>> bookEntity = Entity.entity(map, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("books").request().header("Authorization", "Bearer " + NormalToken.getAuthToken()).post(bookEntity);

        assertEquals(400, response.getStatus());

    }

    @Test
    public void testAddBookWithAdditionalDataInThePayload() {
        getToken();
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", "Seventh Door");
        map.put("author", "Aramazd Natanael");
        map.put("published", "2015-10-31T21:22:44.732 UTC");
        map.put("genre", "SCIFI");
        map.put("junk1", "this does not exist in the model");
        map.put("junk2", "this does not exist in the model");
        map.put("junk3", "this does not exist in the model");
        map.put("junk4", "this does not exist in the model");
        Entity<HashMap<String, Object>> bookEntity = Entity.entity(map, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("books").request().header("Authorization", "Bearer " + AdminToken.getAuthToken()).post(bookEntity);

        assertEquals(200, response.getStatus());

        Book responseBook = response.readEntity(Book.class);
        assertEquals("Seventh Door", responseBook.getTitle());
        assertEquals("Aramazd Natanael", responseBook.getAuthor());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS 'UTC'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = sdf.format(responseBook.getPublished());

        assertEquals("2015-10-31T21:22:44.732 UTC", utcTime);
        assertEquals(Genre.SCIFI, responseBook.getGenre());
    }

    @Test
    public void testAddBookWithNonAuthroizedUser() {
        getToken();
        Book book = new Book();
        String title = "The Renegade of the Enemy";
        String author = "Michael Van Der Van";
        book.setTitle(title);
        book.setAuthor(author);
        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("books").request().header("Authorization", "Bearer " + NormalToken.getAuthToken()).post(bookEntity);

        assertEquals(400, response.getStatus());
    }

    @Test
    public void testGetBookAsNormalUser() {
        getToken();
        Book response = target("books").path("1").request().header("Authorization", "Bearer " + NormalToken.getAuthToken()).get(Book.class);
        assertNotNull(response);
    }

    @Test
    public void testGetBookAsNormalAdmin() {
        getToken();
        Book response = target("books").path("1").request().header("Authorization", "Bearer " + AdminToken.getAuthToken()).get(Book.class);
        assertNotNull(response);
    }

    @Test
    public void testGetBooksAsNormalUser() {
        getToken();
        Collection<Book> response = target("books").request().header("Authorization", "Bearer " + NormalToken.getAuthToken())
                .get(new GenericType<Collection<Book>>() {
                });
        assertNotNull(response);
        assertTrue(response.size() > 0);
    }

    @Test
    public void testGetBooksAsNormalAdmin() {
        getToken();
        Collection<Book> response = target("books").request().header("Authorization", "Bearer " + AdminToken.getAuthToken())
                .get(new GenericType<Collection<Book>>() {
                });
        assertNotNull(response);
        assertTrue(response.size() > 0);
    }

    @Test
    public void testGetMissingBook() {
        getToken();
        Response response = target("books").path("0").request().header("Authorization", "Bearer " + NormalToken.getAuthToken()).get();
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        GenericErrorMessage s = response.readEntity(GenericErrorMessage.class);
        assertEquals("Book Not Found", s.getMessage());
    }

    @Test
    public void testGetBookEntityTag() {
        getToken();
        EntityTag entityTag = target("books").path("1").request().header("Authorization", "Bearer " + NormalToken.getAuthToken())
                .get().getEntityTag();
        assertNotNull(entityTag);

        Response response = target("books").path("1").request().header("Authorization", "Bearer " + NormalToken.getAuthToken())
                .header("If-None-Match", entityTag).get();

        assertEquals(304, response.getStatus());
    }

}
