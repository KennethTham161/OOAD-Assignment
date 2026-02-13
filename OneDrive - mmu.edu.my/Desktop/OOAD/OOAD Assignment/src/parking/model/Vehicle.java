package parking.model;

import java.time.LocalDateTime;

// Abstract base class for all vehicle types
public abstract class Vehicle {
    private String licensePlate;
    private VehicleType vehicleType;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;  // null if still parked
    private String spotId;           // the spot this vehicle is parked in

    
    private boolean isVip = false;       // is it reservation/vip?
    private boolean hasViolation = false; // is it violation

    public Vehicle(String licensePlate, VehicleType vehicleType) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.spotId = null;
        // isVip 和 hasViolation 默认为 false，不需要在构造函数里特别写
    }

    // Abstract method - each vehicle type says which spot types it can park in
    public abstract boolean canParkIn(SpotType spotType);

    // Getters
    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public String getSpotId() {
        return spotId;
    }

    public boolean isVip() {
        return isVip;
    }

    public boolean hasViolation() {
        return hasViolation;
    }

    // Setters
    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }
 
    public void setVip(boolean isVip) {
        this.isVip = isVip;
    }

    public void setViolation(boolean hasViolation) {
        this.hasViolation = hasViolation;
    }

    @Override
    public String toString() {
        return vehicleType + " [" + licensePlate + "]";
    }
}