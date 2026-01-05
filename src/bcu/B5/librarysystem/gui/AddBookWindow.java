package bcu.B5.librarysystem.gui;

import bcu.B5.librarysystem.commands.AddBook;
import bcu.B5.librarysystem.commands.Command;
import bcu.B5.librarysystem.main.LibraryException;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import bcu.B5.librarysystem.data.LibraryData;
import bcu.B5.librarysystem.model.Book;

public class AddBookWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField titleText = new JTextField();
    private JTextField authText = new JTextField();
    private JTextField pubDateText = new JTextField();
    private JTextField publisherText = new JTextField();

    private JButton addBtn = new JButton("Add");
    private JButton cancelBtn = new JButton("Cancel");

    public AddBookWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {

        } 

        setTitle("Add a New Book");

        setSize(300, 200);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(5, 2));
        topPanel.add(new JLabel("Title : "));
        topPanel.add(titleText);
        topPanel.add(new JLabel("Author : "));
        topPanel.add(authText);
        topPanel.add(new JLabel("publisher : "));
        topPanel.add(publisherText);
        topPanel.add(new JLabel("Publishing Date : "));
        topPanel.add(pubDateText);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(addBtn);
        bottomPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addBook();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }

    }

    private void addBook() {
        // Snapshot the current state (books list)
        List<Book> before = new ArrayList<>(mw.getLibrary().getBooks());

        try {
            String title = titleText.getText();
            String author = authText.getText();
            String publicationYear = pubDateText.getText();
            String publisher = publisherText.getText();

            // create and execute the AddBook Command
            Command addBook = new AddBook(title, author, publicationYear, publisher);
            addBook.execute(mw.getLibrary(), LocalDate.now());

            // attempt to store changes
            LibraryData.store(mw.getLibrary());

            // refresh the view with the list of books
            mw.displayBooks();

            // hide (close) the AddBookWindow
            this.setVisible(false);

        } catch (IOException ioEx) {
            // rollback to previous state
            mw.getLibrary().getBooks().clear();
            mw.getLibrary().getBooks().addAll(before);

            JOptionPane.showMessageDialog(
                this,
                "Failed to save data to file.\nChanges were rolled back.\n\n" + ioEx.getMessage(),
                "Storage Error",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
