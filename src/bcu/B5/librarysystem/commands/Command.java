package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;

import java.time.LocalDate;

public interface Command {

    public static final String HELP_MESSAGE = "Commands:\n"
            + "\tlistbooks                       print all books*\n"
            + "\tlistpatrons                     print all patrons\n"
            + "\taddbook (title, author, publicationYear, publisher)  add a new book*\n"
            + "\taddpatron (name, phone, email)  add a new patron\n"
            + "\tshowbook (bookid)               show book details\n"
            + "\tshowpatron (patronid)           show patron details\n"
            + "\tborrow (patronid) (bookid)      borrow a book\n"
            + "\trenew (patronid) (bookid)       renew a book\n"
            + "\treturn (patronid) (bookid)      return a book\n"
            + "\tloadgui                         loads the GUI version of the app*\n"
            + "\thelp                            prints this help message*\n"
            + "\texit                            exits the program*";

    
    public void execute(Library library, LocalDate currentDate) throws LibraryException;
    
}
 