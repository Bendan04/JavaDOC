package bcu.B5.librarysystem.data;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LibraryData {

    private static final List<DataManager> dataManagers = new ArrayList<>();

    static {
        dataManagers.add(new BookDataManager());
        dataManagers.add(new PatronDataManager());
        dataManagers.add(new LoanDataManager());
    }

    public static Library load() throws LibraryException, IOException {

        Library library = new Library();
        for (DataManager dm : dataManagers) {
            dm.loadData(library);
        }
        return library;
    }

    /**
     * Stores the entire library to file storage.
     * If any DataManager fails, an IOException is thrown
     * and no partial success should be assumed by the caller.
     */
    public static void store(Library library) throws IOException {

        try {
            for (DataManager dm : dataManagers) {
                dm.storeData(library);
            }
        } catch (IOException e) {
            // Fail fast – caller must rollback in-memory state
            throw new IOException(
                "Failed to store library data. Changes were not persisted.",
                e
            );
        }
    }

}