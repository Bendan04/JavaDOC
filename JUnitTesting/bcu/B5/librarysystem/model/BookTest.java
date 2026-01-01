package bcu.B5.librarysystem.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class BookTest {

    @Test
    void publisherTest() {
        Book book = new Book(1, "Java Work", "Test", "2025", "TestPublisher"); //Create new book object.
        assertEquals("TestPublisher", book.getPublisher()); // Try and check the getPublisher method against our argument.
    }

    @Test
    void LongDetailsTest() {
        Book book = new Book(1, "Test", "Test", "2025", "TestPublisher"); //Create another book object
        assertTrue(book.getDetailsLong().contains("Publisher: TestPublisher")); // Using contains as the typical assertion didn't work correctly.
    }
}
