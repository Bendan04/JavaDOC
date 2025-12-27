package bcu.B5.librarysystem.data;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;

import java.io.IOException;

public class PatronDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/patrons.txt";
    
    @Override
    public void loadData(Library library) throws IOException, LibraryException {
        try (var reader = new java.io.BufferedReader(
                new java.io.FileReader(RESOURCE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("::");

                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String phone = parts[2].trim();
                String email = parts[3].trim();
                
                Patron patron = new Patron(id, name, phone, email);
                library.addPatron(patron);
            }
        }
    }

    @Override
    public void storeData(Library library) throws IOException {
        try (var writer = new java.io.BufferedWriter(
                new java.io.FileWriter(RESOURCE))) {

            for (Patron patron : library.getPatrons()) {
                writer.write(
                    patron.getId() + "::" +
                    patron.getName() + "::" +
                    patron.getPhone() + "::" +
                    patron.getEmail() + "::"
                );
                writer.newLine();
            }
        }
    }
}
 