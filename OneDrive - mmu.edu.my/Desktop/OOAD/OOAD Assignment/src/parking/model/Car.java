package parking.model;

// Car - can park in Compact or Regular spots
public class Car extends Vehicle {

    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        return spotType == SpotType.COMPACT || spotType == SpotType.REGULAR;
    }
}
