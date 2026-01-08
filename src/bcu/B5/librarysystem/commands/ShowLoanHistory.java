package bcu.B5.librarysystem.commands;

import bcu.B5.librarysystem.main.LibraryException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Displays loan history for a given patron.
 */
public class ShowLoanHistory implements Command {

    private final int patronId;
    private static final String RESOURCE = "./resources/data/LoanHistory.txt";

    public ShowLoanHistory(int patronId) {
        this.patronId = patronId;
    }

    @Override
    public void execute(bcu.B5.librarysystem.model.Library library,
                        java.time.LocalDate date) throws LibraryException {

        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCE))) {

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split("::");
                int storedPatronId = Integer.parseInt(parts[1].trim());

                if (storedPatronId == patronId) {
                    System.out.println(line);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No loan history found for this patron.");
            }

        } catch (IOException ex) {
            throw new LibraryException("Failed to read loan history.");
        }
    }
}
