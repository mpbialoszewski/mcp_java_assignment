package cymru.mab152.mcp;
import cymru.mab152.mcp.exception.ParkingSpaceOccupiedException;
import cymru.mab152.mcp.exception.VehicleSizeNotSupportedException;

/**
 * A Vehicle is an object created when a Customer registers it.
 * It has a license plate, a type, dimensions and an assigned ParkingReceipt.
 */
public class Vehicle {

    private String licensePlate;
    private VehicleType type;
    private float height, length;
    private ParkingReceipt parkingReceipt; // null if not assigned

    // This is NOT an ID of the ParkingReceipt! It's used when loading the Vehicle
    // from the database to connect the Vehicle and the ParkingReceipt together.
    // To get the ParkingReceipt's ID use getParkingReceipt().getId() method!
    private int tempParkingReceiptId = -1;

    /**
     * The constructor for Vehicle objects.
     * It's used when adding existing Vehicles from JSON database.
     *
     * @param lp vehicle's license plate
     * @param h vehicle's height in metres
     * @param l vehicle's length in metres
     * @param vt vehicle type (enum)
     */
    Vehicle(String lp, float h, float l, VehicleType vt) {
        this.licensePlate = lp;
        this.type = vt;
        this.height = h;
        this.length = l;
        this.parkingReceipt = null;
    }

    /**
     * The constructor for Vehicle objects.
     * It's used when a Driver parks a new Vehicle.
     *
     * @param lp   vehicle's license plate
     * @param h    vehicle's height in metres
     * @param l    vehicle's length in metres
     */
    Vehicle(String lp, float h, float l) {
        this.licensePlate = lp;
        this.height = h;
        this.length = l;

        // Calculate the size from vehicle's dimensions.
        try {
            this.type = checkType(h, l);
        } catch (VehicleSizeNotSupportedException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Calculates and returns the VehicleType depending on vehicle's dimensions.<br>
     * <b>VehicleType.STANDARD</b> -- height up to 2 metres and length up to 5 metres.<br>
     * <b>VehicleType.HIGHER</b> -- height between 2 and 3 metres and length up to 5 metres.<br>
     * <b>VehicleType.LONGER</b> -- height up to 3 metres and length between 5.1 and 6 metres.<br>
     *
     * @param height vehicle's height in metres
     * @param length vehicle's length in metres
     * @return VehicleType (enum) calculated by vehicle's size
     * @throws VehicleSizeNotSupportedException thrown if the vehicle can't be fitted into any type
     */
    private VehicleType checkType(float height, float length) throws VehicleSizeNotSupportedException {
        // Size cannot be negative or equal to 0
        if (height <= 0.0F || length <= 0.0F) {
            throw new VehicleSizeNotSupportedException();
        }

        if (height < 2.0F && length < 5.0F) {
            return VehicleType.STANDARD;
        } else if (height >= 2.0F && height < 3.0F && length < 5.0F) {
            return VehicleType.HIGHER;
        } else if (height < 3.0F && length >= 5.1F && length < 6.0F) {
            return VehicleType.LONGER;
        } else {
            throw new VehicleSizeNotSupportedException();
        }
    }

    /**
     * Returns vehicle's license plate.
     *
     * @return vehicle's license plate
     */
    String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Sets vehicle's license plate to the string provided.
     *
     * @param lp becomes new license plate number
     */
    void setLicensePlate(String lp) {
        licensePlate = lp;
    }

    /**
     * Returns all info about the vehicle as a string.
     *
     * @return all info about the vehicle
     */
    public String toString() {
        StringBuilder Vehicle_sb = new StringBuilder();

        // Basic Vehicle info

        Vehicle_sb.append("A vehicle (license plate ");
        Vehicle_sb.append(this.licensePlate);
        Vehicle_sb.append(").\nIts parkingReceipt number is ");
        Vehicle_sb.append(this.parkingReceipt.getId());
        Vehicle_sb.append(".\nThe vehicle type is ");
        Vehicle_sb.append(this.type.toString());

        // Dimensions
        Vehicle_sb.append(" and its dimensions are ");
        Vehicle_sb.append(this.length);
        Vehicle_sb.append(" metres in length and ");
        Vehicle_sb.append(this.height);
        Vehicle_sb.append(" metres in height.");

        return Vehicle_sb.toString();
    }

    /**
     * Parks the Vehicle in the ParkingSpace specified.
     * Throws an exception if the ParkingSpace is already occupied.
     *
     * @param ps the ParkingSpace you want to park the Vehicle in
     * @throws ParkingSpaceOccupiedException thrown if the ParkingSpace is already occupied
     */
    void parkIn(ParkingSpace ps) throws ParkingSpaceOccupiedException {
        if (ps.isFree()) {
            ps.setVehicle(this);
        } else {
            throw new ParkingSpaceOccupiedException();
        }
    }

    /**
     * Returns a ParkingReceipt of the Vehicle.
     *
     * @return parking receipt object
     */
    ParkingReceipt getParkingReceipt() {
        return this.parkingReceipt;
    }

    /**
     * Assigns a ParkingReceipt to the Vehicle.
     *
     * @param pr ParkingReceipt to be assigned.
     */
    void setParkingReceipt(ParkingReceipt pr) {
        this.parkingReceipt = pr;
    }

    /**
     * Returns Vehicle's type.
     *
     * @return vehicle type (enum)
     */
    VehicleType getVehicleType() {
        return this.type;
    }

    /**
     * Returns the temp ParkingReceipt ID.
     * The tempParkingReceiptId is <b>NOT</b> the ParkingReceipt's ID!
     * It's used only while parsing the Vehicles from the JSON database.
     *
     * To get a ParkingReceipt's ID, use getParkingReceipt().getId() method.
     *
     * @return ParkingReceipt ID
     */
    int getTempParkingReceiptId() {
        return this.tempParkingReceiptId;
    }

    /**
     * Sets the temp ParkingReceipt ID to a given integer.
     * The tempParkingReceiptId is <b>NOT</b> the ParkingReceipt's ID!
     * It's used only while parsing the Vehicles from the JSON database.
     * <p>
     * To assign a ParkingReceipt to a Vehicle, use setParkingReceipt() method.
     *
     * @param id temp ParkingReceipt id
     */
    void setTempParkingReceiptId(int id) {
        this.tempParkingReceiptId = id;
    }

    /**
     * Returns Vehicle's height in metres.
     *
     * @return Vehicle's height
     */
    float getHeight() {
        return this.height;
    }

    /**
     * Returns Vehicle's length in metres.
     *
     * @return Vehicle's length
     */
    float getLength() {
        return this.length;
    }

}
