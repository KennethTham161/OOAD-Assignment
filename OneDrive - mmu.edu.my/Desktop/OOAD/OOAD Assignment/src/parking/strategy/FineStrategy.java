package parking.strategy;

// Strategy Pattern interface for fine calculation
// The admin can switch between different fine schemes at runtime
public interface FineStrategy {

    // Calculate the fine based on how many hours the vehicle overstayed
    double calculateFine(long overstayHours);

    // Returns the name of this fine scheme (e.g. "Fixed", "Progressive", "Hourly")
    String getSchemeName();
}
