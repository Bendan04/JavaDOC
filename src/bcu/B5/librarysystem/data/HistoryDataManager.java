package bcu.B5.librarysystem.data;

import bcu.B5.librarysystem.model.Loan;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages persistence of historical loan data.
 * 
 * Loan history is append-only and uses the same
 * format as loans.txt.
 */
public class HistoryDataManager {

    private static final String RESOURCE = "./resources/data/LoanHistory.txt";

    /**
     * Appends a loan record to the loan history file.
     *
     * @param loan the loan to persist
     * @throws IOException if writing fails
     */
    public static void StoreData(Loan loan) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESOURCE, true))) {
            writer.write(
                loan.getBook().getId() + "::" +
                loan.getPatron().getId() + "::" +
                loan.getStartDate() + "::" +
                loan.getDueDate() + "::"
            );
            writer.newLine();
        }
    }
}
