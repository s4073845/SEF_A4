package Maven.SEF_A4;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

// Integration tests for BusRepository - uses real TXT files
public class BusRepoTesting {

    private static final String FILE = "test_buses.txt";
    private BusRepository repo;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(FILE));
        repo = new BusRepository(FILE);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(FILE));
    }

    // IT-B1: valid bus is stored and can be retrieved from the file
    @Test
    void it_ValidBusStoredAndRetrieved() {
        repo.add(new Bus("12345678", 40, 75.0, "Diesel"));

        Bus found = new BusRepository(FILE).retrieve("12345678");
        assertNotNull(found);
        assertEquals(40, found.getCapacity());
        assertEquals("Diesel", found.getFuelType());
    }

    // IT-B2: invalid bus is rejected and nothing is written to the file
    @Test
    void it_InvalidBus_Rejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Bus("ABCD1234", 30, 50.0, "Diesel"));
        assertEquals(0, repo.count());
    }

    // IT-B3: update is persisted - new values readable by a fresh repository instance
    @Test
    void it_UpdatePersisted() {
        repo.add(new Bus("22334455", 60, 80.0, "Hybrid"));
        repo.update("22334455", 50, 90.0, "Diesel");

        Bus updated = new BusRepository(FILE).retrieve("22334455");
        assertEquals(50, updated.getCapacity());
        assertEquals(90.0, updated.getFuelLevel(), 0.001);
        assertEquals("Diesel", updated.getFuelType());
    }

    // IT-B4: count is updated correctly after adds
    @Test
    void it_CountUpdatedCorrectly() throws IOException {
        assertEquals(0, repo.count());

        repo.add(new Bus("11111111", 30, 60.0, "Diesel"));
        assertEquals(1, repo.count());

        repo.add(new Bus("22222222", 50, 40.0, "Hybrid"));
        assertEquals(2, repo.count());

        // Confirm file has exactly 2 non-empty lines
        long lines = Files.lines(Paths.get(FILE)).filter(l -> !l.trim().isEmpty()).count();
        assertEquals(2, lines);
    }

    // IT-B5: capacity increase is rejected - file stays unchanged
    @Test
    void it_CapacityIncrease_Rejected() {
        repo.add(new Bus("44444444", 40, 50.0, "Diesel"));
        assertThrows(IllegalArgumentException.class, () ->
                repo.update("44444444", 45, 50.0, "Diesel"));
        assertEquals(40, repo.retrieve("44444444").getCapacity());
    }
}
