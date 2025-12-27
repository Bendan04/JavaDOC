package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;

import java.time.LocalDate;

public class RenewBook implements Command {

    private final int patronId;
    private final int bookId;

    public RenewBook(int patronId, int bookId) {
        this.patronId = patronId;
        this.bookId = bookId;
    }

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        Patron patron = library.getPatronByID(patronId);
        Book book = library.getBookByID(bookId);

        LocalDate newDueDate = currentDate.plusDays(library.getLoanPeriod());
        patron.renewBook(book, newDueDate);

        System.out.println("Book renewed successfully. New due date: " + newDueDate);
    }
}
