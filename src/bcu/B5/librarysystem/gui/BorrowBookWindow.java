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
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;

public class BorrowBookWindow extends JFrame implements ActionListener {

    private MainWindow mw;

    // Patron search
    private JTextField patronSearchField = new JTextField();
    private JComboBox<Patron> patronDropdown = new JComboBox<>();

    // Book search
    private JTextField bookSearchField = new JTextField();
    private JComboBox<Book> bookDropdown = new JComboBox<>();

    private JButton borrowBtn = new JButton("Borrow");
    private JButton cancelBtn = new JButton("Cancel");

    public BorrowBookWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
        updatePatronDropdown("");
        updateBookDropdown("");
    }

    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}

        setTitle("Borrow Book");
        setSize(450, 240);

        JPanel topPanel = new JPanel(new GridLayout(4, 2));

        topPanel.add(new JLabel("Search patron:"));
        topPanel.add(patronSearchField);
        topPanel.add(new JLabel("Select patron:"));
        topPanel.add(patronDropdown);

        topPanel.add(new JLabel("Search book:"));
        topPanel.add(bookSearchField);
        topPanel.add(new JLabel("Select book:"));
        topPanel.add(bookDropdown);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.add(new JLabel(" "));
        bottomPanel.add(borrowBtn);
        bottomPanel.add(cancelBtn);

        patronSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updatePatronDropdown(patronSearchField.getText());
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updatePatronDropdown(patronSearchField.getText());
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updatePatronDropdown(patronSearchField.getText());
            }
        });

        bookSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateBookDropdown(bookSearchField.getText());
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateBookDropdown(bookSearchField.getText());
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateBookDropdown(bookSearchField.getText());
            }
        });

        borrowBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private void updatePatronDropdown(String filter) {
        patronDropdown.removeAllItems();

        List<Patron> matches = mw.getLibrary().getPatrons().stream()
            .filter(p -> p.getName().toLowerCase().contains(filter.toLowerCase()))
            .collect(Collectors.toList());

        for (Patron p : matches) {
            patronDropdown.addItem(p);
        }
    }

    private void updateBookDropdown(String filter) {
        bookDropdown.removeAllItems();

        List<Book> matches = mw.getLibrary().getBooks().stream()
            .filter(b -> !b.isOnLoan()) // only available books
            .filter(b -> b.getTitle().toLowerCase().contains(filter.toLowerCase()))
            .collect(Collectors.toList());

        for (Book b : matches) {
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

        // Snapshot for rollback
        Library snapshot = mw.getLibrary().copy();

        try {
            Command borrow = new BorrowBook(patron.getId(), book.getId());
            borrow.execute(mw.getLibrary(), LocalDate.now());

            // Immediate persistence (Option A)
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
