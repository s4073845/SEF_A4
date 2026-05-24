package Maven.SEF_A4;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

// Integration tests for DriverRepository - uses real TXT files
public class DriverRepoTesting {

    private static final String FILE = "test_drivers.txt";
    private DriverRepository repo;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(FILE));
        repo = new DriverRepository(FILE);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(FILE));
    }

    // IT-D1: valid driver is stored and can be retrieved from the file
    @Test
    void it_ValidDriverStoredAndRetrieved() {
        Driver d = new Driver("23@!abcdAB", "Alice Smith", 5, "Light",
                "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        repo.add(d);

        // Re-read from disk using a new repository instance
        Driver found = new DriverRepository(FILE).retrieve("23@!abcdAB");
        assertNotNull(found);
        assertEquals("Alice Smith", found.getName());
        assertEquals("Light", found.getLicenseType());
    }

    // IT-D2: invalid driver is rejected and nothing is written to the file
    @Test
    void it_InvalidDriver_Rejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Driver("BADID", "Bob", 2, "Heavy",
                        "1|A St|City|ST|Country", "01-01-1990"));
        assertEquals(0, repo.count());
    }

    // IT-D3: update is persisted - new values readable by a fresh repository instance
    @Test
    void it_UpdatePersisted() {
        repo.add(new Driver("34##xyxyXY", "Bob Jones", 3, "Medium",
                "5|Oak Rd|Sydney|NSW|Australia", "01-01-1985"));

        repo.update("34##xyxyXY", 4, "Heavy",
                "5|Oak Rd|Sydney|NSW|Australia", "01-01-1985");

        Driver updated = new DriverRepository(FILE).retrieve("34##xyxyXY");
        assertEquals(4, updated.getExperienceYears());
        assertEquals("Heavy", updated.getLicenseType());
        assertEquals("Bob Jones", updated.getName()); // D5: name unchanged
    }

    // IT-D4: count is updated correctly after adds
    @Test
    void it_CountUpdatedCorrectly() throws IOException {
        assertEquals(0, repo.count());

        repo.add(new Driver("23@!abcdAB", "Alice", 5, "Light",
                "1|A St|Melbourne|VIC|Australia", "01-01-1990"));
        assertEquals(1, repo.count());

        repo.add(new Driver("34##xyxyXY", "Bob", 3, "Medium",
                "2|B St|Sydney|NSW|Australia", "02-02-1985"));
        assertEquals(2, repo.count());

        // Confirm file has exactly 2 non-empty lines
        long lines = Files.lines(Paths.get(FILE)).filter(l -> !l.trim().isEmpty()).count();
        assertEquals(2, lines);
    }

    // IT-D5: duplicate driverID is rejected by repository (D1 uniqueness)
    @Test
    void it_DuplicateID_Rejected() {
        repo.add(new Driver("23@!abcdAB", "Alice", 5, "Light",
                "1|A St|Melbourne|VIC|Australia", "01-01-1990"));
        assertThrows(IllegalArgumentException.class, () ->
                repo.add(new Driver("23@!abcdAB", "Copy", 3, "Medium",
                        "2|B St|Sydney|NSW|Australia", "03-03-1992")));
        assertEquals(1, repo.count());
    }
}
