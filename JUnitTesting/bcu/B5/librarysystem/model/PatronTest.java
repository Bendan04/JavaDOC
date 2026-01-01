package bcu.B5.librarysystem.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PatronTest {

    @Test
    void StoresEmailTest() {
        Patron patron = new Patron(1, "Ben McNally", "07123456789", "ben@test.com"); // Create new patron object.
        assertEquals("ben@test.com", patron.getEmail()); 
    }

    @Test
    void emailUpdatedTest() {
        Patron patron = new Patron(1, "Ben", "07123", "old@test.com");
        patron.setEmail("new@test.com"); // Setting new email using setEmail method.
        assertEquals("new@test.com", patron.getEmail());
    }

    @Test
    void longDetailsEmailTest() {
        Patron patron = new Patron(1, "Ben", "07123", "ben@test.com");
        String details = patron.getDetailsLong();
        assertTrue(details.contains("Email: ben@test.com"));
    }
}
