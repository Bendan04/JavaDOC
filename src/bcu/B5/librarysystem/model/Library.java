package bcu.B5.librarysystem.model;

import java.util.*;

import bcu.B5.librarysystem.main.LibraryException;

public class Library {
    
    private final int loanPeriod = 7;
    private final int maxLoansPerPatron = 2;
    private final Map<Integer, Patron> patrons = new TreeMap<>();
    private final Map<Integer, Book> books = new TreeMap<>();

    public int getLoanPeriod() {
        return loanPeriod;
    }

    public List<Book> getBooks() {
        return books.values().stream()
            .filter(b -> !b.isDeleted())
            .toList();
    }

    public Collection<Book> getAllBooks() {
        return books.values();
    }

    public Collection<Patron> getAllPatrons() {
        return patrons.values();
    }

    public Book getBookByID(int id) throws LibraryException {
        if (!books.containsKey(id)) {
            throw new LibraryException("There is no such book with that ID.");
        }
        return books.get(id);
    }

    public Patron getPatronByID(int id) throws LibraryException {
        if (!patrons.containsKey(id)) {
            throw new LibraryException("There is no such patron with that ID.");
        }
        return patrons.get(id);
    }

    public void addBook(Book book) {
        if (books.containsKey(book.getId())) {
            throw new IllegalArgumentException("Duplicate book ID.");
        }
        books.put(book.getId(), book);
    }

    public void addPatron(Patron patron) {
        if (patrons.containsKey(patron.getId())) {
            throw new IllegalArgumentException("Duplicate patron ID.");
        }
        patrons.put(patron.getId(), patron);
    }
    public List<Patron> getPatrons() {
        return patrons.values().stream()
            .filter(p -> !p.isDeleted())
            .toList();
    }

    public int getMaxLoansPerPatron() {
        return maxLoansPerPatron;
    }

    public Library copy() {
        Library copy = new Library();

        // Copy patrons (Map<Integer, Patron>)
        for (Patron p : this.patrons.values()) {
            copy.patrons.put(p.getId(), p.copy());
        }

        // Copy books (Map<Integer, Book>)
        for (Book b : this.books.values()) {
            copy.books.put(b.getId(), b.copy());
        }

        return copy;
    }

}
 