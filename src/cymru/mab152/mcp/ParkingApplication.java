package cymru.mab152.mcp;

import cymru.mab152.mcp.exception.*;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


public class ParkingApplication {

    private Parking parking;
    private Scanner in;
    private String Filename;


    private ParkingApplication() {
        in = new Scanner(System.in);
        parking = new Parking();

        // Get database Filename

        System.out.print("Please enter the name of database file: ");
        Filename = in.nextLine();
    }

    /**
     * Initialise the application (try to load the JSON database file).
     */
    private void init() {
        try {
            parking.load(Filename
            );
        } catch (IOException e) {
            System.err.println("The file cannot be found!\n" + "Check the Filename and try again.\n" +
            "If this is the first time running the app, manually create a file first!");
            // Exit the app
            System.exit(1);
        }

        // If any error occurs, display the message and exit the app.
        catch (ParseException e) {
            System.err.println("The provided file does not contain valid JSON!" +
                    "\nCheck the file and try again.");
            System.exit(1);
        } catch (ParkingZoneMismatchException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ParkingSpaceOccupiedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (VehicleDoesNotExistException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Prints the menu.
     */
    private void printMenu() {
        // Customer menu
        System.out.println("----- CUSTOMER MENU -----");
        System.out.println("1. Add a vehicle.");
        System.out.println("2. Collect a vehicle.");
        System.out.println("3. Get number of free parking spaces.");
        System.out.println("4. Exit the parking (with a token)");

        // Employee menu
        System.out.println("----- EMPLOYEE MENU -----");
        System.out.println("A. See parking statistics.");
        System.out.println("B. Remove a vehicle (and the corresponding parking receipt)");
        System.out.println("C. Get info about a vehicle");
        System.out.println("D. Add an employee");
        System.out.println("E. Remove an employee");

        // Application menu
        System.out.println("----- APPLICATION MENU -----");
        System.out.println("X. toString()");
        System.out.println("S. To save the file");
        System.out.println("Q. Exit the application.");
    }

    /**
     * Following printing menu, the choices are being shown
     */
    private void runMenu() {
        String response;
        do {
            printMenu();
            System.out.print("> ");
            response = in.nextLine().toUpperCase();
            switch (response) {
                case "1": {
                    // Add a Vehicle (as a Customer)
                    try {
                        parking.addVehicle();
                        save();
                    } catch (VehicleSizeNotSupportedException e) {
                        System.err.println(e.getMessage());
                    } catch (VehicleAlreadyExistsException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                }
                case "2": {
                    // Collect a Vehicle (as a Customer)
                    try {
                        parking.collectVehicle();
                        save();
                    } catch (VehicleDoesNotExistException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                }
                case "3": {
                    // Get number of free ParkingSpaces (as a Customer)
                    parking.getNumberOfFreeSpaces();
                    break;
                }
                case "4": {
                    // Exit the parking (with a token)
                    try {
                        parking.exitParking();
                        save();
                    } catch (TokenExpiredException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                }
                case "A": {
                    // See Parking statistics (as an Employee)
                    parking.employeeSeeStatistics();
                    break;
                }
                case "B": {
                    // Remove a Vehicle (as an Employee)
                    try {
                        parking.employeeRemoveVehicle();
                        save();
                    } catch (VehicleDoesNotExistException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                }
                case "C": {
                    // Get info about a Vehicle (as an Employee)
                    try {
                        parking.employeeGetVehicleInfo();
                    } catch (VehicleDoesNotExistException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                }
                case "D": {
                    // Add an Employee
                    parking.employeeAddEmployee();
                    save();
                    break;
                }
                case "E": {
                    // Remove an Employee
                    try {
                        parking.employeeRemoveEmployee();
                        save();
                    } catch (EmployeeDoesNotExistException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                }
                case "X": {
                    System.out.println(parking.toString());

                    break;
                }
                case "Q": {
                    // This exists only to prevent the error message when user types "Q".
                    System.out.println("Thank you for using MCP. Application is closing now...");
                    System.exit(0);
                    break;
                }
                case "S": {
                    System.out.println("All changes successfully saved");
                    save();
                }
                default: {
                    System.err.println("This option does not exist! Try again.");
                }
            }

            // Wait for user input before displaying the menu again
            System.out.println("\n((( press ENTER to continue )))");
            in.nextLine();
        } while (!response.toUpperCase().equals("Q"));
    }

    /**
     * Save all data to the database file.
     *
     */
    private void save() {
        try {
            parking.save(Filename);
        } catch (FileNotFoundException e) {
            System.err.println("The provided file doesn't exist!" +
                    "\nCheck the name and try again.");
        }
    }

    /**
     * The main method of the application.
     *
     * @param args auto-generated by Java
     */
    public static void main(String[] args) {
        System.out.println("Loading Application. Please wait...");
        ParkingApplication app = new ParkingApplication();
        app.init();
        app.runMenu();
        app.save();
        System.out.println("Thank you for using MCP");
    }

}
