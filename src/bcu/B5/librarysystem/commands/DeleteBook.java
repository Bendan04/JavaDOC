package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Library;

import java.time.LocalDate;

public class DeleteBook implements Command {

    private final int bookId;

    public DeleteBook(int bookId) {
        this.bookId = bookId;
    }

    @Override
    public void execute(Library library, LocalDate date) throws LibraryException {

        Book book = library.getBookByID(bookId);

        if (book == null) {
            throw new LibraryException("Book not found.");
        }

        if (book.isOnLoan()) {
            throw new LibraryException("Cannot delete a book that is currently on loan.");
        }

        book.setDeleted(true); // Soft deleted
    }
}
