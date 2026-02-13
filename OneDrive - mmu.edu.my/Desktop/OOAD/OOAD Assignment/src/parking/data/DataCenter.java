package parking.data;

import java.util.ArrayList;
import java.util.List;
import parking.model.*;
import parking.strategy.*;

/**
 * DataCenter - Central data storage for the parking lot system. Uses static
 * ArrayLists to store all data in memory. All methods are static - no need to
 * create an instance.
 */
public class DataCenter {

    // ========== DATA COLLECTIONS ==========
    private static ParkingLot parkingLot = new ParkingLot("University Parking Lot");
    private static ArrayList<Vehicle> vehicles = new ArrayList<>();
    private static ArrayList<Ticket> tickets = new ArrayList<>();
    private static ArrayList<Payment> payments = new ArrayList<>();
    private static ArrayList<Fine> fines = new ArrayList<>();

    // Current fine strategy (default: Fixed Fine Scheme)
    private static FineStrategy activeFineStrategy = new FixedFineStrategy();

    // ========== INITIALIZATION BLOCK ==========
    // This block runs automatically when the program starts.
    static {
        System.out.println("DataCenter: Initializing system data...");
        seedParkingLot();
        System.out.println("DataCenter: Initialization complete. Total spots: " + getTotalSpots());
    }

    // Creates 5 floors with mixed spot types (Compact, Regular, Handicapped, Reserved)
    private static void seedParkingLot() {
        for (int floor = 1; floor <= 5; floor++) {
            Floor f = new Floor(floor);

            // Each floor has 2 rows, each row has 5 spots
            for (int row = 1; row <= 2; row++) {
                for (int spot = 1; spot <= 5; spot++) {
                    SpotType type;

                    // Assign spot types based on spot number logic:
                    // Spots 1-2: Compact
                    // Spots 3-4: Regular
                    // Spot 5: Row 1 = Handicapped, Row 2 = Reserved
                    if (spot <= 2) {
                        type = SpotType.COMPACT;
                    } else if (spot <= 4) {
                        type = SpotType.REGULAR;
                    } else {
                        if (row == 1) {
                            type = SpotType.HANDICAPPED;
                        } else {
                            type = SpotType.RESERVED;
                        }
                    }

                    // Create the spot. 
                    // Assuming your ParkingSpot constructor is (int floor, int row, int spot, SpotType type)
                    // If your constructor takes (String id, SpotType type), you need to format the ID here.
                    ParkingSpot ps = new ParkingSpot(floor, row, spot, type);

                    // Add spot to the floor
                    f.addSpot(ps);
                }
            }

            // Add floor to the parking lot
            parkingLot.addFloor(f);
        }
    }

    // ========== PARKING LOT METHODS ==========
    public static ParkingLot getParkingLot() {
        return parkingLot;
    }

    public static ArrayList<Floor> getFloors() {
        return parkingLot.getFloors();
    }

    public static Floor getFloor(int floorNumber) {
        return parkingLot.getFloor(floorNumber);
    }

    // Find a spot by its ID (e.g., "F1-R1-S1")
    public static ParkingSpot findSpotById(String spotId) {
        return parkingLot.findSpotById(spotId);
    }

    // Get all available spots that a specific vehicle can park in
    public static ArrayList<ParkingSpot> getAvailableSpotsForVehicle(Vehicle vehicle) {
        ArrayList<ParkingSpot> result = new ArrayList<>();

        // Loop through all floors and spots
        for (Floor floor : parkingLot.getFloors()) {
            for (ParkingSpot spot : floor.getSpots()) {
                // Check 1: Is the spot empty?
                // Check 2: Can this specific vehicle type park here? (Checks logic inside Vehicle class)
                if (spot.isAvailable() && vehicle.canParkIn(spot.getType())) {
                    result.add(spot);
                }
            }
        }
        return result;
    }

    // Get all available spots of a specific type (helper method)
    public static ArrayList<ParkingSpot> getAvailableSpotsByType(SpotType type) {
        ArrayList<ParkingSpot> result = new ArrayList<>();
        for (Floor floor : parkingLot.getFloors()) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isAvailable() && spot.getType() == type) {
                    result.add(spot);
                }
            }
        }
        return result;
    }

    // ========== VEHICLE METHODS ==========
    // Park a vehicle in a specific spot
    public static void parkVehicle(Vehicle vehicle, ParkingSpot spot) {
        if (spot.isAvailable()) {
            spot.occupy(vehicle);
            vehicle.setSpotId(spot.getSpotId());
            vehicles.add(vehicle); // Add to history list
            System.out.println("DataCenter: Vehicle " + vehicle.getLicensePlate() + " parked at " + spot.getSpotId());
        }
    }

    // Remove a vehicle from its spot (when exiting)
    public static void removeVehicle(String plate) {
        Vehicle v = findVehicleByPlate(plate);
        if (v == null) {
            for (Vehicle temp : vehicles) {
                if (temp.getLicensePlate().equalsIgnoreCase(plate)) {
                    v = temp;
                    break;
                }
            }
        }

        if (v != null) {
            ParkingSpot spot = findSpotById(v.getSpotId());
            if (spot != null) {
                spot.release(); 
            }
            vehicles.remove(v); 
            System.out.println("DataCenter: Vehicle " + plate + " removed and spot released.");
        } else {
            System.out.println("DataCenter: Error - Could not find vehicle " + plate + " to remove.");
        }
    }

    // Find a currently parked vehicle by license plate
    public static Vehicle findVehicleByPlate(String licensePlate) {
        for (Vehicle v : vehicles) {
            // Match plate AND ensure vehicle hasn't exited yet (exitTime is null)
            if (v.getLicensePlate().equalsIgnoreCase(licensePlate) && v.getExitTime() == null) {
                return v;
            }
        }
        return null;
    }

    // Get all vehicles (both currently parked and historical)
    public static ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    // Get only currently parked vehicles
    public static ArrayList<Vehicle> getAllParkedVehicles() {
        ArrayList<Vehicle> parked = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (v.getExitTime() == null) {
                parked.add(v);
            }
        }
        return parked;
    }

    // ========== TICKET METHODS ==========
    public static void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public static ArrayList<Ticket> getTickets() {
        return tickets;
    }

    // Find the most recent ticket for a license plate
    public static Ticket findTicketByPlate(String licensePlate) {
        // Search backwards to find the latest entry
        for (int i = tickets.size() - 1; i >= 0; i--) {
            if (tickets.get(i).getLicensePlate().equalsIgnoreCase(licensePlate)) {
                return tickets.get(i);
            }
        }
        return null;
    }

    // ========== PAYMENT METHODS ==========
    public static void addPayment(Payment payment) {
        payments.add(payment);
    }

    public static ArrayList<Payment> getPayments() {
        return payments;
    }

    // Get total revenue from all payments
    public static double getTotalRevenue() {
        double total = 0;
        for (Payment p : payments) {
            total += p.getTotalAmount();
        }
        return total;
    }

    // ========== FINE METHODS ==========
    public static void addFine(Fine fine) {
        fines.add(fine);
    }

    public static ArrayList<Fine> getFines() {
        return fines;
    }

    // Get unpaid fines for a specific license plate
    public static ArrayList<Fine> getUnpaidFines(String licensePlate) {
        ArrayList<Fine> unpaid = new ArrayList<>();
        for (Fine f : fines) {
            if (f.getLicensePlate().equalsIgnoreCase(licensePlate) && !f.isPaid()) {
                unpaid.add(f);
            }
        }
        return unpaid;
    }

    // Get total unpaid fine amount for a license plate
    public static double getUnpaidFineTotal(String licensePlate) {
        double total = 0;
        for (Fine f : getUnpaidFines(licensePlate)) {
            total += f.getAmount();
        }
        return total;
    }

    // Mark all fines for a license plate as paid
    public static void markFinesPaid(String licensePlate) {
        for (Fine f : fines) {
            if (f.getLicensePlate().equalsIgnoreCase(licensePlate) && !f.isPaid()) {
                f.setPaid(true);
            }
        }
    }

    // Get all unpaid fines across all vehicles (for Admin Report)
    public static ArrayList<Fine> getAllUnpaidFines() {
        ArrayList<Fine> unpaid = new ArrayList<>();
        for (Fine f : fines) {
            if (!f.isPaid()) {
                unpaid.add(f);
            }
        }
        return unpaid;
    }

    // ========== FINE STRATEGY METHODS ==========
    public static FineStrategy getActiveFineStrategy() {
        return activeFineStrategy;
    }

    public static void setActiveFineStrategy(FineStrategy strategy) {
        activeFineStrategy = strategy;
    }

    public static String getActiveFineSchemeName() {
        return activeFineStrategy.getSchemeName();
    }

    // ========== REPORTING HELPER METHODS ==========
    // Get occupancy rate as a percentage
    public static double getOccupancyRate() {
        int total = parkingLot.getTotalSpots();
        if (total == 0) {
            return 0.0;
        }
        int occupied = parkingLot.getTotalOccupied();
        return (occupied * 100.0) / total;
    }

    public static int getTotalSpots() {
        return parkingLot.getTotalSpots();
    }

    public static int getTotalOccupied() {
        return parkingLot.getTotalOccupied();
    }
}
