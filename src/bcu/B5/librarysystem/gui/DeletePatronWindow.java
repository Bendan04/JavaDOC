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
import java.util.Comparator; 
import java.util.List; 

import javax.swing.*; 

/**
 * Window that allows administrators to perform a soft delete on patrons.
 * <p>
 * Only patrons with no books on loan may be deleted.
 * </p>
 */
public class DeletePatronWindow extends JFrame implements ActionListener {

	/**
	 * DeletePatronWindow.
	 *
	 * @param mw = mainWindow that shares the library.
	 */
    private MainWindow mw; // reference to the main application window

    private JComboBox<Patron> patronDropdown = new JComboBox<>();

    private JButton deleteBtn = new JButton("Delete");
    private JButton cancelBtn = new JButton("Cancel");

    public DeletePatronWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
        populateDropdown();
    }

    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {} // ignore look-and-feel errors

        setTitle("Delete Patron");
        setSize(500, 160);

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.add(new JLabel("Select patron:"));
        topPanel.add(patronDropdown);

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
     * Patrons sorted alphabetically.
     */
    private void populateDropdown() {
        patronDropdown.removeAllItems();

        List<Patron> patrons = mw.getLibrary().getPatrons()
            .stream()
            .sorted(Comparator.comparing(Patron::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();

        for (Patron p : patrons) {
            patronDropdown.addItem(p);
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
            deleteSelectedPatron();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    /**
     * Soft delete selected patron.
     * <p>
     * The library state is backed up before any modifications.
     * </p>
     */
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

        Library snapshot = mw.getLibrary().copy(); // backup for rollback

        try {
            Command delete = new DeletePatron(selected.getId());
            delete.execute(mw.getLibrary(), LocalDate.now());

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
