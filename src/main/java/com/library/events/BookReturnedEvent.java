package com.library.events;

import com.library.entities.Book;
import com.library.entities.Transaction;

import java.io.Serializable;

public class BookReturnedEvent implements Serializable {
    private final Book book;
    private final Transaction transaction;

    public BookReturnedEvent(Book book, Transaction transaction) {
        this.book = book;
        this.transaction = transaction;
    }

    public Book getBook() {
        return book;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
