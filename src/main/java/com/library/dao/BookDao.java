package com.library.dao;

import com.library.entities.Book;

public class BookDao extends AbstractJpaDao<Book, Long> {
    public BookDao() {
        super();
    }

    // You can add specific Book-related DAO methods here if needed
}
