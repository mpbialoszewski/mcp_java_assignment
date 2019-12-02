package cymru.mab152.mcp;

/**
 * The superclass for DriverEmployee and regular Customers.
 */
public abstract class Driver {

    protected Vehicle vehicle;

    /**
     * Checks if the driver has a vehicle assigned.
     *
     * @return a boolean of Vehicle presence
     */
    private boolean hasVehicle() {
        return (this.vehicle != null);
    }

    /**
     * Returns all info about the driver as a String.
     *
     * @return the String with all info about the Driver.
     */
    public String toString() {
        StringBuilder driver_sb = new StringBuilder();
        driver_sb.append("A driver ");
        if (this.hasVehicle()) {
            driver_sb.append("with a vehicle:\n");
            driver_sb.append(this.vehicle.toString());
        } else {
            driver_sb.append("without a vehicle.");
        }
        return driver_sb.toString();
    }

    /**
     * Returns the vehicle assigned to the driver.
     * If no vehicle has been assigned -- returns null.
     *
     * @return the assigned Vehicle
     */
    Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Assigns a vehicle to the driver.
     *
     * @param v the new Vehicle
     */
    void setVehicle(Vehicle v) {
        vehicle = v;
    }

}
