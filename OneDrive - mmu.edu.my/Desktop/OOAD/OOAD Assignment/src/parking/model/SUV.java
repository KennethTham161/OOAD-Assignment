package parking.model;

public class SUV extends Vehicle {

    public SUV(String licensePlate) {
        super(licensePlate, VehicleType.SUV); 
    }

    @Override
    public boolean canParkIn(SpotType spotType) {
        if (spotType == SpotType.REGULAR) {
            return true;
        }

        if (spotType == SpotType.RESERVED && this.isVip()) {
            return true;
        }

        return false;
    }
}