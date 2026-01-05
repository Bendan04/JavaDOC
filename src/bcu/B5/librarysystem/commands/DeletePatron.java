package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;

import java.time.LocalDate;

public class DeletePatron implements Command {

    private final int patronId;

    public DeletePatron(int patronId) {
        this.patronId = patronId;
    }

    @Override
    public void execute(Library library, LocalDate date) throws LibraryException {

        Patron patron = library.getPatronByID(patronId);

        if (patron == null) {
            throw new LibraryException("Patron not found.");
        }

        if (patron.hasBooksOnLoan()) {
            throw new LibraryException("Cannot delete a patron with books currently on loan.");
        }

        patron.setDeleted(true);
    }
}