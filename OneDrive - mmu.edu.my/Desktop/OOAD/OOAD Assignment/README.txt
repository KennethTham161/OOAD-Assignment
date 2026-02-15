HOW TO COMPILE AND RUN
----------------------
Open a terminal in this folder and run:

  javac -d out src\parking\model\*.java src\parking\strategy\*.java src\parking\data\*.java src\parking\service\*.java src\parking\ui\*.java src\parking\*.java

  java -cp out parking.Main

No external libraries needed. Just plain Java + Swing.


PROJECT STRUCTURE
-----------------
src/parking/
  |
  |-- Main.java                  --> Entry point. Just launches MainFrame.
  |-- MainFrame.java             --> The main window. Uses CardLayout to switch
  |                                  between the 4 panels using the nav bar.
  |
  |-- data/
  |     |-- DataCenter.java      --> Central data storage. ALL data goes here.
  |                                  Uses static ArrayLists (same as Lab Test).
  |                                  Pre-seeds 5 floors x 10 spots = 50 spots.
  |
  |-- model/                     --> All data classes and enums live here.
  |     |-- Vehicle.java         --> Abstract class. Has licensePlate, entryTime,
  |     |                            exitTime, spotId. Subclasses override canParkIn().
  |     |-- Motorcycle.java      --> Can park in COMPACT only.
  |     |-- Car.java             --> Can park in COMPACT or REGULAR.
  |     |-- SUV.java             --> Can park in REGULAR only.
  |     |-- HandicappedVehicle.java --> Can park in ANY spot.
  |     |-- VehicleType.java     --> Enum: MOTORCYCLE, CAR, SUV, HANDICAPPED
  |     |-- ParkingSpot.java     --> Has spotId (e.g. "F1-R1-S1"), type, status,
  |     |                            currentVehicle. Has occupy() and release().
  |     |-- Floor.java           --> Contains an ArrayList of ParkingSpots.
  |     |-- ParkingLot.java      --> Contains an ArrayList of Floors.
  |     |-- SpotType.java        --> Enum: COMPACT (RM2), REGULAR (RM5),
  |     |                            HANDICAPPED (RM2), RESERVED (RM10).
  |     |                            Has getHourlyRate() method.
  |     |-- SpotStatus.java      --> Enum: AVAILABLE, OCCUPIED
  |     |-- Ticket.java          --> Format: T-PLATE-TIMESTAMP
  |     |-- Payment.java         --> Full receipt with entry/exit time, fees, fines, total.
  |     |-- Fine.java            --> Linked to license plate. Has amount, reason, isPaid.
  |     |-- PaymentMethod.java   --> Enum: CASH, CARD
  |
  |-- strategy/                  --> Strategy Pattern for fine calculation.
  |     |-- FineStrategy.java    --> Interface: calculateFine(overstayHours), getSchemeName()
  |     |-- FixedFineStrategy.java      --> STUB. Member 4 to implement (Flat RM 50).
  |     |-- ProgressiveFineStrategy.java --> STUB. Member 4 to implement (RM 50/100/150/200).
  |     |-- HourlyFineStrategy.java     --> STUB. Member 4 to implement (RM 20 per hour).
  |
  |-- service/                   --> Business logic layer (between UI and DataCenter).
  |     |                            These are EMPTY STUBS. Each member implements their own.
  |     |-- EntryService.java    --> STUB. Member 2 to implement.
  |     |-- ExitService.java     --> STUB. Member 4 to implement.
  |     |-- SpotAllocation.java  --> STUB. Member 3 to implement.
  |     |-- PaymentProcessor.java --> STUB. Member 4 to implement.
  |     |-- ReportService.java   --> STUB. Member 5 to implement.
  |
  |-- ui/                        --> Swing panels (one per tab in the app).
  |                                  These are PLACEHOLDER STUBS. Each member builds their own UI.
        |-- EntryPanel.java      --> PLACEHOLDER. Member 2 to implement.
        |-- ExitPanel.java       --> PLACEHOLDER. Member 4 to implement.
        |-- AdminPanel.java      --> PLACEHOLDER. Member 5 to implement.
        |-- ReportingPanel.java  --> PLACEHOLDER. Member 5 to implement.


HOW DATACENTER WORKS (IMPORTANT - READ THIS)
---------------------------------------------
DataCenter is the central "database" of the system. It stores everything
in static ArrayLists, just like the Lab Test DataCenter.

All methods are STATIC. You never create an instance.
Just call DataCenter.methodName() from anywhere.

Example:
    DataCenter.parkVehicle(vehicle, spot);
    Vehicle v = DataCenter.findVehicleByPlate("ABC1234");
    ArrayList<Fine> fines = DataCenter.getUnpaidFines("ABC1234");

Available methods:

  PARKING LOT:
    getParkingLot()                          --> Get the whole ParkingLot object
    getFloors()                              --> Get all floors
    getFloor(int floorNumber)                --> Get a specific floor
    findSpotById(String spotId)              --> Find a spot like "F1-R1-S1"
    getAvailableSpotsForVehicle(Vehicle v)   --> Spots this vehicle CAN park in
    getAvailableSpotsByType(SpotType type)   --> Spots of a specific type

  VEHICLES:
    parkVehicle(Vehicle v, ParkingSpot s)    --> Parks vehicle, marks spot occupied
    removeVehicle(String plate)              --> Frees up the spot
    findVehicleByPlate(String plate)         --> Find currently parked vehicle
    getAllParkedVehicles()                    --> All vehicles still in the lot
    getVehicles()                            --> All vehicles (parked + exited)

  TICKETS:
    addTicket(Ticket t)                      --> Save a new ticket
    getTickets()                             --> Get all tickets
    findTicketByPlate(String plate)          --> Find most recent ticket for a plate

  PAYMENTS:
    addPayment(Payment p)                    --> Save a payment record
    getPayments()                            --> Get all payments
    getTotalRevenue()                        --> Sum of all payment totals

  FINES:
    addFine(Fine f)                          --> Add a new fine
    getFines()                               --> Get all fines
    getUnpaidFines(String plate)             --> Unpaid fines for a plate
    getUnpaidFineTotal(String plate)         --> Total RM of unpaid fines
    markFinesPaid(String plate)              --> Mark all fines for a plate as paid
    getAllUnpaidFines()                       --> All unpaid fines across all vehicles

  FINE STRATEGY:
    getActiveFineStrategy()                  --> Get the current FineStrategy object
    setActiveFineStrategy(FineStrategy s)    --> Change the active scheme
    getActiveFineSchemeName()                --> Get name like "Fixed", "Progressive", "Hourly"

  REPORTS:
    getOccupancyRate()                       --> Percentage of spots occupied
    getTotalSpots()                          --> Total number of spots (50)
    getTotalOccupied()                       --> Number of occupied spots


HOW MAINFRAME WORKS
-------------------
MainFrame uses CardLayout. There are 4 panels stacked on top of each other.
The nav bar at the top has 4 buttons. Clicking a button shows that panel.

If you need to add a new panel in the future, just:
  1. Create a new JPanel class in the ui/ package
  2. Add it to cardPanel in MainFrame: cardPanel.add(myPanel, "Name")
  3. Add a button to the nav bar


PARKING LOT LAYOUT (DEFAULT SEEDING)
-------------------------------------
5 Floors, each with 2 rows, each row with 5 spots.
Total: 50 spots.

Per floor:
  Row 1: Spot 1 (COMPACT), Spot 2 (COMPACT), Spot 3 (REGULAR), Spot 4 (REGULAR), Spot 5 (HANDICAPPED)
  Row 2: Spot 1 (COMPACT), Spot 2 (COMPACT), Spot 3 (REGULAR), Spot 4 (REGULAR), Spot 5 (RESERVED)

Spot ID format: F{floor}-R{row}-S{spot}
Example: F3-R2-S4 = Floor 3, Row 2, Spot 4 (REGULAR)




