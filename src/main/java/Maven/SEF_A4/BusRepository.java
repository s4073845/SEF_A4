package Maven.SEF_A4;

import java.io.*;
import java.util.*;

// Stores and retrieves Bus objects from a human-readable TXT file.
// Each line = one bus in semicolon-delimited format.
public class BusRepository {

    private final String filePath;

    public BusRepository(String filePath) {
        this.filePath = filePath;
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create bus file: " + filePath, e);
        }
    }

    // Add a new bus - B1: rejects duplicate busID
    public void add(Bus bus) {
        if (retrieve(bus.getBusID()) != null)
            throw new IllegalArgumentException("B1: Duplicate busID: " + bus.getBusID());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(bus.toFileString());
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write bus.", e);
        }
    }

    // Retrieve bus by ID, returns null if not found
    public Bus retrieve(String busID) {
        for (Bus b : retrieveAll()) {
            if (b.getBusID().equals(busID)) return b;
        }
        return null;
    }

    // Read all buses from the file
    public List<Bus> retrieveAll() {
        List<Bus> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) list.add(Bus.fromFileString(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read bus file.", e);
        }
        return list;
    }

    // Update mutable fields of an existing bus (B2 enforced inside Bus.update)
    public void update(String busID, int newCapacity, double newFuelLevel, String newFuelType) {
        List<Bus> buses = retrieveAll();
        boolean found = false;
        for (Bus b : buses) {
            if (b.getBusID().equals(busID)) {
                b.update(newCapacity, newFuelLevel, newFuelType);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Bus not found: " + busID);
        writeAll(buses);
    }

    // Return total number of stored buses
    public int count() {
        return retrieveAll().size();
    }

    // Overwrite the file with the given list
    private void writeAll(List<Bus> buses) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Bus b : buses) {
                bw.write(b.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write bus file.", e);
        }
    }
}
