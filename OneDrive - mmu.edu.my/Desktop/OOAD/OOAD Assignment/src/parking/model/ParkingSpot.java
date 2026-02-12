package parking.model;

// Represents a single parking spot in the lot
public class ParkingSpot {
    private String spotId;       // e.g. "F1-R1-S1" (Floor 1, Row 1, Spot 1)
    private int floorNumber;
    private int rowNumber;
    private int spotNumber;
    private SpotType type;
    private SpotStatus status;
    private Vehicle currentVehicle;  // null if available

    public ParkingSpot(int floorNumber, int rowNumber, int spotNumber, SpotType type) {
        this.floorNumber = floorNumber;
        this.rowNumber = rowNumber;
        this.spotNumber = spotNumber;
        this.type = type;
        this.status = SpotStatus.AVAILABLE;
        this.currentVehicle = null;
        // Build spot ID like "F1-R1-S1"
        this.spotId = "F" + floorNumber + "-R" + rowNumber + "-S" + spotNumber;
    }

    // Getters
    public String getSpotId() {
        return spotId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public SpotType getType() {
        return type;
    }

    public SpotStatus getStatus() {
        return status;
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public double getHourlyRate() {
        return type.getHourlyRate();
    }

    public boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }

    // Setters
    public void setStatus(SpotStatus status) {
        this.status = status;
    }

    public void setCurrentVehicle(Vehicle vehicle) {
        this.currentVehicle = vehicle;
    }

    // Park a vehicle in this spot
    public void occupy(Vehicle vehicle) {
        this.currentVehicle = vehicle;
        this.status = SpotStatus.OCCUPIED;
    }

    // Free up this spot
    public void release() {
        this.currentVehicle = null;
        this.status = SpotStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return spotId + " (" + type + ") - " + status;
    }
}
