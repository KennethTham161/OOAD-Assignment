package parking.service;

import java.util.ArrayList;
import java.util.List;
import parking.data.DataCenter;
import parking.model.*;
import parking.strategy.FineStrategy;


public class ParkingFacade {

    // Internal services that the Facade coordinates
    private final EntryService entryService;
    private final ExitService exitService;
    private final PaymentProcessor paymentProcessor;
    private final ReportService reportService;

    public ParkingFacade() {
        this.entryService = new EntryService();
        this.exitService = new ExitService();
        this.paymentProcessor = new PaymentProcessor();
        this.reportService = new ReportService();
    }

    // ========== VEHICLE ENTRY OPERATIONS ==========

    /**
     * Creates a vehicle and sets its VIP status.
     * Simplifies the two-step process of creating a vehicle and configuring it.
     *
     * @param plate  License plate number
     * @param type   Vehicle type string ("Car", "Motorcycle", "SUV", "Handicapped")
     * @param isVip  Whether the vehicle has a VIP reservation
     * @return The created Vehicle object, or null if invalid
     */
    public Vehicle createAndConfigureVehicle(String plate, String type, boolean isVip) {
        Vehicle vehicle = entryService.createVehicle(plate, type);
        if (vehicle != null) {
            vehicle.setVip(isVip);
        }
        return vehicle;
    }

    /**
     * Finds all available spots for a given vehicle based on its type.
     *
     * @param vehicle The vehicle looking for a spot
     * @return List of compatible available parking spots
     */
    public List<ParkingSpot> findAvailableSpots(Vehicle vehicle) {
        return entryService.findAvailableSpots(vehicle);
    }

    /**
     * Parks a vehicle in a specific spot and generates a ticket.
     *
     * @param vehicle The vehicle to park
     * @param spotId  The ID of the selected spot
     * @return The generated Ticket, or null if parking failed
     */
    public Ticket parkVehicle(Vehicle vehicle, String spotId) {
        return entryService.parkVehicle(vehicle, spotId);
    }

    // ========== VEHICLE EXIT OPERATIONS ==========

    /**
     * Calculates the total fees for a vehicle exit (parking fee + fines + unpaid fines).
     *
     * @param plate License plate number
     * @return The total amount due, or -1.0 if vehicle not found
     */
    public double calculateExitFees(String plate) {
        return exitService.processExitCalculation(plate);
    }

    /**
     * Processes the full payment and finalizes the vehicle exit.
     * This single call handles: exit finalization, payment creation,
     * vehicle removal, and fine clearing.
     *
     * @param plate   License plate number
     * @param method  Payment method (CASH or CARD)
     * @param total   Total amount due
     * @return The created Payment record
     */
    public Payment processPayment(String plate, PaymentMethod method, double total) {
        return paymentProcessor.processPayment(plate, exitService, method, total);
    }

    /**
     * Provides access to the ExitService for detailed billing data display.
     *
     * @return The ExitService containing calculated billing details
     */
    public ExitService getExitService() {
        return exitService;
    }

    // ========== DATA LOOKUP OPERATIONS ==========

    /**
     * Finds the most recent ticket for a given license plate.
     * Hides the direct DataCenter dependency from the UI layer.
     *
     * @param plate License plate number
     * @return The Ticket object, or null if not found
     */
    public Ticket findTicketByPlate(String plate) {
        return DataCenter.findTicketByPlate(plate);
    }

    /**
     * Gets the total unpaid fine amount for a given license plate.
     * Hides the direct DataCenter dependency from the UI layer.
     *
     * @param plate License plate number
     * @return Total unpaid fine amount in RM
     */
    public double getUnpaidFineTotal(String plate) {
        return DataCenter.getUnpaidFineTotal(plate);
    }

    // ========== ADMIN OPERATIONS ==========

    /**
     * Changes the active fine calculation scheme.
     *
     * @param strategy The new fine strategy to apply
     */
    public void setFineStrategy(FineStrategy strategy) {
        DataCenter.setActiveFineStrategy(strategy);
    }

    /**
     * Gets the name of the currently active fine scheme.
     *
     * @return The scheme name string
     */
    public String getActiveFineSchemeName() {
        return DataCenter.getActiveFineSchemeName();
    }

    // ========== REPORT OPERATIONS ==========

    /**
     * Gets the overall parking lot occupancy rate as a percentage.
     */
    public double getOccupancyRate() {
        return reportService.getOccupancyRate();
    }

    /**
     * Gets a detailed occupancy breakdown string (by floor and spot type).
     */
    public String getOccupancyDetails() {
        return reportService.getOccupancyDetails();
    }

    /**
     * Gets the total number of completed payments.
     */
    public int getPaymentCount() {
        return reportService.getPaymentCount();
    }

    /**
     * Gets total parking fees collected across all payments.
     */
    public double getTotalParkingFees() {
        return reportService.getTotalParkingFees();
    }

    /**
     * Gets total fines collected across all payments.
     */
    public double getTotalFinesCollected() {
        return reportService.getTotalFinesCollected();
    }

    /**
     * Gets total revenue from all payments.
     */
    public double getTotalRevenue() {
        return reportService.getTotalRevenue();
    }

    /**
     * Gets table-ready data for all currently parked vehicles.
     * Each row: [licensePlate, type, spotId, entryTime, duration, status]
     */
    public ArrayList<String[]> getCurrentVehiclesData() {
        return reportService.getCurrentVehiclesData();
    }

    /**
     * Gets the number of unique vehicles with unpaid fines.
     */
    public int getUnpaidFinesVehicleCount() {
        return reportService.getUnpaidFinesVehicleCount();
    }

    /**
     * Gets the total amount of all unpaid fines.
     */
    public double getUnpaidFinesTotal() {
        return reportService.getUnpaidFinesTotal();
    }

    /**
     * Gets the count of currently parked vehicles with violations.
     */
    public int getViolationCount() {
        return reportService.getViolationCount();
    }
}
