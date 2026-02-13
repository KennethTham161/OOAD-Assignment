package parking.strategy;

 //Plan B: Progressive Fine Scheme. Fines increase based on the number of days stayed: RM 50/100/150/200. 

public class ProgressiveFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long hours) {
        // check the tiers from longest to shortest for easier logic
        if (hours > 72) {
            return 200.0; // Day 4 and beyond 
        } else if (hours > 48) {
            return 150.0; // Day 3 
        } else if (hours > 24) {
            return 100.0; // Day 2 
        } else {
            // Day 1
            return (hours > 0) ? 50.0 : 0.0;
        }
    }

    @Override
    public String getSchemeName() {
        return "Progressive Fine (RM 50-200)";
    }
}
