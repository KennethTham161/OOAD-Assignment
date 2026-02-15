package parking.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents the entire parking lot with multiple floors.
 *
 * DESIGN PATTERN: Iterator
 * Implements Iterable<ParkingSpot> so that all parking spots across all floors
 * can be traversed using a simple for-each loop, without exposing the internal
 * floor/row/spot structure. The actual iteration logic is in ParkingSpotIterator.
 *
 * Usage: for (ParkingSpot spot : parkingLot) { ... }
 */
public class ParkingLot implements Iterable<ParkingSpot> {
    private String name;
    private ArrayList<Floor> floors;

    public ParkingLot(String name) {
        this.name = name;
        this.floors = new ArrayList<>();
    }

    // Add a floor to the parking lot
    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    // Get all floors
    public ArrayList<Floor> getFloors() {
        return floors;
    }

    // Get a specific floor by number
    public Floor getFloor(int floorNumber) {
        for (Floor floor : floors) {
            if (floor.getFloorNumber() == floorNumber) {
                return floor;
            }
        }
        return null;
    }

    // Find a spot by its ID across all floors
    public ParkingSpot findSpotById(String spotId) {
        for (Floor floor : floors) {
            ParkingSpot spot = floor.findSpotById(spotId);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    // Get all available spots across all floors
    public ArrayList<ParkingSpot> getAllAvailableSpots() {
        ArrayList<ParkingSpot> available = new ArrayList<>();
        for (Floor floor : floors) {
            available.addAll(floor.getAvailableSpots());
        }
        return available;
    }

    // Get total number of spots in the entire lot
    public int getTotalSpots() {
        int total = 0;
        for (Floor floor : floors) {
            total += floor.getTotalSpots();
        }
        return total;
    }

    // Get total occupied spots in the entire lot
    public int getTotalOccupied() {
        int total = 0;
        for (Floor floor : floors) {
            total += floor.getOccupiedCount();
        }
        return total;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns an Iterator that traverses all parking spots across all floors.
     * This is the Iterator Pattern entry point - enables for-each loops over all spots.
     */
    @Override
    public Iterator<ParkingSpot> iterator() {
        return new ParkingSpotIterator(this);
    }

    @Override
    public String toString() {
        return name + " (" + getTotalOccupied() + "/" + getTotalSpots() + " occupied)";
    }
}
