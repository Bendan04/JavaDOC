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
                
                return new AddBook(title, author, publicationYear);
            } else if (cmd.equals("addpatron")) {
                
            } else if (cmd.equals("loadgui")) {
                return new LoadGUI();
            } else if (parts.length == 1) {
                if (line.equals("listbooks")) {
                    return new ListBooks();
                } else if (line.equals("listpatrons")) {
                     
                } else if (line.equals("help")) {
                    return new Help();
                }
            } else if (parts.length == 2) {
                int id = Integer.parseInt(parts[1]);

                if (cmd.equals("showbook")) {
                    
                } else if (cmd.equals("showpatron")) {
                    
                }
            } else if (parts.length == 3) {
                int patronID = Integer.parseInt(parts[1]);
                int bookID = Integer.parseInt(parts[2]);

                if (cmd.equals("borrow")) {
                    
                } else if (cmd.equals("renew")) {
                    
                } else if (cmd.equals("return")) {
                    
                }
            }
        } catch (NumberFormatException ex) {

        }

        throw new LibraryException("Invalid command.");
    }
}
