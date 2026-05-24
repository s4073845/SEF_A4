package Maven.SEF_A4;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Bus class - enforces conditions B1 to B5
public class Bus {

    private String busID;
    private int capacity;
    private double fuelLevel;
    private String fuelType; // Diesel, Hybrid, Electricity

    public Bus(String busID, int capacity, double fuelLevel, String fuelType) {
        if (!isValidBusID(busID))
            throw new IllegalArgumentException("Invalid busID: " + busID);
        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity must be > 0.");
        if (fuelLevel < 0 || fuelLevel > 100)
            throw new IllegalArgumentException("Fuel level must be 0-100.");
        if (!isValidFuelType(fuelType))
            throw new IllegalArgumentException("Invalid fuelType: " + fuelType);

        this.busID = busID;
        this.capacity = capacity;
        this.fuelLevel = fuelLevel;
        this.fuelType = fuelType;
    }

    // Getters
    public String getBusID()     { return busID; }
    public int    getCapacity()  { return capacity; }
    public double getFuelLevel() { return fuelLevel; }
    public String getFuelType()  { return fuelType; }

    // B2: capacity can only decrease (or stay same) on update
    public void update(int newCapacity, double newFuelLevel, String newFuelType) {
        if (newCapacity > this.capacity)
            throw new IllegalArgumentException("B2: Capacity cannot increase.");
        if (newCapacity <= 0)
            throw new IllegalArgumentException("Capacity must be > 0.");
        if (newFuelLevel < 0 || newFuelLevel > 100)
            throw new IllegalArgumentException("Fuel level must be 0-100.");
        if (!isValidFuelType(newFuelType))
            throw new IllegalArgumentException("Invalid fuelType: " + newFuelType);

        this.capacity = newCapacity;
        this.fuelLevel = newFuelLevel;
        this.fuelType = newFuelType;
    }

    // Checks B3, B4, B5 - returns true if the driver is allowed to drive this bus
    public boolean isDriverAllowed(Driver driver) {
        int age = calculateAge(driver.getBirthdate());
        String license = driver.getLicenseType();
        int exp = driver.getExperienceYears();

        // B3: drivers over 50 cannot drive buses with capacity >= 50
        if (age > 50 && this.capacity >= 50) return false;

        // B4: electric buses require at least 5 years experience
        if ("Electricity".equals(fuelType) && exp < 5) return false;

        // B5: electric and hybrid buses require Heavy or PublicTransport license
        if (("Electricity".equals(fuelType) || "Hybrid".equals(fuelType))
                && !license.equals("Heavy") && !license.equals("PublicTransport")) return false;

        return true;
    }

    // Helper: calculate age from DD-MM-YYYY birthdate
    static int calculateAge(String birthdate) {
        int day   = Integer.parseInt(birthdate.substring(0, 2));
        int month = Integer.parseInt(birthdate.substring(3, 5));
        int year  = Integer.parseInt(birthdate.substring(6));
        return (int) ChronoUnit.YEARS.between(LocalDate.of(year, month, day), LocalDate.now());
    }

    // B1: exactly 8 digits
    public static boolean isValidBusID(String id) {
        return id != null && id.matches("\\d{8}");
    }

    public static boolean isValidFuelType(String fuelType) {
        return fuelType != null && (fuelType.equals("Diesel")
                || fuelType.equals("Hybrid") || fuelType.equals("Electricity"));
    }

    // File format: busID;capacity;fuelLevel;fuelType
    public String toFileString() {
        return busID + ";" + capacity + ";" + fuelLevel + ";" + fuelType;
    }

    public static Bus fromFileString(String line) {
        String[] p = line.split(";", 4);
        return new Bus(p[0], Integer.parseInt(p[1]), Double.parseDouble(p[2]), p[3]);
    }

    @Override
    public String toString() {
        return "Bus[" + busID + ", cap=" + capacity + ", fuel=" + fuelLevel + "%, " + fuelType + "]";
    }
}
