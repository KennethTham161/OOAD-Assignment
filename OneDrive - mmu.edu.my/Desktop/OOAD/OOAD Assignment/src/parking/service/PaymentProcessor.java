package parking.service;

import java.time.LocalDateTime;
import parking.data.DataCenter;
import parking.model.Fine;
import parking.model.Payment;
import parking.model.PaymentMethod;
import parking.model.Ticket;

/**
 * PaymentProcessor - Handles payment processing logic for vehicle exit.
 * Creates payment records, finalizes exits, and updates system state.
 */
public class PaymentProcessor {

    /**
     * Processes a full payment for a vehicle exit.
     * Creates the Payment record, finalizes exit, removes vehicle, and marks fines as paid.
     *
     * @param plate         License plate of the exiting vehicle
     * @param exitService   The ExitService containing calculated billing data
     * @param paymentMethod The chosen payment method (CASH or CARD)
     * @param totalDue      The total amount due (parking fee + fines + unpaid fines)
     * @return the created Payment object
     */
    public Payment processPayment(String plate, ExitService exitService, PaymentMethod paymentMethod, double totalDue) {
        // 1. Permanently record exit timestamp
        exitService.finalizeExit(plate);

        // 2. Gather billing data from the exit service
        Ticket ticket = DataCenter.findTicketByPlate(plate);
        String ticketId = (ticket != null) ? ticket.getTicketId() : "N/A";
        LocalDateTime entryTime = exitService.getIn();
        LocalDateTime exitTime = exitService.getOut();
        long hoursParked = exitService.getHours();
        double parkingFee = exitService.getFee();
        double currentFine = exitService.getFine();
        double oldUnpaidFines = DataCenter.getUnpaidFineTotal(plate);
        double finesPaid = currentFine + oldUnpaidFines;

        // 3. Create Fine objects for the current session (for tracking and reporting)
        if (currentFine > 0) {
            String reason;
            if (hoursParked > 24 && exitService.isViolation()) {
                reason = "Overstay (>" + 24 + "h) + Reserved Spot Violation";
            } else if (hoursParked > 24) {
                reason = "Overstay (>" + 24 + "h)";
            } else {
                reason = "Reserved Spot Violation";
            }
            Fine fineRecord = new Fine(plate, currentFine, reason);
            fineRecord.setPaid(true); // Paid immediately as part of this payment
            DataCenter.addFine(fineRecord);
        }

        // 4. Create the payment record using the 9-parameter constructor
        Payment payment = new Payment(
            plate,          // 1. licensePlate
            ticketId,       // 2. ticketId
            entryTime,      // 3. entryTime
            exitTime,       // 4. exitTime
            hoursParked,    // 5. hoursParked
            parkingFee,     // 6. parkingFee
            finesPaid,      // 7. finesPaid
            totalDue,       // 8. totalAmount
            paymentMethod   // 9. paymentMethod
        );

        // 5. Save payment to the data layer
        DataCenter.addPayment(payment);

        // 6. Release the parking spot and remove the vehicle
        DataCenter.removeVehicle(plate);

        // 7. Mark all outstanding fines as paid (including old unpaid fines)
        DataCenter.markFinesPaid(plate);

        return payment;
    }
}
