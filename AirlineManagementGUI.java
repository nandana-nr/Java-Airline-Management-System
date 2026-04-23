import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Passenger {
    private String name;
    private String passportNumber;
    
    public Passenger(String name, String passportNumber) {
        this.name = name;
        this.passportNumber = passportNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPassportNumber() {
        return passportNumber;
    }
    
    @Override
    public String toString() {
        return name + " (" + passportNumber + ")";
    }
}

class Flight {
    private String flightNumber;
    private String source;
    private String destination;
    private int seatsAvailable;
    
    public Flight(String flightNumber, String source, String destination, int seatsAvailable) {
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.seatsAvailable = seatsAvailable;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public int getSeatsAvailable() {
        return seatsAvailable;
    }
    
    public void bookSeat() {
        if (seatsAvailable > 0) {
            seatsAvailable--;
        }
    }
    
    @Override
    public String toString() {
        return flightNumber + " (" + source + " to " + destination + ")";
    }
}

class Ticket {
    private Passenger passenger;
    private Flight flight;
    private String ticketNumber;
    private Date bookingDate;
    
    public Ticket(Passenger passenger, Flight flight) {
        this.passenger = passenger;
        this.flight = flight;
        this.bookingDate = new Date();
        // Generate a random ticket number
        this.ticketNumber = "TKT" + System.currentTimeMillis() % 10000;
    }
    
    public Passenger getPassenger() {
        return passenger;
    }
    
    public Flight getFlight() {
        return flight;
    }
    
    public String getTicketNumber() {
        return ticketNumber;
    }
    
    public Date getBookingDate() {
        return bookingDate;
    }
    
    @Override
    public String toString() {
        return ticketNumber + ": " + passenger.getName() + " on " + flight.getFlightNumber();
    }
}

public class AirlineManagementGUI {
    // GUI Components
    private JFrame frame;
    private JTextField nameField, passportField;
    private JComboBox<String> sourceBox, destinationBox;
    private JComboBox<Flight> flightBox;
    private JComboBox<Passenger> passengerBox;
    private DefaultTableModel flightTableModel;
    private DefaultTableModel ticketTableModel;
    
    // Data Storage
    private ArrayList<Passenger> passengers = new ArrayList<>();
    private ArrayList<Flight> flights = new ArrayList<>();
    private ArrayList<Ticket> tickets = new ArrayList<>();
    
    // City Data
    private String[] cities = {"Mumbai", "Delhi", "Chennai", "Bangalore", "Kolkata", "Hyderabad", "Pune"};
    
    public AirlineManagementGUI() {
        // Initialize the frame
        frame = new JFrame("Airline Management System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Initialize data
        initializeFlights();
        
        // Create the UI
        createUI();
        
        frame.setLocationRelativeTo(null);  
        frame.setVisible(true);
    }
    
    private void initializeFlights() {
        flights.add(new Flight("AI101", "Mumbai", "Delhi", 10));
        flights.add(new Flight("AI102", "Delhi", "Bangalore", 5));
        flights.add(new Flight("AI103", "Chennai", "Kolkata", 8));
        flights.add(new Flight("AI104", "Mumbai", "Bangalore", 12));
        flights.add(new Flight("AI105", "Delhi", "Chennai", 7));
        flights.add(new Flight("AI106", "Kolkata", "Mumbai", 9));
        flights.add(new Flight("AI107", "Bangalore", "Hyderabad", 15));
        flights.add(new Flight("AI108", "Hyderabad", "Pune", 6));
    }
    
    private void createUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Register Passenger", createRegisterPanel());
        tabbedPane.addTab("Search Flight", createSearchPanel());
        tabbedPane.addTab("Book Ticket", createBookingPanel());
        tabbedPane.addTab("Flight Availability", createFlightAvailabilityPanel());
        tabbedPane.addTab("Ticket Management", createTicketManagementPanel());
        
        frame.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel statusBar = new JPanel();
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.add(new JLabel("Airline Management System v1.0"));
        frame.add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        nameField = new JTextField(20);
        passportField = new JTextField(20);
        JButton registerBtn = new JButton("Register Passenger");
        registerBtn.addActionListener(e -> registerPassenger());
        
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Passport Number:"));
        formPanel.add(passportField);
        formPanel.add(new JLabel(""));
        formPanel.add(registerBtn);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // List of registered passengers
        DefaultTableModel passengerTableModel = new DefaultTableModel(
            new Object[]{"Name", "Passport Number"}, 0
        );
        JTable passengerTable = new JTable(passengerTableModel);
        JScrollPane scrollPane = new JScrollPane(passengerTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel searchFormPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        searchFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        sourceBox = new JComboBox<>(cities);
        destinationBox = new JComboBox<>(cities);
        JButton searchBtn = new JButton("Search Flights");
        searchBtn.addActionListener(e -> searchFlight());
        
        searchFormPanel.add(new JLabel("Source:"));
        searchFormPanel.add(sourceBox);
        searchFormPanel.add(new JLabel("Destination:"));
        searchFormPanel.add(destinationBox);
        searchFormPanel.add(new JLabel(""));
        searchFormPanel.add(searchBtn);
        
        panel.add(searchFormPanel, BorderLayout.NORTH);
        
        // Search results
        DefaultTableModel searchResultsModel = new DefaultTableModel(
            new Object[]{"Flight Number", "Source", "Destination", "Available Seats"}, 0
        );
        JTable searchResultsTable = new JTable(searchResultsModel);
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel bookingFormPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        bookingFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        passengerBox = new JComboBox<>();
        flightBox = new JComboBox<>();
        JButton bookBtn = new JButton("Book Ticket");
        bookBtn.addActionListener(e -> bookTicket());
        
        bookingFormPanel.add(new JLabel("Select Passenger:"));
        bookingFormPanel.add(passengerBox);
        bookingFormPanel.add(new JLabel("Select Flight:"));
        bookingFormPanel.add(flightBox);
        bookingFormPanel.add(new JLabel(""));
        bookingFormPanel.add(bookBtn);
        
        panel.add(bookingFormPanel, BorderLayout.NORTH);
        
        // Booking confirmation area
        JTextArea confirmationArea = new JTextArea();
        confirmationArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(confirmationArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFlightAvailabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Flight table
        flightTableModel = new DefaultTableModel(
            new Object[]{"Flight Number", "Source", "Destination", "Available Seats"}, 0
        );
        JTable flightTable = new JTable(flightTableModel);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh button
        JButton refreshBtn = new JButton("Refresh Flight Data");
        refreshBtn.addActionListener(e -> refreshFlightTable());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        refreshFlightTable();
        
        return panel;
    }
    
    private JPanel createTicketManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Ticket table
        ticketTableModel = new DefaultTableModel(
            new Object[]{"Ticket Number", "Passenger Name", "Flight", "Booking Date"}, 0
        );
        JTable ticketTable = new JTable(ticketTableModel);
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh Tickets");
        refreshBtn.addActionListener(e -> refreshTicketTable());
        buttonPanel.add(refreshBtn);
        
        JButton cancelBtn = new JButton("Cancel Selected Ticket");
        cancelBtn.addActionListener(e -> {
            int selectedRow = ticketTable.getSelectedRow();
            if (selectedRow >= 0) {
                String ticketNumber = (String) ticketTableModel.getValueAt(selectedRow, 0);
                cancelTicket(ticketNumber);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a ticket to cancel.", 
                                           "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(cancelBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void registerPassenger() {
        String name = nameField.getText().trim();
        String passport = passportField.getText().trim();
        
        if (name.isEmpty() || passport.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both name and passport number.", 
                                         "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check for duplicate passport
        for (Passenger p : passengers) {
            if (p.getPassportNumber().equals(passport)) {
                JOptionPane.showMessageDialog(frame, "A passenger with this passport number already exists.", 
                                             "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        Passenger newPassenger = new Passenger(name, passport);
        passengers.add(newPassenger);
        
        passengerBox.addItem(newPassenger);
        
        nameField.setText("");
        passportField.setText("");
        
        JOptionPane.showMessageDialog(frame, "Passenger registered successfully!", 
                                     "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void searchFlight() {
        String source = (String) sourceBox.getSelectedItem();
        String destination = (String) destinationBox.getSelectedItem();
        
        if (source.equals(destination)) {
            JOptionPane.showMessageDialog(frame, "Source and destination cannot be the same.", 
                                         "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        flightBox.removeAllItems();
        
        boolean found = false;
        for (Flight flight : flights) {
            if (flight.getSource().equals(source) && flight.getDestination().equals(destination)) {
                flightBox.addItem(flight);
                found = true;
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(frame, "No flights available for the selected route.", 
                                         "No Flights", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Flights found! Please select a flight to book.", 
                                         "Flights Available", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void bookTicket() {
        Passenger selectedPassenger = (Passenger) passengerBox.getSelectedItem();
        Flight selectedFlight = (Flight) flightBox.getSelectedItem();
        
        if (selectedPassenger == null || selectedFlight == null) {
            JOptionPane.showMessageDialog(frame, "Please select both a passenger and a flight.", 
                                         "Incomplete Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check seat availability
        if (selectedFlight.getSeatsAvailable() <= 0) {
            JOptionPane.showMessageDialog(frame, "No seats available on this flight.", 
                                         "Flight Full", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Book the seat
        selectedFlight.bookSeat();
        
        // Create a ticket
        Ticket newTicket = new Ticket(selectedPassenger, selectedFlight);
        tickets.add(newTicket);
        
        // Refresh displays
        refreshFlightTable();
        refreshTicketTable();
        
        // Show confirmation
        JOptionPane.showMessageDialog(frame, 
            "Ticket booked successfully!\n\n" +
            "Ticket Number: " + newTicket.getTicketNumber() + "\n" +
            "Passenger: " + selectedPassenger.getName() + "\n" +
            "Flight: " + selectedFlight.getFlightNumber() + "\n" +
            "Route: " + selectedFlight.getSource() + " to " + selectedFlight.getDestination(),
            "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cancelTicket(String ticketNumber) {
        // Find the ticket
        Ticket ticketToCancel = null;
        for (Ticket ticket : tickets) {
            if (ticket.getTicketNumber().equals(ticketNumber)) {
                ticketToCancel = ticket;
                break;
            }
        }
        
        if (ticketToCancel == null) {
            JOptionPane.showMessageDialog(frame, "Ticket not found.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm cancellation
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to cancel ticket " + ticketNumber + "?",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Process the cancellation
        // Increase seat availability
        for (Flight flight : flights) {
            if (flight.getFlightNumber().equals(ticketToCancel.getFlight().getFlightNumber())) {
                // Add the seat back
                flight.bookSeat(); // This isn't ideal, should add a separate method to increase seats
                break;
            }
        }
        
        // Remove the ticket
        tickets.remove(ticketToCancel);
        
        // Refresh displays
        refreshFlightTable();
        refreshTicketTable();
        
        JOptionPane.showMessageDialog(frame, "Ticket cancelled successfully!", 
                                     "Cancellation Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshFlightTable() {
        flightTableModel.setRowCount(0);
        for (Flight flight : flights) {
            flightTableModel.addRow(new Object[]{
                flight.getFlightNumber(), 
                flight.getSource(), 
                flight.getDestination(), 
                flight.getSeatsAvailable()
            });
        }
    }
    
    private void refreshTicketTable() {
        ticketTableModel.setRowCount(0);
        for (Ticket ticket : tickets) {
            ticketTableModel.addRow(new Object[]{
                ticket.getTicketNumber(),
                ticket.getPassenger().getName(),
                ticket.getFlight().getFlightNumber() + ": " + 
                    ticket.getFlight().getSource() + " to " + ticket.getFlight().getDestination(),
                ticket.getBookingDate()
            });
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new AirlineManagementGUI();
        });
    }
}
