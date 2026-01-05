package bcu.B5.librarysystem.gui;

import bcu.B5.librarysystem.commands.BorrowBook;
import bcu.B5.librarysystem.commands.Command;
import bcu.B5.librarysystem.data.LibraryData;
import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Library;
import bcu.B5.librarysystem.model.Patron;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;

public class BorrowBookWindow extends JFrame implements ActionListener {

    private MainWindow mw;

    private JComboBox<Patron> patronDropdown = new JComboBox<>();
    private JComboBox<Book> bookDropdown = new JComboBox<>();

    private JButton borrowBtn = new JButton("Borrow");
    private JButton cancelBtn = new JButton("Cancel");

    public BorrowBookWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
        populatePatronDropdown();
        populateBookDropdown();
    }

    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}

        setTitle("Borrow Book");
        setSize(450, 200);

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        topPanel.add(new JLabel("Select patron:"));
        topPanel.add(patronDropdown);

        topPanel.add(new JLabel("Select book:"));
        topPanel.add(bookDropdown);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.add(new JLabel(" "));
        bottomPanel.add(borrowBtn);
        bottomPanel.add(cancelBtn);

        borrowBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private void populatePatronDropdown() {
        patronDropdown.removeAllItems();

        List<Patron> patrons = mw.getLibrary().getPatrons().stream()
            .sorted(Comparator.comparing(Patron::getName, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());

        for (Patron p : patrons) {
            patronDropdown.addItem(p);
        }
    }

    private void populateBookDropdown() {
        bookDropdown.removeAllItems();

        List<Book> books = mw.getLibrary().getBooks().stream()
            .filter(b -> !b.isOnLoan()) // available books only
            .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());

        for (Book b : books) {
            bookDropdown.addItem(b);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == borrowBtn) {
            borrowSelectedBook();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    private void borrowSelectedBook() {

        Patron patron = (Patron) patronDropdown.getSelectedItem();
        Book book = (Book) bookDropdown.getSelectedItem();

        if (patron == null || book == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select both a patron and a book.",
                "Selection Required",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Library snapshot = mw.getLibrary().copy();

        try {
            Command borrow = new BorrowBook(patron.getId(), book.getId());
            borrow.execute(mw.getLibrary(), LocalDate.now());

            LibraryData.store(mw.getLibrary());

            mw.displayBooks();
            mw.showPatrons();
            this.setVisible(false);

            JOptionPane.showMessageDialog(
                this,
                "Book borrowed successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ioEx) {
            mw.setLibrary(snapshot);

            JOptionPane.showMessageDialog(
                this,
                "Failed to save data.\nBorrow operation was rolled back.",
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
