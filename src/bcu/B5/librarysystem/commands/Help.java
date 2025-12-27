package bcu.B5.librarysystem.commands;

import java.time.LocalDate;

import bcu.B5.librarysystem.model.Library;

public class Help implements Command {

    @Override
    public void execute(Library library, LocalDate currentDate) {
        System.out.println(Command.HELP_MESSAGE);
    }
}
 