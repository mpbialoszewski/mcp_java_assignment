package cymru.mab152.mcp;


public class ParkingSpace {

    private String id;
    private Vehicle vehicle;

    /**
     * Constructor for parking spaces.
     *
     * @param id stands for  ParkingSpace's ID
     */
    ParkingSpace(String id) {
        this.id = id;
        this.vehicle = null;
    }

    /**
     * Returns info about the parking space as a String.
     *
     * @return all info about the parking space
     */
    public String toString() {
        StringBuilder parking_space_sb = new StringBuilder();
        parking_space_sb.append("Parking space ");
        parking_space_sb.append(id);

        // Is free?
        if (this.vehicle == null) {
            parking_space_sb.append("\n - empty.");
        } else {
            parking_space_sb.append("\n with a vehicle parked in it:\n");
            parking_space_sb.append(this.vehicle.toString());
        }

        return parking_space_sb.toString();
    }

    /**
     * Returns a boolean whether the parking space is free or not.
     *
     * @return is the parking space free
     */
    boolean isFree() {
        return (this.vehicle == null);
    }

    /**
     * Assigns a Vehicle to the ParkingSpace.
     * It's also used to remove the Vehicle from the ParkingSpace
     * (by using setVehicle(null))
     *
     * @param v vehicle to be "parked" in the parking space
     */
    void setVehicle(Vehicle v) {
        this.vehicle = v;
    }

    /**
     * Returns a Vehicle parked in the space.
     *
     * @return a vehicle object
     */
    Vehicle getVehicle() {
        return this.vehicle;
    }

    /**
     * Returns parking space's ID.
     *
     * @return ParkingSpace id
     */
    String getID() {
        return this.id;
    }

}
