package parking.model;

// Handicapped Vehicle - can park in any spot
// Gets discounted price of RM 2/hour only in handicapped spots
public class HandicappedVehicle extends Vehicle {

    public HandicappedVehicle(String licensePlate) {
        super(licensePlate, VehicleType.HANDICAPPED);
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        // Handicapped vehicles can park in any spot type
        return true;
    }
}
