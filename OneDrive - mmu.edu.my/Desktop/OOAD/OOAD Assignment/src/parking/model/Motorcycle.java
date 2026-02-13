package parking.model;

public class Motorcycle extends Vehicle {

    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        
        if (spotType == SpotType.COMPACT) {
            return true;
        }
        
        if (spotType == SpotType.RESERVED && this.isVip()) {
            return true;
        }

        return false;
    }
}