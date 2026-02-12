package parking.model;

// Motorcycle - can park in Compact spots only
public class Motorcycle extends Vehicle {

    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        return spotType == SpotType.COMPACT;
    }
}
