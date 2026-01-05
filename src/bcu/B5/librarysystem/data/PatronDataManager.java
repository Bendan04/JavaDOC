package bcu.B5.librarysystem.data;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class PatronDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/patrons.txt";

    @Override
    public void loadData(Library library) throws IOException, LibraryException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int lineIdx = 1;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isBlank()) {
                    lineIdx++;
                    continue;
                }

                String[] parts = line.split("::");

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String phone = parts[2].trim();
                    String email = parts[3].trim();

                    // deleted flag backwards compatible
                    boolean deleted = false;
                    if (parts.length >= 5) {
                        deleted = Boolean.parseBoolean(parts[4].trim());
                    }

                    Patron patron = new Patron(id, name, phone, email);
                    patron.setDeleted(deleted); // soft delete flag
                    library.addPatron(patron);

                } catch (Exception ex) {
                    throw new LibraryException(
                        "Unable to parse patron data on line " + lineIdx +
                        "\nError: " + ex.getMessage()
                    );
                }

                lineIdx++;
            }
        }
    }

    @Override
    public void storeData(Library library) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {

            // All patrons (including del) are written to .txt file
            for (Patron patron : library.getAllPatrons()) {
                out.print(patron.getId() + "::");
                out.print(patron.getName() + "::");
                out.print(patron.getPhone() + "::");
                out.print(patron.getEmail() + "::");
                out.print(patron.isDeleted()); // deleted flag
                out.println();
            }
        }
    }
}
