package parking.strategy;

public class FixedFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long hours) {
        //hours > 24，pay 50；if no pay 0
        if (hours > 24) {
            return 50.0; // pay 50
        } else {
            return 0.0;
        }
    }

    @Override
    public String getSchemeName() {
        return "Fixed RM 50 Scheme"; 
    }
}
