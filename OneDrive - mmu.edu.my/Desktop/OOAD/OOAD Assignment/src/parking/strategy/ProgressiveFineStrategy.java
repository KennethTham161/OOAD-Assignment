package parking.strategy;

public class ProgressiveFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long overstayHours) {
        return 0.0;
    }

    @Override
    public String getSchemeName() {
        return "Progressive";
    }
}
