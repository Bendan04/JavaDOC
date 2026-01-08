package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;

import java.time.LocalDate;

public class ReturnBook implements Command {

    private final int patronId;
    private final int bookId;

    public ReturnBook(int patronId, int bookId) {
        this.patronId = patronId;
        this.bookId = bookId;
    }

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {

        Patron patron = library.getPatronByID(patronId);
        Book book = library.getBookByID(bookId);

        if (!book.isOnLoan()) {
            throw new LibraryException("This book is not currently on loan.");
        }

        // Capture loan BEFORE return
        var loan = book.getLoan();

        try {
            // Write to history first (roll back if this fails)
            bcu.B5.librarysystem.data.HistoryDataManager.StoreData(loan);
        } catch (java.io.IOException ioEx) {
            throw new LibraryException(
                "Failed to write loan history. Return cancelled."
            );
        }

        // Now perform the return
        patron.returnBook(book);

        System.out.println("Book returned successfully.");
    }

}
