package parking.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import parking.data.DataCenter;
import parking.model.*;

/**
 * ReportService - Handles report generation logic for the parking lot system.
 * Provides occupancy, revenue, vehicle, fine, and violation report data.
 */
public class ReportService {

    // ========== OCCUPANCY REPORT ==========

    /**
     * Gets the overall parking lot occupancy rate as a percentage.
     */
    public double getOccupancyRate() {
        return DataCenter.getOccupancyRate();
    }

    /**
     * Builds a detailed occupancy breakdown string (by floor and by spot type).
     */
    public String getOccupancyDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("BY FLOOR:\n");
        sb.append("─────────────────────\n");

        for (Floor floor : DataCenter.getFloors()) {
            int floorSpots = floor.getTotalSpots();
            int floorOccupied = floor.getOccupiedCount();
            double floorRate = floorSpots > 0 ? (floorOccupied * 100.0 / floorSpots) : 0.0;

            sb.append(String.format("Floor %d: %2d/%2d (%.0f%%)\n",
                floor.getFloorNumber(), floorOccupied, floorSpots, floorRate));
        }

        sb.append("\nBY SPOT TYPE:\n");
        sb.append("─────────────────────\n");

        // Count by spot type
        int[] spotsByType = new int[SpotType.values().length];
        int[] occupiedByType = new int[SpotType.values().length];

        for (Floor floor : DataCenter.getFloors()) {
            for (ParkingSpot spot : floor.getSpots()) {
                SpotType type = spot.getType();
                int index = type.ordinal();
                spotsByType[index]++;
                if (!spot.isAvailable()) {
                    occupiedByType[index]++;
                }
            }
        }

        for (SpotType type : SpotType.values()) {
            int index = type.ordinal();
            int total = spotsByType[index];
            int occupied = occupiedByType[index];
            double rate = total > 0 ? (occupied * 100.0 / total) : 0.0;

            sb.append(String.format("%-11s: %2d/%2d (%.0f%%)\n",
                type.name(), occupied, total, rate));
        }

        return sb.toString();
    }

    // ========== REVENUE REPORT ==========

    /**
     * Gets the total number of payments recorded.
     */
    public int getPaymentCount() {
        return DataCenter.getPayments().size();
    }

    /**
     * Gets the total parking fees collected from all payments.
     */
    public double getTotalParkingFees() {
        double total = 0;
        for (Payment payment : DataCenter.getPayments()) {
            total += payment.getParkingFee();
        }
        return total;
    }

    /**
     * Gets the total fines collected from all payments.
     */
    public double getTotalFinesCollected() {
        double total = 0;
        for (Payment payment : DataCenter.getPayments()) {
            total += payment.getFinesPaid();
        }
        return total;
    }

    /**
     * Gets the total revenue (parking fees + fines) from all payments.
     */
    public double getTotalRevenue() {
        return DataCenter.getTotalRevenue();
    }

    // ========== CURRENT VEHICLES REPORT ==========

    /**
     * Gets table data for all currently parked vehicles.
     * Each row contains: [licensePlate, type, spotId, entryTime, duration, status]
     */
    public ArrayList<String[]> getCurrentVehiclesData() {
        ArrayList<String[]> rows = new ArrayList<>();
        var parkedVehicles = DataCenter.getAllParkedVehicles();

        if (parkedVehicles.isEmpty()) {
            rows.add(new String[]{"No vehicles currently parked", "", "", "", "", ""});
        } else {
            java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Vehicle vehicle : parkedVehicles) {
                String plate = vehicle.getLicensePlate();
                String type = vehicle.getVehicleType().name();
                String spotId = vehicle.getSpotId() != null ? vehicle.getSpotId() : "N/A";
                String entryTime = vehicle.getEntryTime() != null
                    ? vehicle.getEntryTime().format(formatter)
                    : "N/A";

                String duration = "N/A";
                String status = "OK";

                if (vehicle.getEntryTime() != null) {
                    Duration dur = Duration.between(vehicle.getEntryTime(), LocalDateTime.now());
                    long hours = dur.toHours();
                    long minutes = dur.toMinutes() % 60;
                    duration = String.format("%dh %dm", hours, minutes);

                    // Check for violations
                    if (hours > 24) {
                        status = "OVERSTAY";
                    } else if (vehicle.hasViolation()) {
                        status = "VIOLATION";
                    }
                }

                rows.add(new String[]{plate, type, spotId, entryTime, duration, status});
            }
        }

        return rows;
    }

    // ========== UNPAID FINES REPORT ==========

    /**
     * Gets the number of unique vehicles with unpaid fines.
     */
    public int getUnpaidFinesVehicleCount() {
        var allUnpaidFines = DataCenter.getAllUnpaidFines();
        Set<String> uniquePlates = new HashSet<>();
        for (Fine fine : allUnpaidFines) {
            uniquePlates.add(fine.getLicensePlate());
        }
        return uniquePlates.size();
    }

    /**
     * Gets the total amount of all unpaid fines.
     */
    public double getUnpaidFinesTotal() {
        var allUnpaidFines = DataCenter.getAllUnpaidFines();
        double total = 0;
        for (Fine fine : allUnpaidFines) {
            total += fine.getAmount();
        }
        return total;
    }

    // ========== VIOLATIONS REPORT ==========

    /**
     * Gets the count of currently parked vehicles that are violating rules.
     * Includes overstaying (>24 hours) and reserved spot violations.
     */
    public int getViolationCount() {
        var parkedVehicles = DataCenter.getAllParkedVehicles();
        int count = 0;

        for (Vehicle vehicle : parkedVehicles) {
            if (vehicle.getEntryTime() != null) {
                Duration dur = Duration.between(vehicle.getEntryTime(), LocalDateTime.now());
                long hours = dur.toHours();

                // Overstaying (>24 hours) is a violation
                if (hours > 24) {
                    count++;
                }
                // Reserved spot violation
                else if (vehicle.hasViolation()) {
                    count++;
                }
            }
        }

        return count;
    }
}
