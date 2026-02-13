package parking.service;

import java.time.LocalDateTime;
import java.time.Duration;
import parking.data.DataCenter;
import parking.model.*;
import parking.strategy.FineStrategy;

// Service class for managing vehicle exit business logic and fee calculations.

 
public class ExitService {
    private PaymentProcessor processor = new PaymentProcessor();
    
    // Internal state for billing details display 
    private String vType = "-";
    private String sType = "-";
    private LocalDateTime inTime;
    private LocalDateTime tempOutTime; 
    private long mins = 0, hours = 0;
    private double rate = 0, fee = 0, fine = 0;
    private boolean isVip = false;        // VIP status 
    private boolean isViolation = false;  // Violation status 

    //Core method to process all calculations required for vehicle exit.
   
     
    public double processExitCalculation(String plate) {
        // 1. Retrieve the vehicle object from the data layer
        
        Vehicle v = DataCenter.findVehicleByPlate(plate);
        if (v == null) return -1.0;

        // 2. Load the specific status of the vehicle
        
        this.isVip = v.isVip();
        this.isViolation = v.hasViolation();

        // 3. Set entry and current exit time for duration calculation
        
        this.inTime = v.getEntryTime();
        this.tempOutTime = LocalDateTime.now(); 
        
        Duration d = Duration.between(inTime, tempOutTime);
        this.mins = d.toMinutes();
        
        // 4. Apply Ceiling Rounding logic: $hours = \lceil \frac{minutes}{60} \rceil$
        
        this.hours = (long) Math.ceil(this.mins / 60.0);
        if (this.hours <= 0) this.hours = 1;

        // 5. Identify spot and vehicle categories
     
        var spot = DataCenter.findSpotById(v.getSpotId());
        this.sType = spot.getType().toString();
        this.vType = v.getClass().getSimpleName();
        
        // 6. Calculate base hourly rate and handle exemptions
       
        this.rate = spot.getType().getHourlyRate();
        
        // Exemption: Handicapped vehicles in designated spots
       
        if (v instanceof HandicappedVehicle && sType.equals("HANDICAPPED")) {
            this.rate = 0.0;
        }
        this.fee = this.hours * this.rate;

        // 7. Calculate fines based on strategy and violations
       
        FineStrategy strategy = DataCenter.getActiveFineStrategy();
        this.fine = strategy.calculateFine(this.hours); // Standard overstay fine 
        
        // Penalty for unauthorized use of a reserved spot
        
        if (this.isViolation) {
            this.fine += 50.0; 
        }

        // 8. Return total amount due including previous unpaid fines
       
        return fee + fine + DataCenter.getUnpaidFineTotal(plate);
    }

    //Formally records the exit timestamp for the vehicle.
 
    public void finalizeExit(String plate) {
        Vehicle v = DataCenter.findVehicleByPlate(plate);
        if (v != null) {
            v.setExitTime(LocalDateTime.now());
        }
    }

    // --- Getters for UI and Receipt Data ---
   
    public String getVType() { return vType; }
    public String getSType() { return sType; }
    public LocalDateTime getIn() { return inTime; }
    public LocalDateTime getOut() { return tempOutTime; }
    public long getMins() { return mins; }
    public long getHours() { return hours; }
    public double getRate() { return rate; }
    public double getFee() { return fee; }
    public double getFine() { return fine; }
    public boolean isVip() { return isVip; }
    public boolean isViolation() { return isViolation; }
}