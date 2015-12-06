/**
 * Created by Philip A Senger on November 10, 2015
 */
package com.sample;

import com.sample.dao.BookDao;
import com.sample.dao.UserDao;
import com.sample.domain.Book;
import com.sample.domain.User;
import com.sample.filters.JWTSecurityFilter;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;
import java.security.Key;
import java.util.Date;

@ApplicationPath("/")
public class Application extends ResourceConfig { // implements ContextResolver<ObjectMapper> {

    private static BookDao bookDao;
    private static UserDao userDao;
    private static Key key;

    public Application() {
        this(new BookDao(), new UserDao(), MacProvider.generateKey());

        User user = new User();
        user.setUsername("ironman");
        user.setEmail("ironman@gmail.com");
        user.setRoles(new String[]{"user", "admin"});
        user.setHashedPassword("password");
        this.getUserDao().addUser(user);

        Book book1 = new Book();
//        book1.setId("1");
        book1.setTitle("The Sharp Sliver");
        book1.setAuthor("Clementine Green");
        book1.setIsbn("1234");
        book1.setPublished(new Date());

        Book book2 = new Book();
//        book2.setId("2");
        book2.setTitle("Edge of Darkness");
        book2.setAuthor("Francisco Fry");
        book2.setIsbn("1234");
        book2.setPublished(new Date());

        this.getBookDao().addBook(book1);
        // books.put(book1.getId(), book1);
        this.getBookDao().addBook(book2);
        // books.put(book2.getId(), book2);
    }

    public Application(final BookDao bookDao, final UserDao userDao, final Key key) {
        this.setBookDao(bookDao);
        this.setUserDao(userDao);
        this.setKey(key);

        // Validation.
        // register(ValidationConfigurationContextResolver.class);
        // logging
        register(LoggingFilter.class);
        // roles security
        register(RolesAllowedDynamicFeature.class);
        // jwt filter
        register(JWTSecurityFilter.class);
        // turn on Jackson, Moxy isn't that good of a solution.
        register(JacksonFeature.class);

        packages("com.sample");

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(getBookDao()).to(BookDao.class);
                bind(getUserDao()).to(UserDao.class);
                bind(getKey()).to(Key.class);
            }
        });
        property("jersey.config.beanValidation.enableOutputValidationErrorEntity.server", "true");
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

    }

    public BookDao getBookDao() {
        return bookDao;
    }

    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

//    @Override
//    public ObjectMapper getContext(Class<?> type) {
//        return null;
//    }


}
