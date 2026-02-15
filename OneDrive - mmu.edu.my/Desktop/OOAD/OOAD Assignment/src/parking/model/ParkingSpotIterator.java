package parking.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ParkingSpotIterator - Provides sequential access to all parking spots
 * across all floors without exposing the internal floor/spot structure.
 *
 * DESIGN PATTERN: Iterator
 * Allows traversal of the entire parking lot's spots in a flat sequence,
 * hiding the complexity of the multi-floor, multi-row nested structure.
 * Clients can iterate through all spots using a simple for-each loop
 * without knowing about floors or rows.
 *
 * Usage: for (ParkingSpot spot : parkingLot) { ... }
 */
public class ParkingSpotIterator implements Iterator<ParkingSpot> {

    private final ParkingLot parkingLot;
    private int floorIndex;
    private int spotIndex;

    /**
     * Creates a new iterator starting from the first spot on the first floor.
     *
     * @param parkingLot The parking lot to iterate over
     */
    public ParkingSpotIterator(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        this.floorIndex = 0;
        this.spotIndex = 0;
    }

    /**
     * Checks if there are more parking spots to iterate over.
     * Automatically advances to the next floor when the current floor's spots are exhausted.
     *
     * @return true if there are more spots, false otherwise
     */
    @Override
    public boolean hasNext() {
        // Advance through floors until we find one with remaining spots
        while (floorIndex < parkingLot.getFloors().size()) {
            Floor floor = parkingLot.getFloors().get(floorIndex);
            if (spotIndex < floor.getSpots().size()) {
                return true;
            }
            // Current floor exhausted, move to next floor
            floorIndex++;
            spotIndex = 0;
        }
        return false;
    }

    /**
     * Returns the next parking spot in the sequence.
     * Traverses spots floor-by-floor, row-by-row.
     *
     * @return The next ParkingSpot
     * @throws NoSuchElementException if no more spots are available
     */
    @Override
    public ParkingSpot next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more parking spots to iterate over.");
        }
        Floor floor = parkingLot.getFloors().get(floorIndex);
        return floor.getSpots().get(spotIndex++);
    }
}
