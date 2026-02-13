package parking.model;

// Car - can park in Compact or Regular spots
public class Car extends Vehicle {

    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        if (spotType == SpotType.COMPACT || spotType == SpotType.REGULAR) {
            return true;
        }
        
        if (spotType == SpotType.RESERVED && this.isVip()) {
            return true; 
        }

        return false;
    }
}