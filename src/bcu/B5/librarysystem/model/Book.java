package bcu.B5.librarysystem.model;

import java.time.LocalDate;

import bcu.B5.librarysystem.main.LibraryException;

public class Book {
    
    private int id;
    private String title;
    private String author;
    private String publicationYear;
    private String publisher;

    private Loan loan;

    public Book(int id, String title, String author, String publicationYear, String publisher) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
    }

    public int getId() {
        return id;
    } 

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }
	
    public String getDetailsShort() {
        return "Book #" + id + " - " + title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public String getDetailsLong() {
        return "Book #" + id + "\n"
             + "Title: " + title + "\n"
             + "Author: " + author + "\n"
             + "Year: " + publicationYear + "\n"
             + "Publisher: " + publisher + "\n"
             + "Status: " + getStatus();
    }
    
    public boolean isOnLoan() {
        return (loan != null);
    }
    
    public String getStatus() {
        if (loan == null) {
            return "Available";
        }
        return "On loan (due " + loan.getDueDate() + ")";
    }

    public LocalDate getDueDate() {
        if (loan == null) {
            return null;
        }
        return loan.getDueDate();
    }
    
    public void setDueDate(LocalDate dueDate) throws LibraryException {
        if (loan == null) {
            throw new LibraryException("Book is not currently on loan.");
        }
        loan.setDueDate(dueDate);
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public void returnToLibrary() {
        loan = null;
    }
}
