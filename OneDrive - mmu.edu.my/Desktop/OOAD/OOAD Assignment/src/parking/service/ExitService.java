package parking.service;

import java.time.LocalDateTime;
import java.time.Duration;
import parking.data.DataCenter;
import parking.model.*;
import parking.strategy.FineStrategy;

//Service class for handling vehicle exit logic.


public class ExitService {
    // Process payments via helper
    private PaymentProcessor processor = new PaymentProcessor();
    
    // Variables to store bill details for UI display
    private String vType = "-";        // Vehicle Type 
    private String sType = "-";        // Spot Type 
    private LocalDateTime inTime;      // Entry Timestamp 
    private LocalDateTime tempOutTime; // Simulated Exit Time for fee preview 
    private long mins = 0;             // Total minutes parked 
    private long hours = 0;            // Total hours after ceiling rounding 
    private double rate = 0;           // Hourly rate based on spot 
    private double fee = 0;            // Calculated parking fee 
    private double fine = 0;           // Calculated fines 

    // Primary method to calculate all costs when searching for a plate.
     
     
    public double processExitCalculation(String plate) {
        // 1. Retrieve vehicle from data center
    
        Vehicle v = DataCenter.findVehicleByPlate(plate);
        if (v == null) return -1.0; // Return error code if not found 

        // 2. Set calculation timestamps
     
        this.inTime = v.getEntryTime();
        this.tempOutTime = LocalDateTime.now(); // Current time as temporary exit 
        
        // 3. Calculate duration between entry and exit

        Duration d = Duration.between(inTime, tempOutTime);
        this.mins = d.toMinutes();
        
        // 4. Implement Ceiling Rounding: Any fraction of an hour counts as a full hour
       
        // Formula: $$hours = \lceil \frac{minutes}{60} \rceil$$
        this.hours = (long) Math.ceil(this.mins / 60.0);
        if (this.hours <= 0) this.hours = 1; // Minimum charge is 1 hour 

        // 5. Get Spot Details
        
        var spot = DataCenter.findSpotById(v.getSpotId());
        this.sType = spot.getType().toString();
        
        // Use Reflection to get the class name as vehicle type 
        
        this.vType = v.getClass().getSimpleName().replace("Vehicle", "");
        
        // 6. Fetch Hourly Rate based on Spot Type
      
        this.rate = spot.getType().getHourlyRate();
        
        // 7. OKU Rule: Free only if Handicapped Vehicle is in a Handicapped Spot
       
        if (v instanceof HandicappedVehicle && sType.equals("HANDICAPPED")) {
            this.rate = 0.0; // Set rate to zero 
        }
        
        // Calculate base parking fee
        
        this.fee = this.hours * this.rate;

        // 8. Fine Logic using Strategy Pattern
       
        FineStrategy strategy = DataCenter.getActiveFineStrategy();
        this.fine = strategy.calculateFine(this.hours); // Overstay fine 
        
        // Extra Penalty: Misuse of RESERVED spot
        
        if (sType.equals("RESERVED")) {
            this.fine += 50.0; // Fixed penalty of RM 50 
        }

        // 9. Grand Total: Fee + Current Fine + Old Unpaid Fines
      
        return fee + fine + DataCenter.getUnpaidFineTotal(plate);
    }

    //Confirms the exit and sets the final timestamp.
    
    
    public void finalizeExit(String plate) {
        Vehicle v = DataCenter.findVehicleByPlate(plate);
        if (v != null) {
            // Permanently record the exit time 
            v.setExitTime(LocalDateTime.now());
        }
    }

    // --- Getters for ExitPanel to update UI 
    public String getVType() { return vType; }
    public String getSType() { return sType; }
    public LocalDateTime getIn() { return inTime; }
    public LocalDateTime getOut() { return tempOutTime; }
    public long getMins() { return mins; }
    public long getHours() { return hours; }
    public double getRate() { return rate; }
    public double getFee() { return fee; }
    public double getFine() { return fine; }
}