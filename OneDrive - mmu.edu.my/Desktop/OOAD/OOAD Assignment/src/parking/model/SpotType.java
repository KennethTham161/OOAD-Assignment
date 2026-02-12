package parking.model;

public enum SpotType {
    COMPACT,     // RM 2/hour - for motorcycles
    REGULAR,     // RM 5/hour - for cars and SUVs
    HANDICAPPED, // RM 2/hour - for handicapped card holders
    RESERVED;    // RM 10/hour - for VIP customers

    // Returns the hourly rate for this spot type
    public double getHourlyRate() {
        switch (this) {
            case COMPACT:
                return 2.0;
            case REGULAR:
                return 5.0;
            case HANDICAPPED:
                return 2.0;
            case RESERVED:
                return 10.0;
            default:
                return 0.0;
        }
    }
}
