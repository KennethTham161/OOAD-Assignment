package parking.data;

import java.util.ArrayList;
import parking.model.*;
import parking.strategy.*;

/**
 * DataCenter - Central data storage for the parking lot system.
 * Uses static ArrayLists to store all data in memory.
 * Same pattern as the OOAD Lab Test DataCenter.
 * All methods are static - no need to create an instance.
 */
public class DataCenter {

    // ========== DATA COLLECTIONS ==========
    private static ParkingLot parkingLot = new ParkingLot("University Parking Lot");
    private static ArrayList<Vehicle> vehicles = new ArrayList<>();
    private static ArrayList<Ticket> tickets = new ArrayList<>();
    private static ArrayList<Payment> payments = new ArrayList<>();
    private static ArrayList<Fine> fines = new ArrayList<>();

    // Current fine strategy (default: Fixed)
    private static FineStrategy activeFineStrategy = new FixedFineStrategy();

    // ========== SEED DEFAULT DATA ==========
    // This block runs automatically when the class is first used
    static {
        seedParkingLot();
    }

    // Creates 5 floors with mixed spot types
    private static void seedParkingLot() {
        for (int floor = 1; floor <= 5; floor++) {
            Floor f = new Floor(floor);

            // Each floor has 2 rows, each row has 5 spots
            for (int row = 1; row <= 2; row++) {
                for (int spot = 1; spot <= 5; spot++) {
                    SpotType type;

                    // Assign spot types based on spot number
                    // Spots 1-2: Compact, Spot 3-4: Regular, Spot 5: varies by row
                    if (spot <= 2) {
                        type = SpotType.COMPACT;
                    } else if (spot <= 4) {
                        type = SpotType.REGULAR;
                    } else {
                        // Spot 5: Row 1 = Handicapped, Row 2 = Reserved
                        if (row == 1) {
                            type = SpotType.HANDICAPPED;
                        } else {
                            type = SpotType.RESERVED;
                        }
                    }

                    ParkingSpot ps = new ParkingSpot(floor, row, spot, type);
                    f.addSpot(ps);
                }
            }

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

    // Find a spot by its ID (e.g. "F1-R1-S1")
    public static ParkingSpot findSpotById(String spotId) {
        return parkingLot.findSpotById(spotId);
    }

    // Get all available spots that a vehicle can park in
    public static ArrayList<ParkingSpot> getAvailableSpotsForVehicle(Vehicle vehicle) {
        ArrayList<ParkingSpot> result = new ArrayList<>();
        for (Floor floor : parkingLot.getFloors()) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isAvailable() && vehicle.canParkIn(spot.getType())) {
                    result.add(spot);
                }
            }
        }
        return result;
    }

    // Get all available spots of a specific type
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

    // Park a vehicle in a spot
    public static void parkVehicle(Vehicle vehicle, ParkingSpot spot) {
        spot.occupy(vehicle);
        vehicle.setSpotId(spot.getSpotId());
        vehicles.add(vehicle);
    }

    // Remove a vehicle from its spot (when exiting)
    public static void removeVehicle(String licensePlate) {
        Vehicle vehicle = findVehicleByPlate(licensePlate);
        if (vehicle != null) {
            ParkingSpot spot = findSpotById(vehicle.getSpotId());
            if (spot != null) {
                spot.release();
            }
        }
    }

    // Find a currently parked vehicle by license plate
    public static Vehicle findVehicleByPlate(String licensePlate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equals(licensePlate) && v.getExitTime() == null) {
                return v;
            }
        }
        return null;
    }

    // Get all vehicles (parked + historical)
    public static ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    // Get all currently parked vehicles (exit time is null)
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

    // Find a ticket by license plate (most recent)
    public static Ticket findTicketByPlate(String licensePlate) {
        // Search from the end to find the most recent ticket
        for (int i = tickets.size() - 1; i >= 0; i--) {
            if (tickets.get(i).getLicensePlate().equals(licensePlate)) {
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
            if (f.getLicensePlate().equals(licensePlate) && !f.isPaid()) {
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
            if (f.getLicensePlate().equals(licensePlate) && !f.isPaid()) {
                f.setPaid(true);
            }
        }
    }

    // Get all unpaid fines across all vehicles
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

    // Get the name of the active fine scheme
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

    // Get total number of spots
    public static int getTotalSpots() {
        return parkingLot.getTotalSpots();
    }

    // Get total occupied spots
    public static int getTotalOccupied() {
        return parkingLot.getTotalOccupied();
    }
}
