package bcu.B5.librarysystem.data;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.*;

import java.io.IOException;
import java.time.LocalDate;

public class LoanDataManager implements DataManager {
    
    public final String RESOURCE = "./resources/data/loans.txt";

    @Override
    public void loadData(Library library) throws IOException, LibraryException {
        try (var reader = new java.io.BufferedReader(
                new java.io.FileReader(RESOURCE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split("::");

                int bookId = Integer.parseInt(parts[0].trim());
                int patronId = Integer.parseInt(parts[1].trim());
                LocalDate startDate = LocalDate.parse(parts[2].trim());
                LocalDate dueDate = LocalDate.parse(parts[3].trim());

                Book book = library.getBookByID(bookId);
                Patron patron = library.getPatronByID(patronId);

                Loan loan = new Loan(patron, book, startDate, dueDate);

                book.setLoan(loan);
                patron.addBook(book);
            }
        }
    }

    @Override
    public void storeData(Library library) throws IOException {
        try (var writer = new java.io.BufferedWriter(
                new java.io.FileWriter(RESOURCE))) {

            for (Book book : library.getBooks()) {
                if (book.isOnLoan()) {
                    Loan loan = book.getLoan();

                    writer.write(
                        book.getId() + "::" +
                        loan.getPatron().getId() + "::" +
                        loan.getStartDate() + "::" +
                        loan.getDueDate() + "::"
                    );
                    writer.newLine();
                }
            }
        }
    }
}