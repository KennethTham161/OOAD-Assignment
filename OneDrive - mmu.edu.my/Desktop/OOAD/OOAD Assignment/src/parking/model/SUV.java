package parking.model;

// SUV/Truck - can park in Regular spots only
public class SUV extends Vehicle {

    public SUV(String licensePlate) {
        super(licensePlate, VehicleType.SUV);
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        return spotType == SpotType.REGULAR;
    }
}
