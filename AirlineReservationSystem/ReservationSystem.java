import java.io.*;
import java.util.*;

public class ReservationSystem {
    private static final String FIRST_CLASS = "First";
    private static final String ECONOMY_CLASS = "Economy";
    private static final int FIRST_CLASS_ROWS = 2;
    private static final int ECONOMY_CLASS_ROWS = 20;
    private static final String[] FIRST_CLASS_SEATS = {"A", "B", "C", "D"};
    private static final String[] ECONOMY_CLASS_SEATS = {"A", "B", "C", "D", "E", "F"};
    
    private Airplane airplane;
    private String filename;
    private Scanner scanner;
    
    public ReservationSystem(String filename) {
        this.filename = filename;
        this.airplane = new Airplane();
        this.scanner = new Scanner(System.in);
        loadData();
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ReservationSystem <flightname>");
            return;
        }
        
        ReservationSystem system = new ReservationSystem(args[0]);
        system.run();
    }
    
    public void run() {
        while (true) {
            System.out.println("\nAdd [P]assenger, Add [G]roup, [C]ancel Reservations, Print Seating [A]vailability Chart, Print [M]anifest, [Q]uit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();
            
            switch (choice) {
                case "P":
                    addPassenger();
                    break;
                case "G":
                    addGroup();
                    break;
                case "C":
                    cancelReservations();
                    break;
                case "A":
                    printAvailabilityChart();
                    break;
                case "M":
                    printManifest();
                    break;
                case "Q":
                    saveData();
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void addPassenger() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Service Class: ");
        String serviceClass = scanner.nextLine().trim();
        
        if (!serviceClass.equalsIgnoreCase(FIRST_CLASS) && !serviceClass.equalsIgnoreCase(ECONOMY_CLASS)) {
            System.out.println("Invalid service class. Please use 'First' or 'Economy'.");
            return;
        }
        
        System.out.print("Seat Preference (W)indow, (C)enter, (A)isle: ");
        String preference = scanner.nextLine().trim().toUpperCase();
        
        if (!preference.equals("W") && !preference.equals("C") && !preference.equals("A")) {
            System.out.println("Invalid seat preference. Please use W, C, or A.");
            return;
        }
        
        Seat reservedSeat = airplane.reserveSeat(name, serviceClass, preference);
        if (reservedSeat != null) {
            System.out.println("Seat " + reservedSeat.getRow() + reservedSeat.getColumn() + 
                             " reserved for " + name + " in " + serviceClass + " class.");
        } else {
            String preferenceText = preference.equals("W") ? "Window" : 
                                  preference.equals("C") ? "Center" : "Aisle";
            System.out.println("No " + preferenceText + " seat is available in " + serviceClass + " class.");
            System.out.print("Would you like another seat preference? (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();
            if (response.equals("Y")) {
                addPassenger(); // Recursive call to try again
            }
        }
    }
    
    private void addGroup() {
        System.out.print("Number of passengers in group: ");
        int groupSize;
        try {
            groupSize = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Please try again.");
            return;
        }
        
        List<String> passengerNames = new ArrayList<>();
        for (int i = 0; i < groupSize; i++) {
            System.out.print("Passenger " + (i + 1) + " name: ");
            passengerNames.add(scanner.nextLine().trim());
        }
        
        System.out.print("Service Class: ");
        String serviceClass = scanner.nextLine().trim();
        
        if (!serviceClass.equalsIgnoreCase(FIRST_CLASS) && !serviceClass.equalsIgnoreCase(ECONOMY_CLASS)) {
            System.out.println("Invalid service class. Please use 'First' or 'Economy'.");
            return;
        }
        
        List<Seat> reservedSeats = airplane.reserveGroup(passengerNames, serviceClass);
        if (!reservedSeats.isEmpty()) {
            System.out.println("Group reservation successful:");
            for (Seat seat : reservedSeats) {
                System.out.println(seat.getRow() + seat.getColumn() + ": " + seat.getPassenger());
            }
        } else {
            System.out.println("Unable to accommodate the entire group. No seats have been reserved.");
        }
    }
    
    private void cancelReservations() {
        System.out.print("Enter passenger names to cancel (comma-separated): ");
        String input = scanner.nextLine().trim();
        String[] names = input.split(",");
        
        List<String> passengerNames = new ArrayList<>();
        for (String name : names) {
            passengerNames.add(name.trim());
        }
        
        List<String> cancelledPassengers = airplane.cancelReservations(passengerNames);
        if (!cancelledPassengers.isEmpty()) {
            System.out.println("Cancelled reservations for: " + String.join(", ", cancelledPassengers));
        } else {
            System.out.println("No reservations found for the specified passengers.");
        }
    }
    
    private void printAvailabilityChart() {
        System.out.println("\nAvailability List:");
        airplane.printAvailabilityChart();
    }
    
    private void printManifest() {
        System.out.println("\nManifest List:");
        airplane.printManifest();
    }
    
    private void loadData() {
        File file = new File(filename);
        if (!file.exists()) {
            return;         }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String seatId = parts[0];
                    String passenger = parts[1];
                    String serviceClass = parts[2];
                    
                    int row = Integer.parseInt(seatId.substring(0, seatId.length() - 1));
                    String column = seatId.substring(seatId.length() - 1);
                    
                    airplane.setSeat(row, column, passenger, serviceClass);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
    
    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            airplane.saveToFile(writer);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}
class Seat {
    private int row;
    private String column;
    private String passenger;
    private boolean occupied;
    
    public Seat(int row, String column) {
        this.row = row;
        this.column = column;
        this.occupied = false;
        this.passenger = null;
    }
    
    public boolean reserve(String passengerName) {
        if (!occupied) {
            this.passenger = passengerName;
            this.occupied = true;
            return true;
        }
        return false;
    }
    
    public void cancel() {
        this.passenger = null;
        this.occupied = false;
    }
    
    public boolean isOccupied() {
        return occupied;
    }
    
    public String getPassenger() {
        return passenger;
    }
    
    public int getRow() {
        return row;
    }
    
    public String getColumn() {
        return column;
    }
    
    public String getSeatId() {
        return row + column;
    }
    
    public void setPassenger(String passenger) {
        this.passenger = passenger;
        this.occupied = passenger != null;
    }
}
class SeatRow {
    private int rowNumber;
    private List<Seat> seats;
    private String serviceClass;
    
    public SeatRow(int rowNumber, String[] seatColumns, String serviceClass) {
        this.rowNumber = rowNumber;
        this.serviceClass = serviceClass;
        this.seats = new ArrayList<>();
        
        for (String column : seatColumns) {
            seats.add(new Seat(rowNumber, column));
        }
    }
    
    public Seat findSeatByPreference(String preference) {
        List<Integer> preferredIndices = getPreferredSeatIndices(preference);
        
        for (int index : preferredIndices) {
            if (index < seats.size() && !seats.get(index).isOccupied()) {
                return seats.get(index);
            }
        }
        return null;
    }
    
    private List<Integer> getPreferredSeatIndices(String preference) {
        List<Integer> indices = new ArrayList<>();
        int seatCount = seats.size();
        
        switch (preference) {
            case "W":
                indices.add(0); 
                if (seatCount > 1) {
                    indices.add(seatCount - 1); 
                }
                break;
            case "A": 
                if (seatCount == 4) { 
                    indices.add(1); 
                    indices.add(2);
                } else if (seatCount == 6) {
                    indices.add(2); 
                    indices.add(3); 
                }
                break;
            case "C": 
                if (seatCount == 6) { 
                    indices.add(1); 
                    indices.add(4);
                }
                break;
        }
        return indices;
    }
    
    public int countAdjacentSeats() {
        int maxAdjacent = 0;
        int currentAdjacent = 0;
        
        for (Seat seat : seats) {
            if (!seat.isOccupied()) {
                currentAdjacent++;
                maxAdjacent = Math.max(maxAdjacent, currentAdjacent);
            } else {
                currentAdjacent = 0;
            }
        }
        return maxAdjacent;
    }
    
    public List<Seat> reserveAdjacentSeats(List<String> passengerNames) {
        List<Seat> reservedSeats = new ArrayList<>();
        int seatsNeeded = passengerNames.size();
        
        for (int i = 0; i <= seats.size() - seatsNeeded; i++) {
            boolean canReserve = true;
            for (int j = 0; j < seatsNeeded; j++) {
                if (seats.get(i + j).isOccupied()) {
                    canReserve = false;
                    break;
                }
            }
            
            if (canReserve) {
                for (int j = 0; j < seatsNeeded; j++) {
                    Seat seat = seats.get(i + j);
                    seat.reserve(passengerNames.get(j));
                    reservedSeats.add(seat);
                }
                break;
            }
        }
        
        return reservedSeats;
    }
    
    public List<Seat> getAvailableSeats() {
        List<Seat> available = new ArrayList<>();
        for (Seat seat : seats) {
            if (!seat.isOccupied()) {
                available.add(seat);
            }
        }
        return available;
    }
    
    public List<Seat> getOccupiedSeats() {
        List<Seat> occupied = new ArrayList<>();
        for (Seat seat : seats) {
            if (seat.isOccupied()) {
                occupied.add(seat);
            }
        }
        return occupied;
    }
    
    public Seat getSeat(String column) {
        for (Seat seat : seats) {
            if (seat.getColumn().equals(column)) {
                return seat;
            }
        }
        return null;
    }
    
    public int getRowNumber() {
        return rowNumber;
    }
    
    public String getServiceClass() {
        return serviceClass;
    }
}
class Airplane {
    private static final String FIRST_CLASS = "First";
    private static final String ECONOMY_CLASS = "Economy";
    private static final int FIRST_CLASS_ROWS = 2;
    private static final int ECONOMY_CLASS_ROWS = 20;
    private static final String[] FIRST_CLASS_SEATS = {"A", "B", "C", "D"};
    private static final String[] ECONOMY_CLASS_SEATS = {"A", "B", "C", "D", "E", "F"};
    
    private List<SeatRow> firstClassRows;
    private List<SeatRow> economyClassRows;
    
    public Airplane() {
        initializeSeats();
    }
    
    private void initializeSeats() {
        firstClassRows = new ArrayList<>();
        economyClassRows = new ArrayList<>();
        
        for (int i = 1; i <= FIRST_CLASS_ROWS; i++) {
            firstClassRows.add(new SeatRow(i, FIRST_CLASS_SEATS, FIRST_CLASS));
        }
        
        for (int i = 3; i <= 2 + ECONOMY_CLASS_ROWS; i++) {
            economyClassRows.add(new SeatRow(i, ECONOMY_CLASS_SEATS, ECONOMY_CLASS));
        }
    }
    
    public Seat reserveSeat(String passengerName, String serviceClass, String preference) {
        List<SeatRow> rows = serviceClass.equalsIgnoreCase(FIRST_CLASS) ? firstClassRows : economyClassRows;
        
        for (SeatRow row : rows) {
            Seat seat = row.findSeatByPreference(preference);
            if (seat != null) {
                seat.reserve(passengerName);
                return seat;
            }
        }
        return null;
    }
    
    public List<Seat> reserveGroup(List<String> passengerNames, String serviceClass) {
        List<SeatRow> rows = serviceClass.equalsIgnoreCase(FIRST_CLASS) ? firstClassRows : economyClassRows;
        List<Seat> allReservedSeats = new ArrayList<>();
        List<String> remainingPassengers = new ArrayList<>(passengerNames);
        
        while (!remainingPassengers.isEmpty()) {
            SeatRow bestRow = null;
            int maxAdjacent = 0;
            
            for (SeatRow row : rows) {
                int adjacent = row.countAdjacentSeats();
                if (adjacent > maxAdjacent) {
                    maxAdjacent = adjacent;
                    bestRow = row;
                }
            }
            
            if (bestRow == null || maxAdjacent == 0) {
                for (Seat seat : allReservedSeats) {
                    seat.cancel();
                }
                return new ArrayList<>();
            }
            
            int seatsToReserve = Math.min(maxAdjacent, remainingPassengers.size());
            List<String> passengersForThisRow = remainingPassengers.subList(0, seatsToReserve);
            List<Seat> reservedInRow = bestRow.reserveAdjacentSeats(passengersForThisRow);
            
            if (reservedInRow.size() != seatsToReserve) {
                for (Seat seat : allReservedSeats) {
                    seat.cancel();
                }
                for (Seat seat : reservedInRow) {
                    seat.cancel();
                }
                return new ArrayList<>();
            }
            
            allReservedSeats.addAll(reservedInRow);
            remainingPassengers.removeAll(passengersForThisRow);
        }
        
        return allReservedSeats;
    }
    
    public List<String> cancelReservations(List<String> passengerNames) {
        List<String> cancelledPassengers = new ArrayList<>();
        
        for (String passengerName : passengerNames) {
            boolean found = false;
            
            for (SeatRow row : firstClassRows) {
                for (Seat seat : row.getOccupiedSeats()) {
                    if (seat.getPassenger().equalsIgnoreCase(passengerName)) {
                        seat.cancel();
                        cancelledPassengers.add(passengerName);
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            
            if (!found) {
                for (SeatRow row : economyClassRows) {
                    for (Seat seat : row.getOccupiedSeats()) {
                        if (seat.getPassenger().equalsIgnoreCase(passengerName)) {
                            seat.cancel();
                            cancelledPassengers.add(passengerName);
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }
            }
        }
        
        return cancelledPassengers;
    }
    
    public void printAvailabilityChart() {
        System.out.println("First");
        for (SeatRow row : firstClassRows) {
            List<Seat> availableSeats = row.getAvailableSeats();
            if (!availableSeats.isEmpty()) {
                System.out.print(row.getRowNumber() + ": ");
                for (int i = 0; i < availableSeats.size(); i++) {
                    System.out.print(availableSeats.get(i).getColumn());
                    if (i < availableSeats.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        }
        
        System.out.println("Economy");
        for (SeatRow row : economyClassRows) {
            List<Seat> availableSeats = row.getAvailableSeats();
            if (!availableSeats.isEmpty()) {
                System.out.print(row.getRowNumber() + ": ");
                for (int i = 0; i < availableSeats.size(); i++) {
                    System.out.print(availableSeats.get(i).getColumn());
                    if (i < availableSeats.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        }
    }
    
    public void printManifest() {
        System.out.println("First");
        for (SeatRow row : firstClassRows) {
            for (Seat seat : row.getOccupiedSeats()) {
                System.out.println(seat.getSeatId() + ": " + seat.getPassenger());
            }
        }
        
        System.out.println("Economy");
        for (SeatRow row : economyClassRows) {
            for (Seat seat : row.getOccupiedSeats()) {
                System.out.println(seat.getSeatId() + ": " + seat.getPassenger());
            }
        }
    }
    
    public void setSeat(int row, String column, String passenger, String serviceClass) {
        List<SeatRow> rows = serviceClass.equalsIgnoreCase(FIRST_CLASS) ? firstClassRows : economyClassRows;
        
        for (SeatRow seatRow : rows) {
            if (seatRow.getRowNumber() == row) {
                Seat seat = seatRow.getSeat(column);
                if (seat != null) {
                    seat.setPassenger(passenger);
                }
                break;
            }
        }
    }
    
    public void saveToFile(PrintWriter writer) {
        for (SeatRow row : firstClassRows) {
            for (Seat seat : row.getOccupiedSeats()) {
                writer.println(seat.getSeatId() + "," + seat.getPassenger() + "," + FIRST_CLASS);
            }
        }
        
        for (SeatRow row : economyClassRows) {
            for (Seat seat : row.getOccupiedSeats()) {
                writer.println(seat.getSeatId() + "," + seat.getPassenger() + "," + ECONOMY_CLASS);
            }
        }
    }
}



