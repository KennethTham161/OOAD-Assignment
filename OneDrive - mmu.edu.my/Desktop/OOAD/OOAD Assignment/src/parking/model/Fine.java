package parking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Represents a fine linked to a license plate
public class Fine {
    private String licensePlate;
    private double amount;
    private String reason;
    private boolean isPaid;
    private LocalDateTime createdTime;

    public Fine(String licensePlate, double amount, String reason) {
        this.licensePlate = licensePlate;
        this.amount = amount;
        this.reason = reason;
        this.isPaid = false;
        this.createdTime = LocalDateTime.now();
    }

    // Getters
    public String getLicensePlate() {
        return licensePlate;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    // Setters
    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Fine: RM " + String.format("%.2f", amount)
             + " | Reason: " + reason
             + " | Paid: " + (isPaid ? "Yes" : "No")
             + " | Date: " + createdTime.format(formatter);
    }
}
