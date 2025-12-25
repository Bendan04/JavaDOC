package bcu.B5.librarysystem.main;

import bcu.B5.librarysystem.commands.Command;
import bcu.B5.librarysystem.data.LibraryData;
import bcu.B5.librarysystem.model.Library;

import java.io.*;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) throws IOException, LibraryException {
        
        Library library = LibraryData.load();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Library system");
        System.out.println("Enter 'help' to see a list of available commands.");
        while (true) {
            System.out.print("> ");
            String line = br.readLine();
            if (line.equals("exit")) {
                break;
            }

            try {
                Command command = CommandParser.parse(line);
                command.execute(library, LocalDate.now());                
            } catch (LibraryException ex) {
                System.out.println(ex.getMessage());
            }
        }
        LibraryData.store(library);
        System.exit(0);
    }
}
 