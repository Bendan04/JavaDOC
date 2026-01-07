package bcu.B5.librarysystem.gui;

import bcu.B5.librarysystem.commands.Command;
import bcu.B5.librarysystem.commands.DeleteBook;
import bcu.B5.librarysystem.data.LibraryData;
import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Book;

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
 * Window that allows users to perform a soft delete on books.
 * <p>
 * Only books that are not on loan may be deleted.
 * </p>
 */
public class DeleteBookWindow extends JFrame implements ActionListener {

	/**
	 * DeleteBookWindow.
	 *
	 * @param mw = mainWindow that shares the library.
	 */
    private MainWindow mw; // reference to the main application window

    private JComboBox<Book> bookDropdown = new JComboBox<>();

    private JButton deleteBtn = new JButton("Delete");
    private JButton cancelBtn = new JButton("Cancel");

    public DeleteBookWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
        populateDropdown();
    }

    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {} // ignore look-and-feel errors

        setTitle("Delete Book");
        setSize(500, 160);

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.add(new JLabel("Select book:"));
        topPanel.add(bookDropdown);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.add(new JLabel(" "));
        bottomPanel.add(deleteBtn);
        bottomPanel.add(cancelBtn);

        deleteBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(mw);
        setVisible(true);
    }

    /**
     * 
     * Non-deleted books sorted alphabetically.
     * @param patrons = list of patrons from library system.
     */
    private void populateDropdown() {
        bookDropdown.removeAllItems();

        List<Book> books = mw.getLibrary().getBooks()
            .stream()
            .filter(b -> !b.isDeleted())
            .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
            .toList();

        for (Book b : books) {
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
        if (ae.getSource() == deleteBtn) {
            deleteSelectedBook();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    /**
     * Soft delete selected book.
     * <p>
     * The library state is backed up before any modifications.
     * </p>
     */
    private void deleteSelectedBook() {

        Book selected = (Book) bookDropdown.getSelectedItem();

        if (selected == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a book to delete.",
                "No Selection",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Library snapshot = mw.getLibrary().copy(); // backup for rollback

        try {
            Command delete = new DeleteBook(selected.getId());
            delete.execute(mw.getLibrary(), LocalDate.now());

            LibraryData.store(mw.getLibrary());

            mw.displayBooks();
            this.setVisible(false);

            JOptionPane.showMessageDialog(
                this,
                "Book deleted successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ioEx) {
            mw.setLibrary(snapshot); // rollback on storage failure

            JOptionPane.showMessageDialog(
                this,
                "Failed to save data.\nDeletion was rolled back.",
                "Storage Error",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
