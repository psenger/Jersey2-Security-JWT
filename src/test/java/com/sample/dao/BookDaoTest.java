package com.sample.dao;

import com.sample.domain.Book;
import com.sample.domain.Genre;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * BookDao Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Dec 5, 2015</pre>
 */
public class BookDaoTest {

    private static final BookDao dao = new BookDao();
    private static Book book1, book2, book3;

    private static Book buildBook(String id, String title, String author, String isbn, Date published, Genre genre) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublished(published);
        book.setGenre(genre);
        return book;
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        book1 = dao.addBook(buildBook(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), new Date(), Genre.FANTASY));
        book2 = dao.addBook(buildBook(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), new Date(), Genre.HISTORY));
        book3 = dao.addBook(buildBook(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), new Date(), Genre.SCIFI));
    }

    /**
     * Method: getBooks()
     */
    @Test
    public void testGetBooks() throws Exception {
        Collection<Book> books = dao.getBooks();
        assertNotNull(books);
        assertEquals(3, books.size());
    }

    /**
     * Method: getBook(String id)
     */
    @Test
    public void testGetBook() throws Exception {
        Book book = dao.getBook(book1.getId());
        assertNotNull(book);
        assertEquals(book1, book);
    }

    /**
     * Method: getBookAsync(final String id)
     */
    @Test
    public void testGetBookAsync() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: addBook(Book book)
     */
    @Test
    public void testAddBook() throws Exception {
//TODO: Test goes here... 
    }


} 
