package bcu.B5.librarysystem.gui;

import bcu.B5.librarysystem.commands.Command;
import bcu.B5.librarysystem.data.LibraryData;
import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;
import bcu.B5.librarysystem.commands.RenewBook; // Required import for task 8.4 

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
 * Window that allows a patron to renew a borrowed book.
 * <p>
 * Only books currently on loan to the selected patron can be renewed.
 * </p>
 */
public class RenewBookWindow extends JDialog implements ActionListener {

    private MainWindow mw;

    private JComboBox<Patron> patronDropdown = new JComboBox<>(); // Patron selection
    private JComboBox<Book> bookDropdown = new JComboBox<>(); // Book selection (only books borrowed by selected patron)
 
    private JButton renewBtn = new JButton("Renew");
    private JButton cancelBtn = new JButton("Cancel");

	/**
	 * RenewBookWindow.
	 * 
	 * @param mw = mainWindow that shares the library.
	 */
    public RenewBookWindow(MainWindow mw) {
        super(mw, "Renew Book", true);
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

        setSize(500, 200);

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.add(new JLabel("Select patron:"));
        topPanel.add(patronDropdown);
        topPanel.add(new JLabel("Select book:"));
        topPanel.add(bookDropdown);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.add(new JLabel(" "));
        bottomPanel.add(renewBtn);
        bottomPanel.add(cancelBtn);

        patronDropdown.addActionListener(e -> populateBookDropdown());

        renewBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        getContentPane().add(topPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(mw);
    }

    /**
     * Populates the patron drop down.
     */
    private void populatePatronDropdown() {
        patronDropdown.removeAllItems();

        List<Patron> patrons = mw.getLibrary().getPatrons() // Only patrons who currently have borrowed books.
            .stream() // Turns list into stream which allows for sorting.
            .filter(p -> !p.getBooks().isEmpty()) // Lambda function. (https://codemia.io/knowledge-hub/path/what_does_the_arrow_operator_-_do_in_java)
            .sorted(Comparator.comparing(Patron::getName, String.CASE_INSENSITIVE_ORDER)) // Sort alphabetically by name.
            .toList(); // Convert back to list.

        for (Patron p : patrons) {
            patronDropdown.addItem(p);
        }

        populateBookDropdown();
    }

    /**
     * Populates the book drop down with books on loan to the selected patron.
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
        if (ae.getSource() == renewBtn) {
            renewBook();
        } else if (ae.getSource() == cancelBtn) {
            setVisible(false);
        }
    }

    /**
     * Renew the selected book for the selected patron.
     * <p>
     * Executes the {@link bcu.B5.librarysystem.commands.RenewBook} command for the patron.
     */
    private void renewBook() {

        Patron patron = (Patron) patronDropdown.getSelectedItem();
        Book book = (Book) bookDropdown.getSelectedItem();

        if (patron == null || book == null) { // || Logical or
            JOptionPane.showMessageDialog(this, "You need to select both patron and book.", "Selection Required", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Library snapshot = mw.getLibrary().copy(); // Save library state.

        try {
            Command renew = new RenewBook(patron.getId(), book.getId());
            renew.execute(mw.getLibrary(), LocalDate.now());

            LibraryData.store(mw.getLibrary()); // Persist changes.

            mw.displayBooks();
            mw.showPatrons();
            setVisible(false);
            JOptionPane.showMessageDialog(mw, "Book renewed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ioEx) {
            mw.setLibrary(snapshot); // Rolling back to previous state.

            JOptionPane.showMessageDialog(mw, "Failed to save data.\nRenewal was rolled back.", "Storage Error", JOptionPane.ERROR_MESSAGE);

        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(mw, ex.getMessage(), "Error, renew book failed.", JOptionPane.ERROR_MESSAGE);
        }
    }
}
