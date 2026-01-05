package bcu.B5.librarysystem.gui;

import bcu.B5.librarysystem.commands.Command;
import bcu.B5.librarysystem.commands.DeletePatron;
import bcu.B5.librarysystem.data.LibraryData;
import bcu.B5.librarysystem.main.LibraryException;
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

public class DeletePatronWindow extends JFrame implements ActionListener {

    private MainWindow mw;

    private JTextField searchField = new JTextField();
    private JComboBox<Patron> patronDropdown = new JComboBox<>();

    private JButton deleteBtn = new JButton("Delete");
    private JButton cancelBtn = new JButton("Cancel");

    public DeletePatronWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
        updateDropdown("");
    }

    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}

        setTitle("Delete Patron");
        setSize(400, 180);

        JPanel topPanel = new JPanel(new GridLayout(3, 2));
        topPanel.add(new JLabel("Search name:"));
        topPanel.add(searchField);
        topPanel.add(new JLabel("Select patron:"));
        topPanel.add(patronDropdown);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.add(new JLabel(" "));
        bottomPanel.add(deleteBtn);
        bottomPanel.add(cancelBtn);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateDropdown(searchField.getText());
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateDropdown(searchField.getText());
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateDropdown(searchField.getText());
            }
        });

        deleteBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private void updateDropdown(String filter) {
        patronDropdown.removeAllItems();

        List<Patron> matches = mw.getLibrary().getPatrons().stream()
            .filter(p -> p.getName().toLowerCase().contains(filter.toLowerCase()))
            .collect(Collectors.toList());

        for (Patron p : matches) {
            patronDropdown.addItem(p);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == deleteBtn) {
            deleteSelectedPatron();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    private void deleteSelectedPatron() {

        Patron selected = (Patron) patronDropdown.getSelectedItem();

        if (selected == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a patron to delete.",
                "No Selection",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Snapshot for rollback
        Library snapshot = mw.getLibrary().copy();

        try {
            Command delete = new DeletePatron(selected.getId());
            delete.execute(mw.getLibrary(), LocalDate.now());

            // Immediate persistence (Option A)
            LibraryData.store(mw.getLibrary());

            mw.showPatrons();
            this.setVisible(false);

            JOptionPane.showMessageDialog(
                this,
                "Patron deleted successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ioEx) {
            mw.setLibrary(snapshot);

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
