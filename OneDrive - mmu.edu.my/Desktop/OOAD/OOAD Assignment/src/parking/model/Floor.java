package parking.model;

import java.util.ArrayList;

// Represents one floor of the parking lot
public class Floor {
    private int floorNumber;
    private ArrayList<ParkingSpot> spots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
    }

    // Add a spot to this floor
    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    // Get all spots on this floor
    public ArrayList<ParkingSpot> getSpots() {
        return spots;
    }

    // Get only available spots on this floor
    public ArrayList<ParkingSpot> getAvailableSpots() {
        ArrayList<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable()) {
                available.add(spot);
            }
        }
        return available;
    }

    // Get available spots of a specific type on this floor
    public ArrayList<ParkingSpot> getAvailableSpotsByType(SpotType type) {
        ArrayList<ParkingSpot> result = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.getType() == type) {
                result.add(spot);
            }
        }
        return result;
    }

    // Find a spot by its ID on this floor
    public ParkingSpot findSpotById(String spotId) {
        for (ParkingSpot spot : spots) {
            if (spot.getSpotId().equals(spotId)) {
                return spot;
            }
        }
        return null;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    // Count total spots on this floor
    public int getTotalSpots() {
        return spots.size();
    }

    // Count occupied spots on this floor
    public int getOccupiedCount() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (!spot.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return "Floor " + floorNumber + " (" + getOccupiedCount() + "/" + getTotalSpots() + " occupied)";
    }
}
