package com.library.beans;

import com.library.dao.TransactionDao;
import com.library.entities.Transaction;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class ReportBean implements Serializable {

    private List<Transaction> allTransactions;
    private List<Transaction> issuedBooks;
    private List<Transaction> overdueBooks;

    private TransactionDao transactionDao;

    @PostConstruct
    public void init() {
        System.out.println("ReportBean: init() called.");
        transactionDao = new TransactionDao();
        loadReports();
    }

    public void loadReports() {
        System.out.println("ReportBean: loadReports() called.");
        allTransactions = transactionDao.findAll();
        System.out.println("ReportBean: transactionDao.findAll() returned " + (allTransactions != null ? allTransactions.size() : "null") + " transactions.");
        Date today = new Date();

        issuedBooks = allTransactions.stream()
                .filter(t -> {
                    boolean isIssued = "ISSUED".equals(t.getStatus());
                    boolean isNotReturned = (t.getReturnDate() == null || t.getReturnDate().after(today));
                    // Explicitly access associated entities to ensure they are loaded within the session
                    if (isIssued && isNotReturned) {
                        if (t.getBook() != null) {
                            t.getBook().getTitle(); // Force initialization
                        }
                        if (t.getMember() != null) {
                            t.getMember().getName(); // Force initialization
                        }
                    }
                    System.out.println("ReportBean: Checking transaction ID: " + t.getTransactionId() + ", Status: " + t.getStatus() + ", ReturnDate: " + t.getReturnDate() + ", DueDate: " + t.getDueDate() + ", isIssued: " + isIssued + ", isNotReturned: " + isNotReturned);
                    return isIssued && isNotReturned;
                })
                .collect(Collectors.toList());
        System.out.println("ReportBean: Found " + issuedBooks.size() + " issued books.");

        overdueBooks = allTransactions.stream()
                .filter(t -> "ISSUED".equals(t.getStatus()) && t.getDueDate().before(today))
                .collect(Collectors.toList());
        System.out.println("ReportBean: Found " + overdueBooks.size() + " overdue books.");
    }

    // Getters and Setters
    public List<Transaction> getAllTransactions() {
        return allTransactions;
    }

    public void setAllTransactions(List<Transaction> allTransactions) {
        this.allTransactions = allTransactions;
    }

    public List<Transaction> getIssuedBooks() {
        return issuedBooks;
    }

    public void setIssuedBooks(List<Transaction> issuedBooks) {
        this.issuedBooks = issuedBooks;
    }

    public List<Transaction> getOverdueBooks() {
        return overdueBooks;
    }

    public void setOverdueBooks(List<Transaction> overdueBooks) {
        this.overdueBooks = overdueBooks;
    }
}
