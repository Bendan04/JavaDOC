package bcu.B5.librarysystem.data;

import bcu.B5.librarysystem.main.LibraryException;
import bcu.B5.librarysystem.model.Library;

import java.io.IOException;

public interface DataManager {
    
    public static final String SEPARATOR = "::";
    
    public void loadData(Library library) throws IOException, LibraryException;
    public void storeData(Library library) throws IOException;
}
 