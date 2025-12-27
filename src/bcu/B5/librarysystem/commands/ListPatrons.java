package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;

import java.time.LocalDate;

public class ListPatrons implements Command {

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        for (Patron patron : library.getPatrons()) {
            System.out.println(
                "Patron #" + patron.getId() + " - " + patron.getName()
            );
        }
    }
}