package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;

import java.time.LocalDate;

public class BorrowBook implements Command {

    private final int patronId;
    private final int bookId;

    public BorrowBook(int patronId, int bookId) {
        this.patronId = patronId;
        this.bookId = bookId;
    }
    
    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {

        Patron patron = library.getPatronByID(patronId);
        Book book = library.getBookByID(bookId);

        // 7.3 – Enforce maximum loan limit
        if (patron.getBooks().size() >= library.getMaxLoansPerPatron()) {
            throw new LibraryException(
                "Patron has reached the maximum number of borrowed books (" +
                library.getMaxLoansPerPatron() + ")."
            );
        }

        // Prevent borrowing deleted entities (important with 7.1 / 7.2)
        if (patron.isDeleted()) {
            throw new LibraryException("Cannot borrow books for a deleted patron.");
        }

        if (book.isDeleted()) {
            throw new LibraryException("Cannot borrow a deleted book.");
        }

        LocalDate dueDate = currentDate.plusDays(library.getLoanPeriod());
        patron.borrowBook(book, dueDate);

        System.out.println("Book borrowed successfully. Due date: " + dueDate);
    }

}
