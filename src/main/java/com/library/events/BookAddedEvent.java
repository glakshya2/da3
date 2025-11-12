package com.library.events;

import com.library.entities.Book;

import java.io.Serializable;

public class BookAddedEvent implements Serializable {
    private final Book book;

    public BookAddedEvent(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return book;
    }
}
