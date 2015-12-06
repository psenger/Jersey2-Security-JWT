/**
 * Created by Philip A Senger on November 10, 2015
 */
package com.sample.dao;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sample.domain.Book;
import com.sample.domain.EntityNotFoundException;

import java.util.*;
import java.util.concurrent.Executors;

public class BookDao {

    private Map<String, Book> books;
    private ListeningExecutorService service;

    public BookDao() {
        books = new HashMap<String, Book>();
        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    }

    /**
     * get books
     * @return
     */
    public Collection<Book> getBooks() {
        return (books.values());
    }

    /**
     * get a book by id
     * @param id
     * @return
     * @throws EntityNotFoundException
     */
    public Book getBook(String id) throws EntityNotFoundException {
        if (books.containsKey(id)) {
            return books.get(id);
        }
        throw new EntityNotFoundException("Book Not Found");
    }

    public ListenableFuture<Book> getBookAsync(final String id) {
        ListenableFuture<Book> future = service.submit(
                () -> getBook(id)
        );
        return future;
    }

    public Book addBook(Book book) {
        book.setId(UUID.randomUUID().toString());
        books.put(book.getId(), book);
        return book;
    }
}
