package parking.model;

import java.time.LocalDateTime;

// Abstract base class for all vehicle types
public abstract class Vehicle {
    private String licensePlate;
    private VehicleType vehicleType;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;  // null if still parked
    private String spotId;           // the spot this vehicle is parked in

    public Vehicle(String licensePlate, VehicleType vehicleType) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.spotId = null;
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

    @Override
    public String toString() {
        return vehicleType + " [" + licensePlate + "]";
    }
}
