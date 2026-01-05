package bcu.B5.librarysystem.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import bcu.B5.librarysystem.commands.AddBook;
import bcu.B5.librarysystem.commands.Command;
import bcu.B5.librarysystem.commands.Help;
import bcu.B5.librarysystem.commands.ListBooks;
import bcu.B5.librarysystem.commands.LoadGUI;
import bcu.B5.librarysystem.commands.AddPatron;
import bcu.B5.librarysystem.commands.ListPatrons;
import bcu.B5.librarysystem.commands.ShowPatron;
import bcu.B5.librarysystem.commands.BorrowBook;
import bcu.B5.librarysystem.commands.ReturnBook;
import bcu.B5.librarysystem.commands.RenewBook;
import bcu.B5.librarysystem.commands.DeleteBook;
import bcu.B5.librarysystem.commands.DeletePatron;

public class CommandParser {
    
    public static Command parse(String line) throws IOException, LibraryException {
        try {
            String[] parts = line.split(" ", 3);
            String cmd = parts[0];

            // TODO: Link your implemented features to commands here 
            if (cmd.equals("addbook")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Title: ");
                String title = br.readLine();
                System.out.print("Author: ");
                String author = br.readLine();
                System.out.print("Publication Year: ");
                String publicationYear = br.readLine();
                System.out.print("Publisher: ");
                String publisher = br.readLine();

                return new AddBook(title, author, publicationYear, publisher);
            } else if (cmd.equals("addpatron")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Name: ");
                String name = br.readLine();
                System.out.print("Phone: ");
                String phone = br.readLine();
                System.out.print("Email: ");
                String email = br.readLine();

                return new AddPatron(name, phone, email);
            } else if (cmd.equals("loadgui")) {
                return new LoadGUI();
            } else if (parts.length == 1) {
                if (line.equals("listbooks")) {
                    return new ListBooks();
                } else if (line.equals("listpatrons")) {
                    return new ListPatrons();
                } else if (line.equals("help")) {
                    return new Help();
                }
            } else if (parts.length == 2) {
                int id = Integer.parseInt(parts[1]);

                if (cmd.equals("deletebook")) {
                    return new DeleteBook(id);
                } else if (cmd.equals("showpatron")) {
                    return new ShowPatron(id);
                } else if (cmd.equals("deletepatron")) {
                    return new DeletePatron(id);
                }
            } else if (parts.length == 3) {
                int patronID = Integer.parseInt(parts[1]);
                int bookID = Integer.parseInt(parts[2]);

                if (cmd.equals("borrow")) {
                    return new BorrowBook(patronID, bookID);
                } else if (cmd.equals("renew")) {
                    return new RenewBook(patronID, bookID);
                } else if (cmd.equals("return")) {
                    return new ReturnBook(patronID, bookID);
                }
            }
        } catch (NumberFormatException ex) {

        }

        throw new LibraryException("Invalid command.");
    }
}
