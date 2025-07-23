# Airline Reservation System

Java-based airline reservation system that manages individual and group reservations across First and Economy class seating.

## Overview

This console-based application simulates a real-world airline reservation system with two service classes (First and Economy), supporting individual passenger bookings with seat preferences, group reservations with adjacent seating logic, and comprehensive reservation management features.

## Project Timeline
September 2022 - November 2022

## Features

### Core Functionality
- **Individual Reservations**: Book passengers with specific seat preferences (Window, Aisle, Center)
- **Group Reservations**: Intelligent algorithm to seat groups together using adjacent seat optimization
- **Reservation Cancellation**: Cancel individual or multiple passenger reservations
- **Real-time Reporting**: View seat availability charts and passenger manifests
- **Data Persistence**: Automatic save/load of reservation data between sessions

### Seating Configuration
- **First Class**: Rows 1-2, Seats A-D (4 seats per row)
- **Economy Class**: Rows 3-22, Seats A-F (6 seats per row)
- **Smart Seat Assignment**: 
  - Window seats: A and F positions
  - Aisle seats: B,C (First Class), C,D (Economy)
  - Center seats: B,E (Economy only)

### Advanced Group Logic
- Finds optimal row with sufficient adjacent seats
- Falls back to largest available seat blocks when full rows unavailable
- Ensures entire group is seated or no one is seated (atomic operation)

## System Architecture

```
ReservationSystem (Main Class)
├── Airplane (Manages overall seating)
├── SeatRow (Represents individual rows)
└── Seat (Individual seat management)
```

### Class Responsibilities
- **ReservationSystem**: User interface, file I/O, main program flow
- **Airplane**: High-level seat management, reservation logic
- **SeatRow**: Row-specific operations, adjacent seat finding
- **Seat**: Individual seat state and passenger information

## Getting Started

### Prerequisites
- Java JDK 8 or higher
- Command line interface or Java IDE (Eclipse, IntelliJ, VS Code)

### Installation & Setup

1. **Clone or download the project**
   ```bash
   git clone [your-repository-url]
   cd AirlineReservationSystem
   ```

2. **Compile the program**
   ```bash
   javac ReservationSystem.java
   ```

3. **Run the application**
   ```bash
   java ReservationSystem <flight_data_file>
   ```
   Example:
   ```bash
   java ReservationSystem FaithsFlight01
   ```


## Usage Guide

### Main Menu Options
```
Add [P]assenger, Add [G]roup, [C]ancel Reservations, 
Print Seating [A]vailability Chart, Print [M]anifest, [Q]uit
```

### Individual Passenger Booking
```
Enter choice: P
Name: Kim Kardashian
Service Class: First
Seat Preference (W)indow, (C)enter, (A)isle: W
```

### Group Booking
```
Enter choice: G
Number of passengers in group: 3
Passenger 1 name: George Washington
Passenger 2 name: Penny Lam 
Passenger 3 name: David Long
Service Class: Economy
```

### Cancellation
```
Enter choice: C
Enter passenger names to cancel (comma-separated): John Smith, Alice Johnson
```

### Reports
- **[A]vailability Chart**: Shows available seats by row and class
- **[M]anifest**: Lists all occupied seats with passenger names

## Data Persistence

The system automatically saves reservation data to a specified file in CSV format.

- **File Location**: Same directory as the Java files
- **Format**: `SeatID,PassengerName,ServiceClass`
- **Auto-Load**: Previous reservations loaded automatically on startup
- **Auto-Save**: Data saved when exiting with 'Q' option

## Error Handling

The system includes robust error handling for:
- Invalid service class entries
- Invalid seat preferences
- Unavailable seat types (offers alternatives)
- Non-existent passenger cancellations
- Insufficient space for large groups
- File I/O errors
- Invalid user inputs


## Acknowledgments

- Developed as part of software engineering coursework
- Implements UML design principles and object-oriented programming concepts
- Thanks to Dr. Yeung for project requirements
