package com.library.dao;

import com.library.entities.Transaction;

public class TransactionDao extends AbstractJpaDao<Transaction, Long> {
    public TransactionDao() {
        super();
    }

    // You can add specific Transaction-related DAO methods here if needed
}
