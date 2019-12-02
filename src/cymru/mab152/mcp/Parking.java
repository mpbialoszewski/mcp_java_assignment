package cymru.mab152.mcp;

import cymru.mab152.mcp.exception.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

/**
 * The main Parking class. Contains all data about the Parking,
 * as well as methods allowing to interact with it.
 */
public class Parking {

    private Random rand;
    private Scanner in;
    private String name;
    private JSONParser parser;
    private ArrayList<ParkingZone> parkingZones;
    private ArrayList<DriverEmployee> driverEmployees;
    private ArrayList<FreeEmployee> freeEmployees;
    private ArrayList<ExitToken> exitTokens;

    /**
     * The constructor for Parking object.
     */
    public Parking() {
        rand = new Random();
        in = new Scanner(System.in);
        parser = new JSONParser();
        parkingZones = new ArrayList<>();
        driverEmployees = new ArrayList<>();
        freeEmployees = new ArrayList<>();
        exitTokens = new ArrayList<>();
    }

    /**
     * Scans all existing ParkingReceipts and returns a new, unique ID that can be used.
     *
     * @return new, unique ParkingReceipt ID
     */
    private int getNextReceiptId() {
        int max = -1;

        for (ParkingZone pz : parkingZones) {
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                if (!ps.isFree()) {
                    int tempReceiptId = ps.getVehicle().getParkingReceipt().getId();
                    if (tempReceiptId > max) {
                        max = tempReceiptId;
                    }
                }
            }
        }
        return ++max;
    }

    /**
     * Scans all FreeEmployees and DriverEmployees and returns a new, unique ID
     * that can be used while adding a new Employee.
     *
     * @return new, unique Employee ID
     */
    private int getNextEmployeeId() {
        int max = -1;

        // Scan FreeEmployees
        for (FreeEmployee fe : freeEmployees) {
            int tempId = fe.getId();
            if (tempId >= max) {
                max = tempId;
            }
        }

        // Scan DriverEmployees
        for (DriverEmployee de : driverEmployees) {
            int tempId = de.getId();
            if (tempId >= max) {
                max = tempId;
            }
        }

        return ++max;
    }

    /**
     * Empties the JSON database file and then writes all data to it.
     *
     * @param Filename database filename
     * @throws FileNotFoundException thrown if provided file doesn't exist
     */
    void save(String Filename) throws FileNotFoundException {

        // Clear the file
        try (PrintWriter pw = new PrintWriter(Filename)) {
            pw.write("");
        }

        // Save all data to the JSON file
        // Create the main JSON object
        JSONObject mainJsonObject = new JSONObject();
        // Add the Parking's name to the main JSON object
        mainJsonObject.put("name", this.name);

        // Create a JSON array for Employees
        JSONArray jsonEmployees = new JSONArray();
        for (FreeEmployee fe : freeEmployees) {
            // Create a JSON object for each Employee
            JSONObject jsonEmployee = new JSONObject();
            jsonEmployee.put("id", fe.getId());
            jsonEmployee.put("name", fe.getName());

            // Add the Employee JSON object to JSON array
            jsonEmployees.add(jsonEmployee);
        }
        // Add the JSON array with Employees to the main JSON object
        mainJsonObject.put("employees", jsonEmployees);

        // -----------------------------------
        // Create a JSON array for ParkingZones
        JSONArray jsonParkingZones = new JSONArray();
        for (ParkingZone pz : parkingZones) {
            // Create a JSON object for each ParkingZone
            JSONObject jsonParkingZone = new JSONObject();
            jsonParkingZone.put("id", pz.getId());

            // Create a JSON array with ParkingSpaces contained within the ParkingZone
            JSONArray jsonParkingSpaces = new JSONArray();
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                // Create a JSON object for each ParkingSpace and but its ID into it
                JSONObject jsonParkingSpace = new JSONObject();
                jsonParkingSpace.put("id", ps.getID());

                // Add the JSON ParkingSpace object to the JSON ParkingZone array
                jsonParkingSpaces.add(jsonParkingSpace);
            }
            // Add the JSON array to the JSON object
            jsonParkingZone.put("parkingSpaces", jsonParkingSpaces);

            jsonParkingZone.put("price", pz.getPrice());

            // Create a JSON array for list of accepted VehicleTypes
            JSONArray jsonAcceptedVehicles = new JSONArray();
            for (VehicleType vt : pz.getListOfAcceptedVehicles()) {
                // Convert the VehicleType to String
                String vehicleTypeString = vt.toString().toUpperCase();
                jsonAcceptedVehicles.add(vehicleTypeString);
            }

            // Add the array of accepted VehicleTypes to the main ParkingZone JSON object
            jsonParkingZone.put("acceptedVehicles", jsonAcceptedVehicles);

            // Add the JSON ParkingZone object to the main ParkingSpaces JSON array
            jsonParkingZones.add(jsonParkingZone);
        }

        // Add the JSON array with ParkingZones to the main JSON object
        mainJsonObject.put("parkingZones", jsonParkingZones);

        // -------------------------------
        // Create a JSON array for Vehicles
        JSONArray jsonVehicles = new JSONArray();

        // Get all Vehicles
        for (ParkingZone pz : parkingZones) {
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                // If the ParkingSpace contains a Vehicle -- add it to the JSON array
                if (!ps.isFree()) {
                    Vehicle v = ps.getVehicle();
                    // Create a JSON object for each Vehicle
                    JSONObject jsonVehicle = new JSONObject();

                    // Put all Vehicle's data into the JSON object
                    jsonVehicle.put("licensePlate", v.getLicensePlate());
                    jsonVehicle.put("parkingSpace", ps.getID());
                    jsonVehicle.put("type", v.getVehicleType().toString().toUpperCase());
                    jsonVehicle.put("height", v.getHeight());
                    jsonVehicle.put("length", v.getLength());
                    jsonVehicle.put("receiptId", v.getParkingReceipt().getId());

                    // Add the JSON object to the JSON array
                    jsonVehicles.add(jsonVehicle);
                }
            }
        }

        // Add the JSON array with Vehicles to the main JSON object
        mainJsonObject.put("vehicles", jsonVehicles);

        // -------------------------------
        // Create a JSON array for ParkingReceipts
        JSONArray jsonParkingReceipts = new JSONArray();

        // Get all ParkingReceipts
        for (ParkingZone pz : parkingZones) {
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                if (!ps.isFree()) {
                    ParkingReceipt tempParkingReceipt = ps.getVehicle().getParkingReceipt();

                    // Create a JSON object for each ParkingReceipt
                    JSONObject jsonParkingReceipt = new JSONObject();

                    // Put all ParkingReceipt's data into the JSON object
                    jsonParkingReceipt.put("id", tempParkingReceipt.getId());
                    jsonParkingReceipt.put("dateStart", tempParkingReceipt.getStartDate().getTime());
                    jsonParkingReceipt.put("isDisabled", tempParkingReceipt.isOwnerDisabled());

                    // Add the JSON object to the JSON array
                    jsonParkingReceipts.add(jsonParkingReceipt);
                }
            }
        }

        // Add the JSON array with ParkingReceipts to the main JSON object
        mainJsonObject.put("parkingReceipts", jsonParkingReceipts);

        // ----------------------------
        // Create a JSON array for ExitTokens
        JSONArray jsonExitTokens = new JSONArray();

        // Get all ExitTokens
        for (ExitToken et : exitTokens) {

            // Create a JSON object for each ExitToken
            JSONObject jsonExitToken = new JSONObject();

            // Put all ExitToken's data into the JSON object
            jsonExitToken.put("id", et.getId());
            jsonExitToken.put("date", et.getDateTimestamp());

            // Add the JSON object to the JSON array
            jsonExitTokens.add(jsonExitToken);
        }

        // Add the JSON array with ExitTokens to the main JSON object
        mainJsonObject.put("exitTokens", jsonExitTokens);

        // ----------------------------
        // Save all data to the JSON database file
        try (FileWriter fw = new FileWriter(Filename)) {
            fw.write(mainJsonObject.toJSONString());
            fw.flush();
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the database:");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Reads the JSON database file.
     *
     * @param Filename database Filename
     * @throws FileNotFoundException thrown if provided file doesn't exist
     * @throws ParseException thrown if JSON cannot be parsed
     * @throws ParkingZoneMismatchException thrown if a ParkingSpace is inside a ParkingZone that can't support it
     * @throws ParkingSpaceOccupiedException thrown if ParkingSpace is already occupied
     * @throws VehicleDoesNotExistException thrown if Vehicle does not exist
     */
    void load(String Filename) throws FileNotFoundException, ParseException, ParkingZoneMismatchException,
            ParkingSpaceOccupiedException, VehicleDoesNotExistException, IOException {

        String fileContent = getContent(Filename);
        Object obj = parser.parse(fileContent);
        JSONObject mainJsonObject = (JSONObject) obj;

        // Set Parking name
        this.name = mainJsonObject.get("name").toString();

        // Parse Employees
        JSONArray jsonEmployees = (JSONArray) mainJsonObject.get("employees");
        parseEmployees(jsonEmployees);

        // Parse ParkingZones and ParkingSpaces
        JSONArray jsonParkingZones = (JSONArray) mainJsonObject.get("parkingZones");
        parseParkingZones(jsonParkingZones);

        // Parse parked Vehicles
        JSONArray jsonVehicles = (JSONArray) mainJsonObject.get("vehicles");
        parseVehicles(jsonVehicles);

        // Parse ParkingReceipts
        JSONArray jsonParkingReceipts = (JSONArray) mainJsonObject.get("parkingReceipts");
        parseParkingReceipts(jsonParkingReceipts);

        // Parse ExitTokens
        JSONArray jsonExitTokens = (JSONArray) mainJsonObject.get("exitTokens");
        parseExitTokens(jsonExitTokens);
    }

    /**
     * Imports all Employees from JSON database.
     *
     * @param ja JSON array with Employees
     */
    private void parseEmployees(JSONArray ja) {
        for (Object o : ja) {
            JSONObject tempEmployeeObject = (JSONObject) o;
            int tempID = Integer.parseInt(tempEmployeeObject.get("id").toString());
            String tempName = tempEmployeeObject.get("name").toString();

            // Add the Employee to the list
            FreeEmployee tempEmployee = new FreeEmployee(tempID, tempName);
            freeEmployees.add(tempEmployee);
        }
    }

    /**
     * Imports all ParkingZones and ParkingSpaces from JSON database.
     *
     * @param ja JSON array with ParkingZones
     * @throws ParkingZoneMismatchException thrown if a ParkingZone contains a ParkingSpace with incorrect ID.
     */
    private void parseParkingZones(JSONArray ja) throws ParkingZoneMismatchException {
        for (Object o : ja) {
            JSONObject tempParkingZoneObj = (JSONObject) o;
            String tempParkingZoneId = tempParkingZoneObj.get("id").toString();

            // Get the list of parking spaces
            JSONArray tempParkingSpaces = (JSONArray) tempParkingZoneObj.get("parkingSpaces");
            ArrayList<ParkingSpace> tempParkingSpacesList = new ArrayList<>();
            for (Object o2 : tempParkingSpaces) {
                JSONObject temp = (JSONObject) o2;
                String tempParkingSpaceId = temp.get("id").toString();

                // Add the ParkingSpace to the temp list
                ParkingSpace tempParkingSpace = new ParkingSpace(tempParkingSpaceId);
                tempParkingSpacesList.add(tempParkingSpace);
            }

            // Get the pricePerHour
            float tempPrice = Float.parseFloat(tempParkingZoneObj.get("price").toString());

            // Get the list of accepted vehicles
            JSONArray tempAcceptedVehicles = (JSONArray) tempParkingZoneObj.get("acceptedVehicles");
            ArrayList<VehicleType> tempAcceptedVehicleList = new ArrayList<>();
            for (Object o2 : tempAcceptedVehicles) {
                VehicleType tempVehicleType = VehicleType.valueOf(o2.toString().toUpperCase());
                tempAcceptedVehicleList.add(tempVehicleType);
            }

            // Assign the ParkingSpaces to the ParkingZone and add it to the main list
            ParkingZone tempParkingZone = new ParkingZone(tempParkingZoneId, tempPrice, tempAcceptedVehicleList);
            tempParkingZone.addParkingSpace(tempParkingSpacesList);
            parkingZones.add(tempParkingZone);
        }
    }

    /**
     * Imports all parked Vehicles from JSON database.
     *
     * @param ja JSON array with Vehicles
     * @throws IllegalArgumentException thrown if Vehicle's type is invalid
     * @throws ParkingSpaceOccupiedException thrown if the ParkingSpace is already occupied
     */
    private void parseVehicles(JSONArray ja) throws IllegalArgumentException, ParkingSpaceOccupiedException {
        for (Object o : ja) {
            JSONObject tempVehicleObject = (JSONObject) o;
            String tempLicensePlate = tempVehicleObject.get("licensePlate").toString();
            String tempParkingSpaceId = tempVehicleObject.get("parkingSpace").toString();

            // Get Vehicle's type
            String tempVehicleTypeString = tempVehicleObject.get("type").toString().toUpperCase();
            VehicleType tempVehicleType = VehicleType.valueOf(tempVehicleTypeString);

            // Get Vehicle's dimensions
            float tempVehicleHeight = Float.parseFloat(tempVehicleObject.get("height").toString());
            float tempVehicleLength = Float.parseFloat(tempVehicleObject.get("length").toString());

            int tempVehicleReceiptId = Integer.parseInt(tempVehicleObject.get("receiptId").toString());

            // Create the Vehicle object and assign the ParkingSpace's ID to it
            Vehicle tempVehicle = new Vehicle(tempLicensePlate, tempVehicleHeight, tempVehicleLength,
                    tempVehicleType);
            tempVehicle.setTempParkingReceiptId(tempVehicleReceiptId);

            // Add the Vehicle to the ParkingSpace
            ParkingSpace tempParkingSpace = getParkingSpaceById(tempParkingSpaceId);
            tempVehicle.parkIn(tempParkingSpace);
        }
    }

    /**
     * Imports all ParkingReceipts from JSON database.
     *
     * @param ja JSON array with ParkingReceipts
     * @throws VehicleDoesNotExistException thrown if Vehicle does not exist
     */
    private void parseParkingReceipts(JSONArray ja) throws VehicleDoesNotExistException {
        for (Object o : ja) {
            JSONObject tempParkingReceiptObject = (JSONObject) o;

            // Get receipt info
            int tempId = Integer.parseInt(tempParkingReceiptObject.get("id").toString());
            Date tempStartDate = new Date(Long.parseLong(tempParkingReceiptObject.get("dateStart").toString()));
            boolean tempIsDisabled = Boolean.parseBoolean(tempParkingReceiptObject.get("isDisabled").toString());
            ParkingReceipt tempParkReceipt = new ParkingReceipt(tempId, tempStartDate, tempIsDisabled);

            // Find the Vehicle that corresponds to this ParkingReceipt
            Vehicle tempVehicle = null;
            for (ParkingZone pz : parkingZones) {
                for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                    if (ps.getVehicle() != null) {
                        if (ps.getVehicle().getTempParkingReceiptId() == tempId) {
                            // Get the Vehicle
                            tempVehicle = ps.getVehicle();
                            // Set the price per hour of parking to the ParkingReceipt
                            tempParkReceipt.setPricePerHour(pz.getPrice());
                            break;
                        }
                    }
                }
            }

            // Check if the Vehicle was found
            if (tempVehicle != null) {
                tempVehicle.setParkingReceipt(tempParkReceipt);
            } else {
                throw new VehicleDoesNotExistException("The vehicle that this parking receipt " + "is assigned to does not exist!");
            }
        }
    }

    /**
     * Search for a ParkingSpace by its ID and returns it.
     *
     * @param id ParkingSpace ID (i.e. "A12" or "C4")
     * @return a ParkingSpace object or null if not found
     */
    private ParkingSpace getParkingSpaceById(String id) {
        for (ParkingZone pz : parkingZones) {
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                if (ps.getID().equals(id)) {
                    return ps;
                }
            }
        }
        // If the parking space cannot be found, return null
        return null;
    }

    /**
     * Imports all ExitTokens from JSON database.
     *
     * @param ja JSON array with ExitTokens
     */
    private void parseExitTokens(JSONArray ja) {
        for (Object o : ja) {
            JSONObject tempJsonExitToken = (JSONObject) o;

            // Get ExitToken's ID and date
            int tempExitTokenId = Integer.parseInt(tempJsonExitToken.get("id").toString());
            Date tempExitTokenDate = new Date(Long.parseLong(tempJsonExitToken.get("date").toString()));

            // Create an ExitToken and add it to the array
            ExitToken tempExitToken = new ExitToken(tempExitTokenId, tempExitTokenDate);
            exitTokens.add(tempExitToken);
        }
    }

    /**
     * Returns content of the provided file as string.
     *
     * @param Filename File name
     * @return File content
     * @throws FileNotFoundException Is thrown if file doesn't exist.
     */
    private String getContent(String Filename) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        File file = new File(Filename);
        System.out.println(file.getAbsolutePath());
        if (!file.exists()){
           boolean success = file.createNewFile();
           if (!success) throw new FileNotFoundException();
           FileWriter writer = new FileWriter(file);
           writer.write(createStructure().toJSONString());
           writer.flush();
           writer.close(); }


        Scanner infile = new Scanner(file);
        while (infile.hasNextLine()) {
            sb.append(infile.nextLine());
        }
        return sb.toString();
    }
    private JSONObject createStructure(){
        JSONObject root = new JSONObject();
        root.put("name" , "");
        root.put("employees" , new JSONArray());
        root.put("parkingZones" , new JSONArray());
        root.put("vehicles" , new JSONArray());
        root.put("parkingReceipts" , new JSONArray());
        root.put("exitTokens" , new JSONArray());

        return root;
    }

    /**
     * Returns all info about the parking.
     *
     * @return everything about the parking
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);

        // Display ParkingZones
        sb.append("\n\n---------- PARKING ZONES ----------\n");
        if (parkingZones.size() > 0) {
            sb.append("Has ");
            sb.append(parkingZones.size());
            sb.append((parkingZones.size() == 1) ? " parking zone:\n" : " parking zones:\n");
            for (ParkingZone pz : parkingZones) {
                sb.append(pz.toString());
                sb.append("\n\n");
            }
        } else {
            sb.append("Doesn't have any parking zones.");
        }

        // Display Vehicles
        sb.append("\n\n---------- VEHICLES ----------");
        ArrayList<Vehicle> vehicles = new ArrayList<>();

        // Get all Vehicles into the array
        for (ParkingZone pz : parkingZones) {
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                if (!ps.isFree()) {
                    vehicles.add(ps.getVehicle());
                }
            }
        }

        if (vehicles.size() > 0) {
            sb.append((vehicles.size() == 1) ? "There is " : "There are ");
            sb.append(vehicles.size());
            sb.append((vehicles.size() == 1) ? " vehicle parked in the parking:" : " vehicles parked in the parking:");
            for (Vehicle v : vehicles) {
                sb.append(v.toString());
                sb.append("\n");
            }
        } else {
            sb.append("\nThere are no vehicles parked in this parking.");
        }

        // Display FreeEmployees
        sb.append("\n\n---------- FREE EMPLOYEES ----------\n");

        if (freeEmployees.size() > 0) {
            sb.append((freeEmployees.size() == 1) ? "There is currently " : "There are currently ");
            sb.append(freeEmployees.size());
            sb.append((freeEmployees.size() == 1) ? " free employee:\n" : " free employees:\n");
            for (FreeEmployee fe : freeEmployees) {
                sb.append(fe.toString());
                sb.append("\n");
            }
        } else {
            sb.append("There are currently no free employees.");
        }

        // Display ExitTokens
        sb.append("\n\n---------- EXIT TOKENS ----------\n");

        if (exitTokens.size() > 0) {
            sb.append((exitTokens.size() == 1) ? "There is currently " : "There are currently ");
            sb.append(exitTokens.size());
            sb.append((exitTokens.size() == 1) ? " exit token:\n" : " exit tokens:\n");
            for (ExitToken et : exitTokens) {
                sb.append(et.toString());
                sb.append("\n");
            }
        } else {
            sb.append("There are currently no exit tokens.");
        }

        return sb.toString();
    }

    /**
     * Adds a ParkingZone to the list of zones.
     *
     * @param pz ParkingZone object to be added
     */
    private void addParkingZone(ParkingZone pz) {
        parkingZones.add(pz);
    }

    /**
     * Removes a ParkingZone from the list.
     *
     * @param pz ParkingZone object to remove from the list
     * @throws ParkingZoneDoesNotExistException thrown if ParkingZone is not in the list
     */
    private void removeParkingZone(ParkingZone pz) throws ParkingZoneDoesNotExistException {
        if (parkingZones.contains(pz)) {
            parkingZones.remove(pz);
        } else {
            throw new ParkingZoneDoesNotExistException();
        }
    }

    /**
     * Used to get Vehicle's height and length.
     *
     * @param dimensionName name of the dimension (either "length" or "height")
     * @return floating-point number value of the dimension
     */
    private float getVehicleDimension(String dimensionName) {
        String response;
        float dimension = -1.0f; // If it's still -1.0 -- it's invalid

        if (dimensionName.toUpperCase().equals("LENGTH") || dimensionName.toUpperCase().equals("HEIGHT")) {
            dimensionName = dimensionName.toLowerCase();
            // Get vehicle dimensions
            boolean isResponseValid = false;

            do {
                System.out.print("What's your vehicle's " + dimensionName + " (in metres)? ");
                response = in.nextLine();
                try {
                    dimension = Float.parseFloat(response);
                    isResponseValid = true;
                } catch (NumberFormatException e) {
                    // Catch illegal float value
                    System.err.println("Invalid value (" + response + ")!\n" + e.getMessage());
                } finally {
                    if (response.isEmpty()) {
                        isResponseValid = false;
                    }
                }
            } while (!isResponseValid);
        }
        else {
            throw new IllegalArgumentException("The specified argument " + dimensionName + " is not valid!" +
                    "\nAccepted values are: \"LENGTH\" and \"HEIGHT\"");
        }
        return dimension;
    }

    /**
     * Adds a Vehicle.
     *
     * @throws VehicleSizeNotSupportedException thrown if Vehicle's size is not supported
     * @throws VehicleAlreadyExistsException thrown if there is already a Vehicle with the same license plate
     */
    void addVehicle() throws VehicleSizeNotSupportedException, VehicleAlreadyExistsException {
        String response, licensePlate;
        boolean isMotorbike, isCoach = false, isDriverDisabled, needsAssistance = false;
        float height, length;
        Vehicle newVehicle;
        ParkingReceipt newParkingReceipt;

        // Get license plate number
        do {
            System.out.print("What's your license plate? ");
            response = in.nextLine().toUpperCase();
        } while (response.isEmpty());
        licensePlate = response;

        // Check if there's a Vehicle with the same license plate already parked
        for (ParkingZone pz : parkingZones) {
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                if (!ps.isFree()) {
                    if (ps.getVehicle().getLicensePlate().equals(licensePlate)) {
                        throw new VehicleAlreadyExistsException();
                    }
                }
            }
        }

        // Is it a motorbike?
        do {
            System.out.print("Is your vehicle a motorbike? [y/n] ");
            response = in.nextLine().toUpperCase();
        } while (! (response.equals("Y") || response.equals("N")));
        isMotorbike = response.equals("Y");

        // Is it a coach?
        if (!isMotorbike) {
            do {
                System.out.print("Is your vehicle a coach? [y/n] ");
                response = in.nextLine().toUpperCase();
            } while (! (response.equals("Y") || response.equals("N")));
            isCoach = response.equals("Y");
        }

        // Get vehicle dimensions
        height = getVehicleDimension("HEIGHT");
        length = getVehicleDimension("LENGTH");

        // Check if a coach doesn't exceed the 15 metres length limit
        if (isCoach) {
            if (length > 15.0f) {
                throw new VehicleSizeNotSupportedException();
            }
        }

        // Is the driver disabled?
        do {
            System.out.print("Are you disabled? [y/n] ");
            response = in.nextLine().toUpperCase();
        } while (! (response.equals("Y") || response.equals("N")));
        isDriverDisabled = response.equals("Y");

        // Create the Vehicle object
        if (isCoach) {
            newVehicle = new Vehicle(licensePlate, height, length, VehicleType.COACH);
        } else if (isMotorbike) {
            newVehicle = new Vehicle(licensePlate, height, length, VehicleType.MOTORBIKE);
        } else {
            newVehicle = new Vehicle(licensePlate, height, length);
        }

        // Get the Vehicle's type
        VehicleType vehicleType = newVehicle.getVehicleType();

        // Does user need assistance parking their vehicle?
        // (available only if the vehicle is neither a coach nor a motorbike
        if (!isCoach && !isMotorbike) {
            do {
                System.out.print("Do you need assistance parking your vehicle? [y/n] ");
                response = in.nextLine().toUpperCase();
            } while (!(response.equals("Y") || response.equals("N")));
            needsAssistance = response.equals("Y");
        }

        // Generate a ParkingReceipt and assign it to the Vehicle
        newParkingReceipt = new ParkingReceipt(getNextReceiptId(), new Date(), isDriverDisabled);
        newVehicle.setParkingReceipt(newParkingReceipt);

        boolean parked = false; // changes to true when vehicle has been successfully parked
        do {
            if (needsAssistance) {
                // Check if there are any free employees
                if (freeEmployees.size() <= 0) {
                    System.out.println("Sorry, all employees are busy at the moment." +
                            "\nYou need to park the vehicle yourself.");
                    // Go to "park yourself" mode
                    needsAssistance = false;
                } else {
                    // Vehicle is parked by a random employee
                    System.out.println("Your vehicle will be parked by an employee shortly!" +
                            "\nYour parking receipt number is: " + newParkingReceipt.getId());

                    // ------------------------------------------------------------------------------ //
                    System.out.println("----- From this point, everything would be displayed to an Employee, not a Customer ---");
                    // ------------------------------------------------------------------------------ //

                    // Get a random FreeEmployee and convert them into DriverEmployee
                    DriverEmployee driverEmployee = convertFreeEmployeeIntoDriverEmployee(
                            freeEmployees.get(rand.nextInt(freeEmployees.size())));

                    System.out.println("Welcome, " + driverEmployee.getName() + "!" +
                            "\nYou've got a vehicle to park.");

                    do {
                        System.out.print("Do you want to park it in a specific parking space? [y/n] ");
                        response = in.nextLine().toUpperCase();
                    } while (! (response.equals("Y") || (response.equals("N"))));
                    boolean isRoamingVehicle = response.equals("Y");

                    if (isRoamingVehicle) {
                        // Employee is roaming the Vehicle to a specified ParkingSpace
                        boolean isSelectedParkingSpaceFree = false;
                        ParkingSpace parkingSpace = null;
                        do {
                            System.out.print("Enter parking space ID: ");
                            response = in.nextLine().toUpperCase();

                            // Check if the ParkingSpace is free
                            for (ParkingZone pz : parkingZones) {
                                for (ParkingSpace ps : pz.getListOfFreeParkingSpaces()) {
                                    if (ps.getID().equals(response)) {
                                        // Check if the ParkingSpace can accept the Vehicle
                                        if (pz.isVehicleAccepted(vehicleType)) {
                                            // Check if the ParkingSpace is occupied
                                            if (ps.isFree()) {
                                                // Get the ParkingSpace
                                                parkingSpace = ps;
                                                isSelectedParkingSpaceFree = true;
                                            } else {
                                                System.out.println("This parking space is already occupied!");
                                            }
                                        } else {
                                            System.out.println("This parking zone cannot support this type of vehicle.");
                                        }
                                    }
                                }
                            }
                        } while (!isSelectedParkingSpaceFree);

                        // Park the Vehicle in the found, free ParkingSpace
                        try {
                            newVehicle.parkIn(parkingSpace);
                            parked = true;
                        } catch (ParkingSpaceOccupiedException e) {
                            // This should never be thrown, as getListOfFreeParkingSpaces()
                            // should return only free ParkingSpaces
                            System.err.println(e.getMessage());
                        }

                    } else {
                        try {
                            // Employee is parking the Vehicle in a random ParkingSpace
                            ParkingSpace randomParkingSpace = getRandomParkingSpace(newVehicle);
                            System.out.println("Park the vehicle in parking space " + randomParkingSpace.getID());
                            parked = parkVehicle(newVehicle, randomParkingSpace, newParkingReceipt);
                        } catch (NoFreeParkingSpacesException e) {
                            System.err.println(e.getMessage());
                            break;
                        }
                    }

                    // Convert the DriverEmployee back into FreeEmployee
                    FreeEmployee tempFreeEmployee = convertDriverEmployeeIntoFreeEmployee(driverEmployee);
                    freeEmployees.add(tempFreeEmployee);
                }
            } else {
                try {
                    // Get a random ParkingSpace that can accept the Vehicle and try to park the Vehicle in it
                    ParkingSpace randomParkingSpace = getRandomParkingSpace(newVehicle);
                    System.out.println("Park your vehicle in parking space " + randomParkingSpace.getID());
                    parked = parkVehicle(newVehicle, randomParkingSpace, newParkingReceipt);
                } catch (NoFreeParkingSpacesException e) {
                    System.err.println(e.getMessage());
                    break;
                }
            }
        } while (!parked);
    }

    /**
     * Returns a random ParkingSpace that can accept the Vehicle provided.
     * It checks if the ParkingSpace is both free and can accept the type of Vehicle.
     *
     * @param v Vehicle to be parked
     * @return a free random ParkingSpace
     * @throws NoFreeParkingSpacesException thrown if there are no free ParkingSpaces in the ParkingZone
     */
    private ParkingSpace getRandomParkingSpace(Vehicle v) throws NoFreeParkingSpacesException {
        ArrayList<ParkingZone> tempParkingZones = new ArrayList<>();
        VehicleType vt = v.getVehicleType();

        // Get all ParkingZones that will accept the Vehicle
        for (ParkingZone pz : parkingZones) {
            if (pz.isVehicleAccepted(vt)) {
                tempParkingZones.add(pz);
            }
        }

        if (tempParkingZones.size() != 0) {
            // Get a random ParkingZone from the list
            ParkingZone randomParkingZone = tempParkingZones.get(rand.nextInt(tempParkingZones.size()));

            // Find a random ParkingSpace in the random ParkingZone
            int freeParkingSpacesCount = randomParkingZone.getListOfFreeParkingSpaces().size();

            try {
                int randomParkingSpaceId = rand.nextInt(freeParkingSpacesCount);
                return randomParkingZone.getListOfFreeParkingSpaces()
                        .get(randomParkingSpaceId);
            } catch (IllegalArgumentException e) {
                // Thrown if there are no free ParkingSpaces in the ParkingZone
                throw new NoFreeParkingSpacesException("There are no free parking spaces! Try again later.");
            }
        }
        else {
            throw new NoFreeParkingSpacesException("There are no free parking spaces! Try again later.");
        }
    }

    /**
     * Parks a Vehicle in the ParkingSpace given.
     * This method does NOT check if the Vehicle can be parked in
     * the ParkingSpace provided, since it's called after using
     * getRandomParkingSpace() method and should NOT be called manually.
     *
     * @param v Vehicle to be parked
     * @param ps ParkingSpace to be parked in
     * @param pr ParkingReceipt assigned to the Vehicle
     * @return TRUE if Parking was successful, FALSE if an error occurred
     */
    private boolean parkVehicle(Vehicle v, ParkingSpace ps, ParkingReceipt pr) {
        // As this is a demo, even in "manual parking" mode
        // the Vehicle is parked automatically.

        // Try to park the Vehicle in the random ParkingSpace
        try {
            v.parkIn(ps);
            System.out.println("You parked your vehicle!" +
                    "\nYour parking receipt number is: " + pr.getId());
            return true;
        } catch (ParkingSpaceOccupiedException e) {
            // This should never be thrown, as the getListOfFreeParkingSpaces()
            // method returns only free ParkingSpaces.
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Generates a unique, free ExitToken.
     *
     * @return unique token
     */
    private int generateExitToken() {
        boolean isFree = true;
        int tempToken;

        // Keep generating the token as long, as the generated one will be free
        do {
            // Generate a random 4-digits-long number
            tempToken = rand.nextInt(9000) + 1000;

            // Check if the token already exists
            for (ExitToken et : exitTokens) {
                if (et.getId() == tempToken) {
                    isFree = false;
                    break;
                }
            }
        } while (!isFree);

        return tempToken;
    }

    /**
     * Collects a Vehicle.
     *
     * @throws VehicleDoesNotExistException thrown if Vehicle doesn't exist
     */
    void collectVehicle() throws VehicleDoesNotExistException {

        boolean isValid;
        String response;
        int tempReceiptId = -1;

        // Get the ParkingReceipt number
        do {
            System.out.print("Enter your parking receipt number: ");
            response = in.nextLine();
            try {
                tempReceiptId = Integer.parseInt(response);
                isValid = true;
            } catch (NumberFormatException e) {
                System.err.println("This is not a valid number!");
                isValid = false;
            } finally {
                if (response.isEmpty()) {
                    isValid = false;
                }
            }
        } while (!isValid);


        Vehicle tempVehicle = null;
        ParkingSpace tempParkingSpace = null;

        // Search for the Vehicle
        for (ParkingZone pz : parkingZones ) {
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                if (!ps.isFree()) {
                    int receiptId = ps.getVehicle().getParkingReceipt().getId();
                    if (receiptId == tempReceiptId) {
                        tempVehicle = ps.getVehicle();
                        tempParkingSpace = ps;
                        ps.getVehicle().getParkingReceipt().setPricePerHour(pz.getPrice());
                        break;
                    }
                }
            }
        }

        // If the Vehicle cannot be found, throw an exception
        if (tempVehicle == null) {
            throw new VehicleDoesNotExistException("The vehicle assigned to this parking receipt doesn't exist!" +
                    "\nContact parking staff.");
        } else {
            // Set the endDate to "now" and calculate price etc.
            Date endDate = new Date();
            try {
                tempVehicle.getParkingReceipt().setEndDate(endDate);
            } catch (EndDateBeforeStartDateException e) {
                System.err.println(e.getMessage());
            }

            // Get the price to pay
            float priceToPay = tempVehicle.getParkingReceipt().getPrice();

            // If the Driver is disabled -- halve the price (except for coach Drivers)
            boolean tempIsDisabled = tempVehicle.getParkingReceipt().isOwnerDisabled();
            boolean tempIsCoachDriver = tempVehicle.getVehicleType().toString().equals("COACH");
            if (tempIsDisabled && !tempIsCoachDriver) {
                priceToPay /= 2;
            }

            // If the Driver is disabled and it's Sunday -- set priceToPay to 0.00
            SimpleDateFormat sdf = new SimpleDateFormat("u");

            if (Integer.parseInt(sdf.format(tempVehicle.getParkingReceipt().getStartDate())) == 7 && Integer.parseInt(sdf.format(endDate)) == 7 && tempIsDisabled) {
                priceToPay = 0.00f;
            }

            Date startDate = tempVehicle.getParkingReceipt().getStartDate();

            // Calculate the time parked and convert it to user-friendly format
            long timeParked = endDate.getTime() - startDate.getTime();
            long secondsParked = timeParked / 1000;
            long minutesParked = secondsParked / 60;
            long hoursParked = minutesParked / 60;
            minutesParked %= 60;

            // Display info to the Customer
            System.out.print("You've been parked for ");
            if (hoursParked > 0) {
                System.out.print(hoursParked + " hours ");
            }
            if (minutesParked >= 0) {
                System.out.print(minutesParked + " min ");
            }

            // Get the Customer to pay
            System.out.println("and your payment is: " + priceToPay + " units.");
            float change = payment(priceToPay);

            // If there is a change -- give it back to the Customer
            if (change != 0.00f) {
                System.out.println("Your change is " + change + " units.");
            }

            // Generate the ExitToken and add it to the array
            ExitToken exitToken = new ExitToken(generateExitToken(), new Date());
            exitTokens.add(exitToken);

            // Ask the Customer if they need assistance collecting their Vehicle (not coaches and motorbikes)
            String tempVehicleType = tempVehicle.getVehicleType().toString().toUpperCase();
            if (! (tempVehicleType.equals("MOTORBIKE") || tempVehicleType.equals("COACH"))) {
                boolean needsAssistance;
                do {
                    System.out.print("Do you need assistance collecting your vehicle? [y/n] ");
                    response = in.nextLine().toUpperCase();
                } while (! (response.equals("Y") || response.equals("N")));
                needsAssistance = response.equals("Y");

                if (needsAssistance) {
                    // Check if there are any free employees
                    if (freeEmployees.size() <= 0) {
                        // There are no FreeEmployees
                        System.out.println("Sorry, all employees are busy at the moment." +
                                "\nYou need to collect the vehicle yourself.");
                        // Go to "collect yourself" mode
                        needsAssistance = false;
                    } else {
                        // Vehicle collected by an Employee
                        System.out.println("Your vehicle will be collected by an employee shortly!");

                        // ------------------------------------------------------------------------------ //
                        System.out.println("----- From this point, everything would be displayed to an Employee, not a Customer ---");
                        // ------------------------------------------------------------------------------ //

                        // Get a random FreeEmployee and convert them into DriverEmployee
                        DriverEmployee driverEmployee = convertFreeEmployeeIntoDriverEmployee(
                                freeEmployees.get(rand.nextInt(freeEmployees.size())));

                        System.out.println("Welcome, " + driverEmployee.getName() + "!" +
                                "\nYou've got a vehicle to collect." +
                                "\nThe vehicle is parked in the parking space " + tempParkingSpace.getID() +
                                "\nThe exit token is: " + exitToken.getId());

                        // As this is a demo, everything happens automatically
                        System.out.println("Thanks for delivering the Vehicle to the Customer. Your job here is done!");

                        // Convert the DriverEmployee back into FreeEmployee
                        FreeEmployee tempFreeEmployee = convertDriverEmployeeIntoFreeEmployee(driverEmployee);
                        freeEmployees.add(tempFreeEmployee);

                    }
                } else {
                    // Vehicle collected by the Customer
                    // Remind the Customer where the Vehicle is
                    System.out.println("Your vehicle is parked in the parking space " + tempParkingSpace.getID());

                    // Collect the Vehicle and generate ExitToken
                    System.out.println("You can now exit the car parking with your vehicle!" +
                            "\nHead towards the exit barrier. Your exit token is: " + exitToken.getId());

                    // Remove the Vehicle from the Parking (also removes the ParkingReceipt)
                    tempParkingSpace.setVehicle(null);
                }
            } else {

                // If the Vehicle is neither a motorbike nor a coach, the Customer has to collect it themself
                // Remind the Customer where the Vehicle is
                System.out.println("Your vehicle is parked in the parking space " + tempParkingSpace.getID());

                // Collect the Vehicle and generate ExitToken
                System.out.println("You can now exit the car parking with your vehicle!" +
                        "\nHead towards the exit barrier. Your exit token is: " + exitToken.getId());

                // Remove the Vehicle from the Parking (also removes the ParkingReceipt)
                tempParkingSpace.setVehicle(null);
            }
        }
    }

    /**
     * The "parking machine" that shows a Customer the price and accepts payments.
     * Returns the amount of change.
     *
     * @param priceToPay price to be paid for parking
     * @return change to be given to the Customer
     */
    private float payment(float priceToPay) {

        boolean paid = false;
        String response;

        if(priceToPay == 0.0f) {
            paid = true;
        }
        while(!paid) {
            System.out.println("Still to pay: " + priceToPay + " units");
            System.out.println("You can insert: 20.00, 10.00, 5.00, 2.00, 1.00, 0.50, 0.20, 0.10");
            System.out.print("Insert coin: ");
            response = in.nextLine();
            float amountPaid = Float.parseFloat(response);

            // Detect and filter invalid responses
            if (amountPaid == 20.00f || amountPaid == 10.00f || amountPaid == 5.00f || amountPaid == 2.00f
                    || amountPaid == 1.00f || amountPaid == 0.50f || amountPaid == 0.20f || amountPaid == 0.10f) {

                if (priceToPay <= amountPaid) {
                    paid = true;
                    return (amountPaid - priceToPay);
                } else {
                    priceToPay -= amountPaid;
                }
            } else {
                System.out.println("That's not a valid coin!");
            }
        }

        return 0.00f;
    }

    /**
     * Prints info about how many free ParkingSpaces are available in each ParkingZone
     */
    void getNumberOfFreeSpaces() {
        System.out.println("Number of free parking spaces:");
        for (ParkingZone pz : parkingZones) {
            int freeSpaces = 0;
            for (ParkingSpace ps : pz.getListOfFreeParkingSpaces()) {
                freeSpaces++;
            }
            System.out.println("Zone " + pz.getId() + " -- " + freeSpaces);
        }
    }

    /**
     * Converts the FreeEmployee into DriverEmployee, adds the new DriverEmployee to the list
     * and removes the FreeEmployee from the list.
     *
     * @param fe FreeEmployee to be converted
     * @return the Employee converted into DriverEmployee
     */
    private DriverEmployee convertFreeEmployeeIntoDriverEmployee(FreeEmployee fe) {
        DriverEmployee de = new DriverEmployee(fe);
        freeEmployees.remove(fe);
        driverEmployees.add(de);
        return de;
    }

    /**
     * Converts the DriverEmployee into FreeEmployee, adds the new FreeEmployee to the list
     * and removes the DriverEmployee from the list.
     *
     * @param de DriverEmployee to be converted
     * @return the Employee converted into FreeEmployee
     */
    private FreeEmployee convertDriverEmployeeIntoFreeEmployee(DriverEmployee de) {
        FreeEmployee fe = new FreeEmployee(de);
        driverEmployees.remove(de);
        freeEmployees.add(fe);
        return fe;
    }

    /**
     * Allows an Employee to see Parking statistics.
     * Displays info such as list of ParkingZones and details about all ParkingSpaces inside them.
     */
    void employeeSeeStatistics() {
        System.out.println("----- Parking statistics: -----");
        System.out.println(this.name);
        for (ParkingZone pz : parkingZones) {
            System.out.println("Parking zone " + pz.getId());
            System.out.println("(has " + pz.getListOfParkingSpaces().size() + " parking spaces)");
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                System.out.println(ps.getID() + " -- " + (ps.isFree() ? "free" : "occupied"));
            }
            System.out.println("\n\n");
        }
    }

    /**
     * Allows an Employee to remove a Vehicle from the Parking.
     *
     * @throws VehicleDoesNotExistException thrown if the Vehicle doesn't exist.
     */
    void employeeRemoveVehicle() throws VehicleDoesNotExistException {
        System.out.print("Enter vehicle's license plate: ");
        String response = in.nextLine().toUpperCase();

        Vehicle tempVehicle = null;

        // Search for the Vehicle
        for (ParkingZone pz : parkingZones) {
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                if (!ps.isFree()) {
                    // Compare Vehicles' license plates
                    if (ps.getVehicle().getLicensePlate().equals(response)) {
                        tempVehicle = ps.getVehicle();
                        // Remove the Vehicle from the Parking
                        ps.setVehicle(null);
                        System.out.println("The vehicle has been removed!");
                        break;
                    }
                }
            }
        }

        // Check if the Vehicle was found and removed
        if (tempVehicle == null) {
            throw new VehicleDoesNotExistException("The vehicle does not exist!");
        }
    }

    /**
     * Gets info about a Vehicle (as an Employee).
     *
     * @throws VehicleDoesNotExistException thrown if Vehicle doesn't exist.
     */
    void employeeGetVehicleInfo() throws VehicleDoesNotExistException {
        String response;

        // Get Vehicle's license plate
        do {
            System.out.print("Enter vehicle's license plate: ");
            response = in.nextLine().toUpperCase();
        } while (response.isEmpty());

        Vehicle tempVehicle = null;

        // Search for the Vehicle
        for (ParkingZone pz : parkingZones) {
            for (ParkingSpace ps : pz.getListOfParkingSpaces()) {
                if (!ps.isFree()) {
                    // Compare Vehicles' license plates
                    if (ps.getVehicle().getLicensePlate().equals(response)) {
                        tempVehicle = ps.getVehicle();

                        // Display all info about the Vehicle
                        System.out.println(tempVehicle.toString());
                        break;
                    }
                }
            }
        }

        if (tempVehicle == null) {
            throw new VehicleDoesNotExistException("The vehicle does not exist!");
        }
    }

    /**
     * Exists the Parking (through the "barrier" with the token).
     *
     * @throws TokenExpiredException thrown if the ExitToken has expired (default 15 mins).
     */
    void exitParking() throws TokenExpiredException {
        String response;
        boolean isValidResponse;
        int exitToken;

        // Get barrier's token from the Customer
        do {
            System.out.print("Enter your exit token: ");
            response = in.nextLine().toUpperCase();

            try {
                exitToken = Integer.parseInt(response);
                isValidResponse = true;
            } finally {
                if (response.isEmpty()) {
                    isValidResponse = false;
                }
            }

        } while (!isValidResponse);

        boolean doesTokenExist = false;
        // Search for the ExitToken
        for (ExitToken et : exitTokens) {
            if (et.getId() == exitToken) {

                // Check if the token is still valid
                Date currentDate = new Date();
                if (et.canExit(currentDate)) {
                    // the Customer did it in under 15 minutes, they can exit
                    exitTokens.remove(et);
                    System.out.println("Thank you for choosing our Parking! Have a great day.");
                    // <------ The end of "Parking cycle" ------> //
                } else {
                    // 15 minutes has passed, the Customer cannot exit
                    throw new TokenExpiredException();
                }
                doesTokenExist = true;
                break;
            }
        }

        if (!doesTokenExist) {
            System.out.println("This token is invalid! Try again.");
        }
    }

    /**
     * Adds a new Employee (FreeEmployee) to the Parking.
     */
    void employeeAddEmployee() {
        String response;

        do {
            System.out.print("Enter new employee's name: ");
            response = in.nextLine();
        } while (response.isEmpty());

        // Get a unique Employee ID
        int tempId = getNextEmployeeId();

        // Create the FreeEmployee class and add it to the array
        FreeEmployee tempFreeEmployee = new FreeEmployee(tempId, response);
        freeEmployees.add(tempFreeEmployee);

        System.out.println("The employee " + response + " has been added as employee ID " + tempId);

    }

    /**
     * Removes an Employee from the Parking.
     *
     * @throws EmployeeDoesNotExistException thrown if the Employee doesn't exist.
     */
    void employeeRemoveEmployee() throws EmployeeDoesNotExistException {
        String response;
        boolean isValid;
        int tempEmployeeId = -1;

        do {
            System.out.print("Enter employee's ID: ");
            response = in.nextLine();
            try {
                tempEmployeeId = Integer.parseInt(response);
                isValid = true;
            } catch (NumberFormatException e) {
                System.err.println("It's not a valid number!");
                isValid = false;
            } finally {
                if (response.isEmpty()) {
                    isValid = false;
                }
            }
        } while (!isValid);

        FreeEmployee freeEmployee = null;

        // Check if the Employee exists
        for (FreeEmployee fe : freeEmployees) {
            if (fe.getId() == tempEmployeeId) {
                freeEmployee = fe;
                freeEmployees.remove(freeEmployee);
                System.out.println("Removed employee " + freeEmployee.getName() + " (" + freeEmployee.getId() + ")");
                break;
            }
        }

        if (freeEmployee == null) {
            throw new EmployeeDoesNotExistException();
        }
    }

}
