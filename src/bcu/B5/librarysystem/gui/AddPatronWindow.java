package bcu.B5.librarysystem.gui;

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
import bcu.B5.librarysystem.model.Patron;

import bcu.B5.librarysystem.commands.AddPatron;
import bcu.B5.librarysystem.commands.Command;
import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;


/**
 * Window used to add a new patron to the library system.
 * <p>
 * Patron details are entered and validated before executing the {@link bcu.B5.librarysystem.commands.AddPatron} command.
 * </p>
 * <p>
 * Corrupted saves will roll back all changes.
 * </p>
 */

public class AddPatronWindow extends JFrame implements ActionListener {
	
    private MainWindow mw;
    private JTextField nameText = new JTextField();
    private JTextField phoneText = new JTextField();
    private JTextField emailText = new JTextField();

    private JButton addBtn = new JButton("Add");
    private JButton cancelBtn = new JButton("Cancel");

	/**
	 * AddPatronWindow.
	 * 
	 * @param mw = mainWindow that shares the library.
	 */
    public AddPatronWindow(MainWindow mw) {
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

        setTitle("Add a New Patron");

        setSize(300, 200);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(5, 2));
        topPanel.add(new JLabel("Name : "));
        topPanel.add(nameText);
        topPanel.add(new JLabel("Phone : "));
        topPanel.add(phoneText);
        topPanel.add(new JLabel("Email : "));
        topPanel.add(emailText);

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
    
    /**
     * Verify's the users input.
     *
     * @param email the email address to validate.
     * @return true if the email contains basic required characters.
     */
    private boolean validEmail(String email) {
        return email != null && email.contains("@") && email.contains("."); // Must contain @ and a .
    }

    /**
     * Handles button click events.
     *
     * @param ae the action event triggered by user interaction.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addPatron();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    /**
     * Adds a new patron to the library.
     * <p>
     * The library state is backed up before any modifications.
     * </p>
     */
    private void addPatron() {

        // Snapshot entire library (safe rollback)
        Library before = mw.getLibrary().copy();

        try {
            String name = nameText.getText();
            String phone = phoneText.getText();
            String email = emailText.getText();

            if (!validEmail(email)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Command addPatron = new AddPatron(name, phone, email);
            addPatron.execute(mw.getLibrary(), LocalDate.now());

            // Attempt to persist
            LibraryData.store(mw.getLibrary());

            mw.showPatrons();
            this.setVisible(false);

        } catch (IOException ioEx) {
            // Rollback entire system state
            mw.setLibrary(before);

            JOptionPane.showMessageDialog(
                this,
                "Failed to save data to file.\nChanges were rolled back.",
                "Storage Error",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
