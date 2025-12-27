package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;

import java.time.LocalDate;

public class ShowPatron implements Command {

    private final int patronId;

    public ShowPatron(int patronId) {
        this.patronId = patronId;
    }

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        Patron patron = library.getPatronByID(patronId);
        System.out.println(patron.getDetailsLong());
    }
}