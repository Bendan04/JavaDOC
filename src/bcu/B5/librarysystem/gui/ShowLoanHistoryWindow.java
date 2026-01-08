package bcu.B5.librarysystem.gui;

import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Patron;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Window that displays loan history for a selected patron.
 *
 * This window is read-only and loads persisted loan history data.
 */
public class ShowLoanHistoryWindow extends JDialog {

    private static final String RESOURCE = "./resources/data/LoanHistory.txt";

    private MainWindow mw;

    // Patron selection
    private JComboBox<Patron> patronDropdown = new JComboBox<>();

    // Loan history table
    private JTable table = new JTable();

    /**
     * ShowLoanHistoryWindow.
     *
     * @param mw main application window
     */
    public ShowLoanHistoryWindow(MainWindow mw) {
        super(mw, "Loan History", true);
        this.mw = mw;
        initialize();
        populatePatronDropdown();
        setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}

        setSize(650, 350);

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.add(new JLabel("Select patron:"));
        topPanel.add(patronDropdown);

        patronDropdown.addActionListener(e -> loadLoanHistory());

        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        setLocationRelativeTo(mw);
    }

    /**
     * Populates the patron drop down sorted alphabetically.
     */
    private void populatePatronDropdown() {
        patronDropdown.removeAllItems();

        mw.getLibrary().getAllPatrons().stream()
            .sorted(Comparator.comparing(Patron::getName, String.CASE_INSENSITIVE_ORDER))
            .forEach(patronDropdown::addItem);
    }

    /**
     * Loads and displays loan history for the selected patron.
     */
    private void loadLoanHistory() {

        Patron patron = (Patron) patronDropdown.getSelectedItem();
        if (patron == null) {
            return;
        }

        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCE))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("::");
                int patronId = Integer.parseInt(parts[1].trim());

                if (patronId == patron.getId()) {
                    rows.add(parts);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                mw,
                "Failed to load loan history data.",
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String[] columns = { "Book Title", "Start Date", "Due Date" };
        Object[][] data = new Object[rows.size()][3];

        for (int i = 0; i < rows.size(); i++) {

            int bookId = Integer.parseInt(rows.get(i)[0]);
            String bookTitle;

            try {
                Book book = mw.getLibrary().getBookByID(bookId);
                bookTitle = book.getTitle();
            } catch (Exception ex) {
                bookTitle = "Unknown Book";
            }

            data[i][0] = bookTitle;
            data[i][1] = rows.get(i)[2]; // Start date
            data[i][2] = rows.get(i)[3]; // Due date
        }

        table.setModel(new DefaultTableModel(data, columns));
    }
}
