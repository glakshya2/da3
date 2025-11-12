package com.library.beans;

import com.library.dao.BookDao;
import com.library.entities.Book;
import com.library.events.BookIssuedEvent;
import com.library.events.BookReturnedEvent;
import com.library.events.EventManager;
import com.library.events.BookAddedEvent;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Named
@SessionScoped
public class BookBean implements Serializable {

    private Book newBook;
    private List<Book> allBooks;
    private BookDao bookDao;

    @PostConstruct
    public void init() {
        bookDao = new BookDao();
        newBook = new Book();
        loadAllBooks();
        
        // Register listeners for book events
        EventManager.registerIssueListener(this::handleBookIssuedEvent);
        EventManager.registerReturnListener(this::handleBookReturnedEvent);
    }

    public void loadAllBooks() {
        allBooks = bookDao.findAll();
    }

    private void handleBookIssuedEvent(BookIssuedEvent event) {
        // Refresh the book list to reflect changes in available copies
        loadAllBooks();
    }

    private void handleBookReturnedEvent(BookReturnedEvent event) {
        // Refresh the book list to reflect changes in available copies
        loadAllBooks();
    }

    public String addBook() {
        bookDao.save(newBook);
        // Fire BookAddedEvent
        EventManager.fireEvent(new BookAddedEvent(newBook));
        newBook = new Book(); // Reset for next entry
        loadAllBooks(); // Refresh the list
        return "addBook?faces-redirect=true"; // Redirect to prevent double submission
    }

    // Getters and Setters
    public Book getNewBook() {
        return newBook;
    }

    public void setNewBook(Book newBook) {
        this.newBook = newBook;
    }

    public List<Book> getAllBooks() {
        return allBooks;
    }

    public void setAllBooks(List<Book> allBooks) {
        this.allBooks = allBooks;
    }
}
