package cymru.mab152.mcp;

import cymru.mab152.mcp.exception.ParkingZoneMismatchException;

import java.util.ArrayList;

/**
 * A ParkingZone contains a list of ParkingSpaces, a list of accepted Vehicles
 * and a pricePerHour for parking a Vehicle in the Zone.
 */
public class ParkingZone {

    private String id;
    private ArrayList<ParkingSpace> parkingSpaces;
    private float price;
    private ArrayList<VehicleType> acceptedVehicles;

    /**
     * Constructor for parking zone.
     * It does not create any parking spaces, so they must
     * be added later using addParkingSpace() method.
     *ś
     * @param id ID of the parking zone
     * @param price pricePerHour (in units) per 1 hour
     * @param av array list of accepted vehicle types
     */
    ParkingZone(String id, float price, ArrayList<VehicleType> av) {
        this.id = id;
        this.price = price;
        this.acceptedVehicles = av;
        parkingSpaces = new ArrayList<>();
    }

    /**
     * Checks if a vehicle can be parked in the parking zone.
     *
     * @param vt type of vehicle (VehicleType enum)
     * @return a boolean whether the vehicle can be parked or not
     */
    boolean isVehicleAccepted(VehicleType vt) {
        for (VehicleType temp : acceptedVehicles) {
            if (temp.equals(vt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a parking space to the parking zone.
     * Throws an exception if ParkingSpace's ID doesn't start
     * with the same letter as ParkingZone's ID.
     *
     * @param ps a ParkingSpace that will be added to the ParkingZone
     * @throws ParkingZoneMismatchException thrown if ParkingZone's and ParkingSpace's ID don't match
     */
    private void addParkingSpace(ParkingSpace ps) throws ParkingZoneMismatchException {
        if (!ps.getID().toUpperCase().startsWith(this.id.toUpperCase())) {
            throw new ParkingZoneMismatchException();
        } else {
            parkingSpaces.add(ps);
        }
    }

    /**
     * Adds a ParkingSpace to the ParkingZone.
     *
     * @param ps ParkingSpace to be added
     * @throws ParkingZoneMismatchException thrown if the ParkingSpace cannot be added to the ParkingZone
     */
    void addParkingSpace(ArrayList<ParkingSpace> ps) throws ParkingZoneMismatchException {
        for (ParkingSpace temp : ps) {
            this.addParkingSpace(temp);
        }
    }

    /**
     * Returns all info about the parking zone as a String.
     *
     * @return all info about the parking zone
     */
    public String toString() {
        StringBuilder parking_zone_sb = new StringBuilder();
        // Basic info
        parking_zone_sb.append("Parking zone ");
        parking_zone_sb.append(id);
        parking_zone_sb.append(", the pricePerHour for 1 hour is ");
        parking_zone_sb.append(price);
        parking_zone_sb.append(" units.");
        // Accepted vehicles
        if (acceptedVehicles.isEmpty()) {
            parking_zone_sb.append("\nIt doesn't currently accept any types of vehicles.");
        } else {
            parking_zone_sb.append("\nIt accepts following types of vehicles:\n");
            for (VehicleType vt : acceptedVehicles) {
                parking_zone_sb.append(vt.toString());
                parking_zone_sb.append('\n');
            }
        }
        // Parking spaces
        if (parkingSpaces.isEmpty()) {
            parking_zone_sb.append("\nIt doesn't currently contain any parking spaces.");
        } else {
            parking_zone_sb.append(".\nContains parking spaces:\n");
            for (ParkingSpace ps : parkingSpaces) {
                parking_zone_sb.append(ps.toString());
                parking_zone_sb.append('\n');
            }
        }
        return parking_zone_sb.toString();
    }

    /**
     * Returns a list of all free ParkingSpaces in the ParkingZone.
     *
     * @return list of free ParkingSpaces
     */
    String getId() {
        return this.id;
    }

    /**
     * Returns a list of all free parking spaces in the parking zone
     * as an ArrayList of ParkingSpace.
     *
     * @return list of free parking spaces in the parking zone
     */
    ArrayList<ParkingSpace> getListOfFreeParkingSpaces() {
        ArrayList<ParkingSpace> freeParkingSpaces = new ArrayList<>();
        for (ParkingSpace ps : parkingSpaces) {
            if (ps.isFree()) {
                freeParkingSpaces.add(ps);
            }
        }
        return freeParkingSpaces;
    }

    /**
     * Returns a list of all ParkingSpaces in the ParkingZone.
     *
     * @return list of all ParkingSpaces
     */
    ArrayList<ParkingSpace> getListOfParkingSpaces() {
        return this.parkingSpaces;
    }

    /**
     * Returns the pricePerHour (in units) per one hour of parking in the Zone.
     *
     * @return pricePerHour per hour
     */
    float getPrice() {
        return this.price;
    }

    /**
     * Returns the list of accepted VehicleTypes.
     *
     * @return list of accepted VehicleTypes
     */
    ArrayList<VehicleType> getListOfAcceptedVehicles() {
        return this.acceptedVehicles;
    }


}
