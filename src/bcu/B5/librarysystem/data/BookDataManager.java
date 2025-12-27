package bcu.B5.librarysystem.data;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Book;
import bcu.B5.librarysystem.model.Library;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class BookDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/books.txt";

    @Override
    public void loadData(Library library) throws IOException, LibraryException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int lineIdx = 1;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isBlank()) {
                    lineIdx++;
                    continue;
                }

                String[] parts = line.split("::");

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String title = parts[1].trim();
                    String author = parts[2].trim();
                    String year = parts[3].trim();
                    String publisher = parts[4].trim();

                    Book book = new Book(id, title, author, year, publisher);
                    library.addBook(book);

                } catch (Exception ex) {
                    throw new LibraryException(
                        "Unable to parse book data on line " + lineIdx + "\nError: " + ex.getMessage()
                    );
                }

                lineIdx++;
            }
        }
    }

    @Override
    public void storeData(Library library) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Book book : library.getBooks()) {
                out.print(book.getId() + "::");
                out.print(book.getTitle() + "::");
                out.print(book.getAuthor() + "::");
                out.print(book.getPublicationYear() + "::");
                out.print(book.getPublisher() + "::");
                out.println();
            }
        }
    }
}