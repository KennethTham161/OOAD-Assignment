package parking.strategy;

//Plan C: Hourly Fine Scheme. RM 20 per hour for every hour over the 24-hour
 
public class HourlyFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long overstayHours) {
        // charge if the total hours exceed 24
        if (overstayHours > 24) {
            // Formula: (Total Hours - 24) * 20  
            return (overstayHours - 24) * 20.0;
        } else {
            return 0.0;
        }
    }

    @Override
    public String getSchemeName() {
        return "Hourly Fine (RM 20/hr after 24h)";
    }
}
