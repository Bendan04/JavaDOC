package bcu.B5.librarysystem.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bcu.B5.librarysystem.main.LibraryException;

public class Patron {
    
    private int id;
    private String name;
    private String phone;
    private String email;
    private final List<Book> books = new ArrayList<>();
    
    public Patron(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public List<Book> getBooks() {
        return List.copyOf(books);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void borrowBook(Book book, LocalDate dueDate) throws LibraryException {
        if (book.isOnLoan()) {
            throw new LibraryException("Book is already on loan.");
        }

        Loan loan = new Loan(this, book, LocalDate.now(), dueDate);
        book.setLoan(loan);
        books.add(book);
    }

    public void renewBook(Book book, LocalDate dueDate) throws LibraryException {
        if (!books.contains(book)) {
            throw new LibraryException("This patron has not borrowed this book.");
        }

        book.setDueDate(dueDate);
    }

    public void returnBook(Book book) throws LibraryException {
        if (!books.contains(book)) {
            throw new LibraryException("This patron has not borrowed this book.");
        }

        book.returnToLibrary();
        books.remove(book);
    }
        
    public void addBook(Book book) {
        books.add(book);
    }

    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Patron #").append(id).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Phone: ").append(phone).append("\n");
        sb.append("Email: ").append(email).append("\n");

        if (books.isEmpty()) {
            sb.append("No books on loan.\n");
        } else {
            sb.append("Books on loan:\n");
            for (Book book : books) {
                sb.append("  - ").append(book.getDetailsShort()).append("\n");
            }
        }

        return sb.toString();
    }
}
 