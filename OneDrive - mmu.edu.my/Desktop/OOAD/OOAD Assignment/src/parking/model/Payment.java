package parking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Represents a payment made when a vehicle exits
public class Payment {
    private String licensePlate;
    private String ticketId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private long hoursParked;
    private double parkingFee;
    private double finesPaid;
    private double totalAmount;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentTime;

    public Payment(String licensePlate, String ticketId, LocalDateTime entryTime,
                   LocalDateTime exitTime, long hoursParked, double parkingFee,
                   double finesPaid, double totalAmount, PaymentMethod paymentMethod) {
        this.licensePlate = licensePlate;
        this.ticketId = ticketId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hoursParked = hoursParked;
        this.parkingFee = parkingFee;
        this.finesPaid = finesPaid;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentTime = LocalDateTime.now();
    }

    // Getters
    public String getLicensePlate() {
        return licensePlate;
    }

    public String getTicketId() {
        return ticketId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public long getHoursParked() {
        return hoursParked;
    }

    public double getParkingFee() {
        return parkingFee;
    }

    public double getFinesPaid() {
        return finesPaid;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "===== PAYMENT RECEIPT =====\n"
             + "License Plate: " + licensePlate + "\n"
             + "Ticket ID: " + ticketId + "\n"
             + "Entry Time: " + entryTime.format(formatter) + "\n"
             + "Exit Time: " + exitTime.format(formatter) + "\n"
             + "Duration: " + hoursParked + " hour(s)\n"
             + "Parking Fee: RM " + String.format("%.2f", parkingFee) + "\n"
             + "Fines Paid: RM " + String.format("%.2f", finesPaid) + "\n"
             + "Total Amount: RM " + String.format("%.2f", totalAmount) + "\n"
             + "Payment Method: " + paymentMethod + "\n"
             + "===========================";
    }
}
