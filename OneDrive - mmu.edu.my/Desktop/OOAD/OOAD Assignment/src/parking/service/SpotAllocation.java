package parking.service;

import parking.model.*;
import parking.data.DataCenter;
import java.util.ArrayList;

public class SpotAllocation {

    public ArrayList<ParkingSpot> findSuitableSpots(Vehicle vehicle) {

        ArrayList<ParkingSpot> result = new ArrayList<>();

        for (Floor floor : DataCenter.getFloors()) {
            for (ParkingSpot spot : floor.getSpots()) {

                if (spot.getStatus() == SpotStatus.AVAILABLE
                        && vehicle.canParkIn(spot.getType())) {

                    result.add(spot);
                }
            }
        }

        return result;
    }
}
