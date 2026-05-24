package Maven.SEF_A4;

import java.io.*;
import java.nio.file.*;
import java.util.*;

// Stores and retrieves Driver objects from a human-readable TXT file.
// Each line = one driver in semicolon-delimited format.
public class DriverRepository {

    private final String filePath;

    public DriverRepository(String filePath) {
        this.filePath = filePath;
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create driver file: " + filePath, e);
        }
    }

    // Add a new driver - D1: rejects duplicate driverID
    public void add(Driver driver) {
        if (retrieve(driver.getDriverID()) != null)
            throw new IllegalArgumentException("D1: Duplicate driverID: " + driver.getDriverID());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(driver.toFileString());
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write driver.", e);
        }
    }

    // Retrieve driver by ID, returns null if not found
    public Driver retrieve(String driverID) {
        for (Driver d : retrieveAll()) {
            if (d.getDriverID().equals(driverID)) return d;
        }
        return null;
    }

    // Read all drivers from the file
    public List<Driver> retrieveAll() {
        List<Driver> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) list.add(Driver.fromFileString(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read driver file.", e);
        }
        return list;
    }

    // Update mutable fields of an existing driver (D4, D5 enforced inside Driver.update)
    public void update(String driverID, int newExp, String newLicense, String newAddress, String newBirthdate) {
        List<Driver> drivers = retrieveAll();
        boolean found = false;
        for (Driver d : drivers) {
            if (d.getDriverID().equals(driverID)) {
                d.update(newExp, newLicense, newAddress, newBirthdate);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Driver not found: " + driverID);
        writeAll(drivers);
    }

    // Return total number of stored drivers
    public int count() {
        return retrieveAll().size();
    }

    // Overwrite the file with the given list
    private void writeAll(List<Driver> drivers) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Driver d : drivers) {
                bw.write(d.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write driver file.", e);
        }
    }
}
