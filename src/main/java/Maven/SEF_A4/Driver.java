package Maven.SEF_A4;

// Driver class - enforces conditions D1 to D5
public class Driver {

    private String driverID;
    private String name;
    private int experienceYears;
    private String licenseType; // Light, Medium, Heavy, PublicTransport
    private String address;     // Format: StreetNum|StreetName|City|State|Country
    private String birthdate;   // Format: DD-MM-YYYY

    public Driver(String driverID, String name, int experienceYears,
                  String licenseType, String address, String birthdate) {

        if (!isValidDriverID(driverID))
            throw new IllegalArgumentException("Invalid driverID: " + driverID);
        if (!isValidAddress(address))
            throw new IllegalArgumentException("Invalid address: " + address);
        if (!isValidBirthdate(birthdate))
            throw new IllegalArgumentException("Invalid birthdate: " + birthdate);
        if (!isValidLicenseType(licenseType))
            throw new IllegalArgumentException("Invalid licenseType: " + licenseType);

        this.driverID = driverID;
        this.name = name;
        this.experienceYears = experienceYears;
        this.licenseType = licenseType;
        this.address = address;
        this.birthdate = birthdate;
    }

    // Getters
    public String getDriverID()        { return driverID; }
    public String getName()            { return name; }
    public int    getExperienceYears() { return experienceYears; }
    public String getLicenseType()     { return licenseType; }
    public String getAddress()         { return address; }
    public String getBirthdate()       { return birthdate; }

    // Update mutable fields - D4: license locked if exp > 10, D5: ID and name are read-only
    public void update(int newExp, String newLicense, String newAddress, String newBirthdate) {
        if (this.experienceYears > 10 && !this.licenseType.equals(newLicense))
            throw new IllegalArgumentException("D4: Cannot change license for driver with >10 years experience.");
        if (!isValidAddress(newAddress))
            throw new IllegalArgumentException("Invalid address: " + newAddress);
        if (!isValidBirthdate(newBirthdate))
            throw new IllegalArgumentException("Invalid birthdate: " + newBirthdate);
        if (!isValidLicenseType(newLicense))
            throw new IllegalArgumentException("Invalid licenseType: " + newLicense);

        this.experienceYears = newExp;
        this.licenseType = newLicense;
        this.address = newAddress;
        this.birthdate = newBirthdate;
    }

    // D1: exactly 10 chars, first 2 are digits 2-9, chars 2-7 have >=2 special chars, last 2 are uppercase letters
    public static boolean isValidDriverID(String id) {
        if (id == null || id.length() != 10) return false;

        char c0 = id.charAt(0), c1 = id.charAt(1);
        if (!Character.isDigit(c0) || c0 < '2') return false;
        if (!Character.isDigit(c1) || c1 < '2') return false;

        if (!Character.isUpperCase(id.charAt(8)) || !Character.isUpperCase(id.charAt(9))) return false;

        int specials = 0;
        for (int i = 2; i <= 7; i++) {
            if (!Character.isLetterOrDigit(id.charAt(i))) specials++;
        }
        return specials >= 2;
    }

    // D2: exactly 5 pipe-separated non-empty parts
    public static boolean isValidAddress(String address) {
        if (address == null) return false;
        String[] parts = address.split("\\|");
        if (parts.length != 5) return false;
        for (String p : parts) {
            if (p.trim().isEmpty()) return false;
        }
        return true;
    }

    // D3: DD-MM-YYYY with valid day/month/year
    public static boolean isValidBirthdate(String birthdate) {
        if (birthdate == null || !birthdate.matches("\\d{2}-\\d{2}-\\d{4}")) return false;
        int day   = Integer.parseInt(birthdate.substring(0, 2));
        int month = Integer.parseInt(birthdate.substring(3, 5));
        int year  = Integer.parseInt(birthdate.substring(6));
        return day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 1900;
    }

    public static boolean isValidLicenseType(String license) {
        return license != null && (license.equals("Light") || license.equals("Medium")
                || license.equals("Heavy") || license.equals("PublicTransport"));
    }

    // File format: fields separated by semicolons (address uses | internally)
    public String toFileString() {
        return driverID + ";" + name + ";" + experienceYears + ";" + licenseType + ";" + address + ";" + birthdate;
    }

    public static Driver fromFileString(String line) {
        String[] p = line.split(";", 6);
        return new Driver(p[0], p[1], Integer.parseInt(p[2]), p[3], p[4], p[5]);
    }

    @Override
    public String toString() {
        return "Driver[" + driverID + ", " + name + ", exp=" + experienceYears
                + ", " + licenseType + ", " + address + ", " + birthdate + "]";
    }
}
