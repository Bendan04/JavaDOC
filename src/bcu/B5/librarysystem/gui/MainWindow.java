package bcu.B5.librarysystem.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import java.io.IOException; // Required imports for both exceptions otherwise application wouldn't run as java doesn't auto import exception types.
import bcu.B5.librarysystem.main.LibraryException; // ^^^^
import bcu.B5.librarysystem.data.LibraryData; //Required import as these are in different packages and eclipse is weird.
import javax.swing.JOptionPane; //Adding option pane as this allows us to check if a book is on loan easily.
import bcu.B5.librarysystem.model.Patron; // Importing Patron for 6.2


import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Library;

public class MainWindow extends JFrame implements ActionListener {

	private JMenuBar menuBar;
    private JMenu adminMenu;
    private JMenu booksMenu;
    private JMenu membersMenu;

    private JMenuItem adminExit;

    private JMenuItem booksView;
    private JMenuItem booksAdd;
    private JMenuItem booksDel;	
    private JMenuItem booksIssue;
    private JMenuItem booksReturn;

    private JMenuItem memView;
    private JMenuItem memAdd;
    private JMenuItem memDel;

    private Library library;

    public MainWindow(Library library) {

        initialize();
        this.library = library;
    } 
    
    public Library getLibrary() {
        return library;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {

        }

        setTitle("Library Management System");

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        //adding adminMenu menu and menu items
        adminMenu = new JMenu("Admin");
        menuBar.add(adminMenu);

        adminExit = new JMenuItem("Exit");
        adminMenu.add(adminExit);
        adminExit.addActionListener(this);

        // adding booksMenu menu and menu items
        booksMenu = new JMenu("Books");
        menuBar.add(booksMenu);

        booksView = new JMenuItem("View");
        booksAdd = new JMenuItem("Add");
        booksDel = new JMenuItem("Delete");
        booksIssue = new JMenuItem("Issue");
        booksReturn = new JMenuItem("Return");
        booksMenu.add(booksView);
        booksMenu.add(booksAdd);
        booksMenu.add(booksDel);
        booksMenu.add(booksIssue);
        booksMenu.add(booksReturn);
        for (int i = 0; i < booksMenu.getItemCount(); i++) {
            booksMenu.getItem(i).addActionListener(this);
        }

        // adding membersMenu menu and menu items
        membersMenu = new JMenu("Patrons");
        menuBar.add(membersMenu);

        memView = new JMenuItem("View");
        memAdd = new JMenuItem("Add");
        memDel = new JMenuItem("Delete");

        membersMenu.add(memView);
        membersMenu.add(memAdd);
        membersMenu.add(memDel);

        memView.addActionListener(this);
        memAdd.addActionListener(this);
        memDel.addActionListener(this);

        setSize(800, 500);

        setVisible(true);
        setAutoRequestFocus(true);
        toFront();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
/* Uncomment the following line to not terminate the console app when the window is closed */
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);        

    }	

/* Uncomment the following code to run the GUI version directly from the IDE */
    public static void main(String[] args) throws IOException, LibraryException {
        Library library = LibraryData.load();
        new MainWindow(library);			
    }



    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == adminExit) {
            System.exit(0);
        } else if (ae.getSource() == booksView) {
            displayBooks();
            
        } else if (ae.getSource() == booksAdd) {
            new AddBookWindow(this);
            
        } else if (ae.getSource() == booksDel) {
            
            
        } else if (ae.getSource() == booksIssue) {
            
            
        } else if (ae.getSource() == booksReturn) {
            
            
        } else if (ae.getSource() == memView) { // Mem is patrons
            displayPatrons();
            
        } else if (ae.getSource() == memAdd) {
            
            
        } else if (ae.getSource() == memDel) {
            
            
        }
    }
    
    private void displayPatrons() {

        String[] columns = { "Patron ID", "Name", "Email", "Books on Loan"};
        java.util.List<Patron> patrons = library.getPatrons();
        Object[][] data = new Object[patrons.size()][4];

        for (int i = 0; i < patrons.size(); i++) { 
            Patron p = patrons.get(i);
            data[i][0] = p.getId();
            data[i][1] = p.getName();
            data[i][2] = p.getEmail();
            data[i][3] = p.getBooks().size(); // Getting the amount of books they have on loan and getting the size of the list.
        }

        JTable table = new JTable(data, columns); // Setting the columns and data for displaying purposes.
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            Patron patron = patrons.get(row);

            if (!patron.getBooks().isEmpty()) {

                String string = ""; // We need to create the output as a string as the skeleton code outputs the books as a list.
                for (Book book : patron.getBooks()) { // Enhanced for loop (Pretty much the same as a regular nested loop but more compact.)
                    string += book.getTitle() + " authored by " + book.getAuthor();
                }

                JOptionPane.showMessageDialog(MainWindow.this, string, patron.getName() + " books.", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        this.getContentPane().removeAll(); // Copied and pasted most of these from displayBooks method.
        this.getContentPane().add(new JScrollPane(table));
        this.revalidate();
    }


    public void displayBooks() {
        List<Book> booksList = library.getBooks();
        // headers for the table
        String[] columns = new String[]{"Title", "Author", "Pub Date", "Status"};

        Object[][] data = new Object[booksList.size()][6];
        for (int i = 0; i < booksList.size(); i++) {
            Book book = booksList.get(i);
            data[i][0] = book.getTitle();
            data[i][1] = book.getAuthor();
            data[i][2] = book.getPublicationYear();
            data[i][3] = book.getStatus();
        }

        JTable table = new JTable(data, columns);
        table.getSelectionModel().addListSelectionListener(e -> { // Whenever the selected row changes then call this action. (Listens for changes in selection)
        	
            int row = table.getSelectedRow(); // Making sure the correct row index is selected. 
            
            Book book = booksList.get(row);
            if (book.isOnLoan()) {
                JOptionPane.showMessageDialog(
                    MainWindow.this, book.getLoan().getPatron().getDetailsLong(), "Patron: ", JOptionPane.INFORMATION_MESSAGE // Message pop up.
                );
            }
        });
        this.getContentPane().removeAll();
        this.getContentPane().add(new JScrollPane(table));
        this.revalidate();    
    }	
}
