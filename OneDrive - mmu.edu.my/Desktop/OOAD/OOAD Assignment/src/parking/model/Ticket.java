package parking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Represents a parking ticket given when a vehicle enters
// Format: T-PLATE-TIMESTAMP
public class Ticket {
    private String ticketId;
    private String licensePlate;
    private String spotId;
    private LocalDateTime entryTime;

    public Ticket(String licensePlate, String spotId, LocalDateTime entryTime) {
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.entryTime = entryTime;
        // Build ticket ID like T-ABC1234-20260212143000
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        this.ticketId = "T-" + licensePlate + "-" + entryTime.format(formatter);
    }

    // Getters
    public String getTicketId() {
        return ticketId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getSpotId() {
        return spotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Ticket: " + ticketId + "\n"
             + "License Plate: " + licensePlate + "\n"
             + "Spot: " + spotId + "\n"
             + "Entry Time: " + entryTime.format(formatter);
    }
}
