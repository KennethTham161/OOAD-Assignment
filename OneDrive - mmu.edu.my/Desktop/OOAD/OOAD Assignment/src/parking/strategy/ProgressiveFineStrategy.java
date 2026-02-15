package parking.strategy;

 //Plan B: Progressive Fine Scheme. Fines increase based on the number of days stayed: RM 50/100/150/200. 

public class ProgressiveFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long hours) {
        // No fine if parked 24 hours or less (no overstay)
        if (hours <= 24) {
            return 0.0;
        }

        // Cumulative progressive fines based on total parking duration
        double fine = 50.0;                     // First 24 hours of overstay: RM 50
        if (hours > 48) fine += 100.0;          // Hours 24-48: Additional RM 100  (total: RM 150)
        if (hours > 72) fine += 150.0;          // Hours 48-72: Additional RM 150  (total: RM 300)
        if (hours > 96) fine += 200.0;          // Above 72 hours of overstay: Additional RM 200 (total: RM 500)

        return fine;
    }

    @Override
    public String getSchemeName() {
        return "Progressive Fine (RM 50-200)";
    }
}
