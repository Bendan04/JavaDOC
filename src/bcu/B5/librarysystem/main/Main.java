//package bcu.B5.librarysystem.main;
//
//import bcu.B5.librarysystem.commands.Command;
//import bcu.B5.librarysystem.data.LibraryData;
//import bcu.B5.librarysystem.model.Library;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.time.LocalDate;
//
//public class Main {
//
//    public static void main(String[] args) {
//
//        try {
//            Library library = LibraryData.load();
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//
//            while (true) {
//                System.out.print("> ");
//                String line = br.readLine();
//
//                if (line == null || line.equalsIgnoreCase("exit")) {
//                    break;
//                }
//
//                Command cmd = CommandParser.parse(line);
//
//                // --- TRANSACTION START ---
//                Library snapshot = library.copy();
//
//                try {
//                    cmd.execute(library, LocalDate.now());
//                    LibraryData.store(library);
//                } catch (IOException e) {
//                    library = snapshot;
//                    System.out.println("ERROR: Failed to save data. Changes rolled back.");
//                } catch (LibraryException e) {
//                    System.out.println(e.getMessage());
//                }
//                // --- TRANSACTION END ---
//            }
//
//        } catch (Exception e) {
//            System.out.println("Fatal error: " + e.getMessage());
//        }
//    }
//}
