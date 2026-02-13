package parking.service;

import parking.data.DataCenter;
import parking.model.*; // Importing all model classes (Car, SUV, etc.)
import java.util.List;
import java.util.ArrayList;
import parking.model.SpotStatus;

/**
 * EntryService handles the core business logic for the parking entrance.
 * It acts as a bridge between the UI (EntryPanel) and the data layer (DataCenter).
 */
public class EntryService {

    /**
     * 1. Create a Vehicle instance.
     * Maps the UI selection string to specific subclass implementations.
     * * @param plate The raw license plate string from user input.
     * @param typeStr The vehicle category selected in the UI dropdown.
     * @return A concrete Vehicle object (Car, Motorcycle, etc.), or null if invalid.
     */
    public Vehicle createVehicle(String plate, String typeStr) {
        if (plate == null || plate.trim().isEmpty()) {
            return null;
        }

        // Normalize plate to uppercase to ensure consistency (e.g., "abc-123" vs "ABC-123")
        String cleanPlate = plate.trim().toUpperCase();

        // Instantiate the appropriate subclass based on the provided type string
        switch (typeStr) {
            case "Car":
                return new Car(cleanPlate);
            case "Motorcycle":
                return new Motorcycle(cleanPlate);
            case "SUV":
                return new SUV(cleanPlate);
            case "Handicapped":
                return new HandicappedVehicle(cleanPlate);
            default:
                // Log or handle unknown vehicle types if necessary
                return null;
        }
    }

    /**
     * 2. Find available parking spots.
     * Delegates the search to the DataCenter based on vehicle compatibility.
     * * @param v The vehicle looking for a spot.
     * @return A list of available ParkingSpot objects.
     */
    public List<ParkingSpot> findAvailableSpots(Vehicle v) {
        try {
            // DataCenter provides a static utility to filter compatible/vacant spots
            return DataCenter.getAvailableSpotsForVehicle(v);
        } catch (Exception e) {
            // Fail-safe: log error and return an empty list to prevent UI from crashing
            System.err.println("Error accessing DataCenter: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 3. Finalize the parking process and generate a Ticket.
     * Updates the spot status in the database and creates a record for the session.
     * * @param v The vehicle being parked.
     * @param spotId The ID of the spot chosen by the user.
     * @return The generated Ticket object, or null if the process fails.
     */
    public Ticket parkVehicle(Vehicle v, String spotId) {
        // Retrieve the spot object from the data center
        ParkingSpot spot = DataCenter.findSpotById(spotId);
        
        if (spot != null) {
            // Update the DataCenter/Database to reflect that the spot is now occupied
            DataCenter.parkVehicle(v, spot);
            
            // Extract necessary data for Ticket construction
            String plate = v.getLicensePlate();
            String assignedSpotId = spot.getSpotId();
            java.time.LocalDateTime entryTime = v.getEntryTime();
            
            /**
             * Ticket Constructor Requirement: 
             * new Ticket(String plate, String spotId, LocalDateTime time)
             */
            Ticket ticket = new Ticket(plate, assignedSpotId, entryTime); 
            
            // Persist the ticket record in the DataCenter
            DataCenter.addTicket(ticket);
            
            return ticket;
        }
        
        // Return null if the spot was not found or is no longer available
        return null;
    }
}