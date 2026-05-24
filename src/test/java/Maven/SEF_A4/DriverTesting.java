package Maven.SEF_A4;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Unit tests for Driver class - covers conditions D1 to D5
public class DriverTesting {

    // Helper: creates a simple valid driver
    private Driver makeDriver(String id) {
        return new Driver(id, "Alice Smith", 5, "Light",
                "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
    }

    // ── D1: Driver ID Rules ───────────────────────────────────────────────────

    @Test
    void d1_ValidID() {
        assertTrue(Driver.isValidDriverID("23@!abcdAB"));
    }

    @Test
    void d1_TooShort() {
        assertFalse(Driver.isValidDriverID("23@!abcAB")); // only 9 chars
    }

    @Test
    void d1_TooLong() {
        assertFalse(Driver.isValidDriverID("23@!abcdeAB")); // 11 chars
    }

    @Test
    void d1_FirstCharOutOfRange() {
        assertFalse(Driver.isValidDriverID("13@!abcdAB")); // '1' not in 2-9
    }

    @Test
    void d1_LastCharsNotUppercase() {
        assertFalse(Driver.isValidDriverID("23@!abcdab")); // lowercase at end
    }

    @Test
    void d1_OnlyOneSpecialChar() {
        assertFalse(Driver.isValidDriverID("23@aabcdAB")); // only 1 special
    }

    @Test
    void d1_NullID() {
        assertFalse(Driver.isValidDriverID(null));
    }

    // ── D2: Address Format ────────────────────────────────────────────────────

    @Test
    void d2_ValidAddress() {
        assertTrue(Driver.isValidAddress("12|Main St|Melbourne|VIC|Australia"));
    }

    @Test
    void d2_TooFewParts() {
        assertFalse(Driver.isValidAddress("12|Main St|Melbourne|VIC")); // only 4 parts
    }

    @Test
    void d2_EmptySegment() {
        assertFalse(Driver.isValidAddress("12|Main St||VIC|Australia")); // blank city
    }

    // ── D3: Birthday Format ───────────────────────────────────────────────────

    @Test
    void d3_ValidBirthdate() {
        assertTrue(Driver.isValidBirthdate("15-06-1990"));
    }

    @Test
    void d3_WrongDelimiter() {
        assertFalse(Driver.isValidBirthdate("15/06/1990"));
    }

    @Test
    void d3_InvalidMonth() {
        assertFalse(Driver.isValidBirthdate("01-13-1990")); // month 13
    }

    // ── D4: License Update Restriction ───────────────────────────────────────

    @Test
    void d4_UpdateLicenseAllowed_Under10Years() {
        Driver d = makeDriver("23@!abcdAB"); // 5 years
        assertDoesNotThrow(() -> d.update(5, "Medium",
                "12|Main St|Melbourne|VIC|Australia", "15-06-1990"));
    }

    @Test
    void d4_UpdateLicenseBlocked_Over10Years() {
        Driver d = new Driver("23@!abcdAB", "Bob", 11, "Heavy",
                "5|Oak Rd|Sydney|NSW|Australia", "01-01-1985");
        assertThrows(IllegalArgumentException.class,
                () -> d.update(11, "Light", "5|Oak Rd|Sydney|NSW|Australia", "01-01-1985"));
    }

    @Test
    void d4_ExactlyTenYears_LicenseCanChange() {
        Driver d = new Driver("34##xyxyXY", "Carol", 10, "Light",
                "1|Park Ave|Brisbane|QLD|Australia", "10-10-1980");
        assertDoesNotThrow(() -> d.update(10, "Medium",
                "1|Park Ave|Brisbane|QLD|Australia", "10-10-1980"));
    }

    // ── D5: Immutable Fields ──────────────────────────────────────────────────

    @Test
    void d5_DriverID_Unchanged_After_Update() {
        Driver d = makeDriver("23@!abcdAB");
        d.update(6, "Light", "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        assertEquals("23@!abcdAB", d.getDriverID());
    }

    @Test
    void d5_Name_Unchanged_After_Update() {
        Driver d = makeDriver("23@!abcdAB");
        d.update(6, "Light", "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        assertEquals("Alice Smith", d.getName());
    }

    @Test
    void d5_ConstructorRejectsInvalidID() {
        assertThrows(IllegalArgumentException.class, () ->
                new Driver("BADID00000", "Test", 1, "Light",
                        "1|A St|City|ST|Country", "01-01-2000"));
    }
}
