package com.library.beans;

import com.library.dao.BookDao;
import com.library.dao.MemberDao;
import com.library.dao.TransactionDao;
import com.library.entities.Book;
import com.library.entities.Member;
import com.library.entities.Transaction;
import com.library.events.BookAddedEvent;
import com.library.events.BookIssuedEvent;
import com.library.events.BookReturnedEvent;
import com.library.events.EventManager;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Named
@SessionScoped
public class TransactionBean implements Serializable {

    private Long selectedBookId;
    private Long selectedMemberId;
    private Date issueDate;
    private Date dueDate;
    private Book selectedBookToReturn;

    private List<Book> availableBooks;
    private List<Member> allMembers;
    private List<Transaction> allTransactions;

    private BookDao bookDao;
    private MemberDao memberDao;
    private TransactionDao transactionDao;

    @PostConstruct
    public void init() {
        System.out.println("TransactionBean: init() called.");
        bookDao = new BookDao();
        memberDao = new MemberDao();
        transactionDao = new TransactionDao();
        loadData();
        resetTransactionFields();

        // Register listener for book added events
        EventManager.registerBookAddedListener(this::handleBookAddedEvent);
    }

    private void handleBookAddedEvent(BookAddedEvent event) {
        loadData(); // Refresh available books when a new book is added
    }

    private void loadData() {
        availableBooks = bookDao.findAll(); // Assuming all books are initially available or you'll filter later
        allMembers = memberDao.findAll();
        allTransactions = transactionDao.findAll();
    }

    private void resetTransactionFields() {
        selectedBookId = null;
        selectedMemberId = null;
        issueDate = new Date();
        // Set due date to 14 days from issue date as an example
        dueDate = new Date(issueDate.getTime() + TimeUnit.DAYS.toMillis(14));
        selectedBookToReturn = null;
    }

    public void issueBook() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (selectedBookId == null || selectedMemberId == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select a book and a member."));
            return;
        }

        Book book = bookDao.findById(selectedBookId);
        Member member = memberDao.findById(selectedMemberId);

        if (book == null || member == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Book or Member not found."));
            return;
        }

        if (book.getAvailableCopies() <= 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No available copies of this book."));
            return;
        }

        Transaction transaction = new Transaction(book, member, issueDate, dueDate, "ISSUED");
        transactionDao.save(transaction);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDao.update(book);

        // Fire BookIssuedEvent
        EventManager.fireEvent(new BookIssuedEvent(book, transaction));

        loadData(); // Refresh data
        resetTransactionFields();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Book issued successfully!"));
    }

    public void returnBook() {
        FacesContext context = FacesContext.getCurrentInstance();
        System.out.println("TransactionBean.returnBook - selectedBookToReturn: " + (selectedBookToReturn != null ? selectedBookToReturn.getTitle() + " (ID: " + selectedBookToReturn.getBookId() + ")" : "null"));

        if (selectedBookToReturn == null || selectedBookToReturn.getBookId() == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Return Error", "Please select a book to return."));
            return;
        }

        // Find the latest issued transaction for this book that is not yet returned
        List<Transaction> issuedTransactions = transactionDao.findAll().stream()
                .filter(t -> t.getBook().getBookId().equals(selectedBookToReturn.getBookId()) && "ISSUED".equals(t.getStatus()))
                .sorted((t1, t2) -> t2.getIssueDate().compareTo(t1.getIssueDate())) // Latest first
                .collect(java.util.stream.Collectors.toList());

        System.out.println("TransactionBean.returnBook - found " + issuedTransactions.size() + " issued transactions for selected book.");

        if (issuedTransactions.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Return Error", "No outstanding issues found for this book."));
            return;
        }

        Transaction transactionToReturn = issuedTransactions.get(0);
        transactionToReturn.setReturnDate(new Date());
        transactionToReturn.setStatus("RETURNED");
        transactionDao.update(transactionToReturn);

        Book book = transactionToReturn.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookDao.update(book);

        // Fire BookReturnedEvent
        EventManager.fireEvent(new BookReturnedEvent(book, transactionToReturn));

        loadData(); // Refresh data
        resetTransactionFields();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Book returned successfully!"));
    }

    // Getters and Setters
    public Long getSelectedBookId() {
        return selectedBookId;
    }

    public void setSelectedBookId(Long selectedBookId) {
        this.selectedBookId = selectedBookId;
    }

    public Long getSelectedMemberId() {
        return selectedMemberId;
    }

    public void setSelectedMemberId(Long selectedMemberId) {
        this.selectedMemberId = selectedMemberId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Book getSelectedBookToReturn() {
        return selectedBookToReturn;
    }

    public void setSelectedBookToReturn(Book selectedBookToReturn) {
        this.selectedBookToReturn = selectedBookToReturn;
    }

    public List<Book> getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(List<Book> availableBooks) {
        this.availableBooks = availableBooks;
    }

    public List<Member> getAllMembers() {
        return allMembers;
    }

    public void setAllMembers(List<Member> allMembers) {
        this.allMembers = allMembers;
    }

    public List<Transaction> getAllTransactions() {
        return allTransactions;
    }

    public void setAllTransactions(List<Transaction> allTransactions) {
        this.allTransactions = allTransactions;
    }
}
