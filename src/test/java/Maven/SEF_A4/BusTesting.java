package Maven.SEF_A4;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Unit tests for Bus class - covers conditions B1 to B5
public class BusTesting {

    // Helpers: reusable test drivers
    private Driver youngHeavyDriver() {
        // age ~30, 5 years exp, Heavy license
        return new Driver("34##xyxyXY", "Young Driver", 5,
                "Heavy", "1|Main Rd|Melbourne|VIC|Australia", "01-01-1995");
    }

    private Driver oldHeavyDriver() {
        // age ~55, 5 years exp, Heavy license
        return new Driver("45!!ababAB", "Old Driver", 5,
                "Heavy", "2|Oak St|Sydney|NSW|Australia", "01-01-1970");
    }

    private Driver lightLicenseDriver() {
        // age ~28, 6 years exp, Light license
        return new Driver("56@@cdcdCD", "Light Driver", 6,
                "Light", "3|Park Ave|Brisbane|QLD|Australia", "01-01-1997");
    }

    // ── B1: Bus ID Rules ──────────────────────────────────────────────────────

    @Test
    void b1_ValidBusID() {
        assertTrue(Bus.isValidBusID("12345678"));
    }

    @Test
    void b1_TooShort() {
        assertFalse(Bus.isValidBusID("1234567")); // 7 digits
    }

    @Test
    void b1_ContainsLetter() {
        assertFalse(Bus.isValidBusID("1234567A"));
    }

    @Test
    void b1_NullID() {
        assertFalse(Bus.isValidBusID(null));
    }

    @Test
    void b1_TooLong() {
        assertFalse(Bus.isValidBusID("123456789")); // 9 digits
    }

    // ── B2: Capacity Update Restriction ───────────────────────────────────────

    @Test
    void b2_CapacityDecrease_Allowed() {
        Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
        assertDoesNotThrow(() -> bus.update(40, 80.0, "Diesel"));
        assertEquals(40, bus.getCapacity());
    }

    @Test
    void b2_CapacityIncrease_Blocked() {
        Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
        assertThrows(IllegalArgumentException.class, () -> bus.update(51, 80.0, "Diesel"));
    }

    @Test
    void b2_SameCapacity_Allowed() {
        Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
        assertDoesNotThrow(() -> bus.update(50, 75.0, "Diesel"));
    }

    // ── B3: Driver Age Restriction ────────────────────────────────────────────

    @Test
    void b3_OldDriver_SmallBus_Allowed() {
        Bus bus = new Bus("11111111", 30, 60.0, "Diesel");
        assertTrue(bus.isDriverAllowed(oldHeavyDriver())); // age>50 but cap<50
    }

    @Test
    void b3_OldDriver_LargeBus_Blocked() {
        Bus bus = new Bus("22222222", 50, 60.0, "Diesel");
        assertFalse(bus.isDriverAllowed(oldHeavyDriver())); // age>50, cap>=50
    }

    @Test
    void b3_YoungDriver_LargeBus_Allowed() {
        Bus bus = new Bus("33333333", 60, 60.0, "Diesel");
        assertTrue(bus.isDriverAllowed(youngHeavyDriver())); // age<=50, ok
    }

    // ── B4: Electric Bus Restriction ─────────────────────────────────────────

    @Test
    void b4_EnoughExperience_ElectricBus_Allowed() {
        Bus bus = new Bus("44444444", 30, 90.0, "Electricity");
        assertTrue(bus.isDriverAllowed(youngHeavyDriver())); // 5 years, Heavy
    }

    @Test
    void b4_NotEnoughExperience_ElectricBus_Blocked() {
        Driver d = new Driver("34##xyxyXY", "New Driver", 4,
                "Heavy", "1|Main Rd|Melbourne|VIC|Australia", "01-01-1995");
        Bus bus = new Bus("55555555", 30, 90.0, "Electricity");
        assertFalse(bus.isDriverAllowed(d)); // only 4 years
    }

    @Test
    void b4_ExactlyFiveYears_ElectricBus_Allowed() {
        Bus bus = new Bus("66666666", 30, 90.0, "Electricity");
        assertTrue(bus.isDriverAllowed(youngHeavyDriver())); // exactly 5 years
    }

    // ── B5: Driver Licence Restriction ───────────────────────────────────────

    @Test
    void b5_HeavyLicense_HybridBus_Allowed() {
        Bus bus = new Bus("77777777", 30, 50.0, "Hybrid");
        assertTrue(bus.isDriverAllowed(youngHeavyDriver()));
    }

    @Test
    void b5_LightLicense_HybridBus_Blocked() {
        Bus bus = new Bus("88888888", 30, 50.0, "Hybrid");
        assertFalse(bus.isDriverAllowed(lightLicenseDriver())); // Light license not allowed
    }

    @Test
    void b5_PublicTransportLicense_ElectricBus_Allowed() {
        Driver d = new Driver("23@!abcdAB", "PT Driver", 6,
                "PublicTransport", "5|Elm St|Adelaide|SA|Australia", "01-01-1985");
        Bus bus = new Bus("99999999", 30, 90.0, "Electricity");
        assertTrue(bus.isDriverAllowed(d));
    }

    @Test
    void b5_MediumLicense_ElectricBus_Blocked() {
        Driver d = new Driver("34##xyxyXY", "Med Driver", 10,
                "Medium", "1|Oak Rd|Perth|WA|Australia", "01-01-1980");
        Bus bus = new Bus("10101010", 30, 90.0, "Electricity");
        assertFalse(bus.isDriverAllowed(d)); // Medium not allowed for electric
    }
}
