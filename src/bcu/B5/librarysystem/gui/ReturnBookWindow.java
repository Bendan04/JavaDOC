package bcu.B5.librarysystem.gui;

import bcu.B5.librarysystem.commands.Command;
import bcu.B5.librarysystem.data.LibraryData;
import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;
import bcu.B5.librarysystem.commands.ReturnBook;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;

/**
 * Window that allows a book to be returned by a patron.
 * <p>
 * Corrupted saves will roll back all changes.
 * </p>
 */
public class ReturnBookWindow extends JDialog implements ActionListener {

    private MainWindow mw;

    // Patron selection
    private JComboBox<Patron> patronDropdown = new JComboBox<>();

    // Book selection (only books borrowed by selected patron)
    private JComboBox<Book> bookDropdown = new JComboBox<>();

    private JButton returnBtn = new JButton("Return");
    private JButton cancelBtn = new JButton("Cancel");

    /**
     * ReturnBookWindow.
     *
     * @param mw = mainWindow that shares the library.
     */
    public ReturnBookWindow(MainWindow mw) {
        super(mw, "Return Book", true);
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

        setSize(450, 220);

        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        topPanel.add(new JLabel("Select patron:"));
        topPanel.add(patronDropdown);

        topPanel.add(new JLabel("Borrowed books:"));
        topPanel.add(bookDropdown);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.add(new JLabel(" "));
        bottomPanel.add(returnBtn);
        bottomPanel.add(cancelBtn);

        patronDropdown.addActionListener(e -> populateBookDropdown());

        returnBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(mw);
    }

    /**
     * Populates the patron drop down sorted alphabetically
     */
    private void populatePatronDropdown() {
        patronDropdown.removeAllItems();

        List<Patron> patrons = mw.getLibrary().getPatrons()
            // Only patrons who currently have borrowed books
            .stream()
            .filter(p -> !p.getBooks().isEmpty())
            // Sort alphabetically by name
            .sorted(Comparator.comparing(Patron::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();

        for (Patron p : patrons) {
            patronDropdown.addItem(p);
        }

        populateBookDropdown();
    }

    /**
     * Populates the book drop down with books borrowed by the selected patron.
     */
    private void populateBookDropdown() {
        bookDropdown.removeAllItems();

        Patron patron = (Patron) patronDropdown.getSelectedItem();
        if (patron == null) {
            return;
        }

        for (Book b : patron.getBooks()) {
            bookDropdown.addItem(b);
        }
    }

    /**
     * Handles button click events.
     *
     * @param ae the action event triggered by user interaction.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == returnBtn) {
            returnSelectedBook();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    /**
     * Executes the return book command with roll back on failure.
     */
    private void returnSelectedBook() {

        Patron patron = (Patron) patronDropdown.getSelectedItem();
        Book book = (Book) bookDropdown.getSelectedItem();

        if (patron == null || book == null) {
            JOptionPane.showMessageDialog(
                mw,
                "Please select both a patron and a book.",
                "Selection Required",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Snapshot for roll back
        Library snapshot = mw.getLibrary().copy();

        try {
            Command ret = new ReturnBook(patron.getId(), book.getId());
            ret.execute(mw.getLibrary(), LocalDate.now());

            // Persist changes
            LibraryData.store(mw.getLibrary());

            mw.displayBooks();
            mw.showPatrons();
            this.setVisible(false);

            JOptionPane.showMessageDialog(
                mw,
                "Book returned successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ioEx) {
            // Roll back state
            mw.setLibrary(snapshot);

            JOptionPane.showMessageDialog(
                mw,
                "Failed to save data.\nReturn was rolled back.",
                "Storage Error",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(
                mw,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
