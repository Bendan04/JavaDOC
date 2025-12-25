package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.gui.MainWindow;
import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;

import java.time.LocalDate;

public class LoadGUI implements Command {

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        new MainWindow(library);
    }
    
}
 