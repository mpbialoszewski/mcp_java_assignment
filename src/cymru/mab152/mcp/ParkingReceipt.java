package cymru.mab152.mcp;


import cymru.mab152.mcp.exception.EndDateBeforeStartDateException;
import cymru.mab152.mcp.exception.PriceNotSetException;

import java.util.Date;

/**
 * A parking receipt is generated when a customer adds a car.
 * Its ID is unique and is used to collect the car afterwards.
 */
public class ParkingReceipt {

    private int id;
    private Date startDate;
    private Date endDate;
    private boolean isDisabled;
    private float pricePerHour;

    /**
     * A constructor for objects of class ParkingReceipt.
     * Its parameters are receipt's ID and the Date when the vehicle is parked.<br>
     * It <b>DOES NOT</b> check if the ID is free, so it needs to
     * be checked before calling the constructor (done with Parking.getNextReceiptId() method)
     *
     * @param id    becomes receipt's ID
     * @param start the Date when a vehicle is parked
     * @param isDisabled is the Driver disabled (applicable for discounts)
     */
    ParkingReceipt(int id, Date start, boolean isDisabled) {
        this.id = id;
        startDate = start;
        endDate = null;
        this.isDisabled = isDisabled;
        this.pricePerHour = -1.0f; // If it stays at -1.0, it's wrong;
    }

    /**
     * Returns the receipt's ID.
     *
     * @return receipt's ID
     */
    int getId() {
        return id;
    }

    /**
     * Sets the start date to the date provided.
     *
     * @param d becomes new start Date
     */
    void setStartDate(Date d) {
        startDate = d;
    }

    /**
     * Returns the Date when the receipt was created.
     *
     * @return startDate
     */
    Date getStartDate() {
        return this.startDate;
    }

    /**
     * Sets the end date to the date provided.
     * Throws an exception if endDate is before startDate.
     *
     * @param d becomes the new endDate
     * @throws EndDateBeforeStartDateException is thrown if the endDate is before startDate
     */
    void setEndDate(Date d) throws EndDateBeforeStartDateException {
        if (d.getTime() < startDate.getTime()) {
            throw new EndDateBeforeStartDateException();
        } else {
            this.endDate = d;
        }
    }

    /**
     * Returns the end date (when the vehicle was leaving the parking)
     * or null, if the vehicle is still parked.
     *
     * @return endDate
     */
    Date getEndDate() {
        return this.endDate;
    }

    /**
     * Calculates and returns a pricePerHour to pay for parking a vehicle.
     *
     * @return a pricePerHour to pay
     */
    float getPrice() {
        long millisParked = this.endDate.getTime() - this.startDate.getTime();
        long secondsParked = millisParked / 1000;
        long minutesParked = secondsParked / 60;
        secondsParked %= 60;
        long hoursParked = minutesParked / 60;

        // Every started hour (even by a second) adds an hour to the counter.
        if (secondsParked != 0) {
            hoursParked++;
        }

        return hoursParked * pricePerHour;
    }

    /**
     * Returns all info about the receipt as a string.
     *
     * @return a String with all info
     */
    public String toString() {
        StringBuilder Parking_receipt_sb = new StringBuilder();
        Parking_receipt_sb.append("A parking receipt (ID ");
        Parking_receipt_sb.append(id);
        Parking_receipt_sb.append(").");
        Parking_receipt_sb.append("Start date:\n");
        Parking_receipt_sb.append(startDate.toString());

        // Is the endDate known?
        if (endDate != null) {
            Parking_receipt_sb.append("\nEnd date:\n");
            Parking_receipt_sb.append(endDate.toString());
        }

        // Is the owner disabled?
        if (this.isDisabled) {
            Parking_receipt_sb.append("\nThe owner of the vehicle assigned to this parking receipt is disabled.");
        }

        return Parking_receipt_sb.toString();
    }

    /**
     * Returns a boolean whether the owner of the Vehicle assigned
     * to this ParkingReceipt is disabled or not.
     *
     * @return is the owner disbled
     */
    boolean isOwnerDisabled() {
        return this.isDisabled;
    }

    /**
     * Allows the user to change whether the owner
     * of the Vehicle is disabled or not.
     *
     * @param d becomes new isDisabled
     */
    void setOwnerDisabled(boolean d) {
        this.isDisabled = d;
    }

    /**
     * Returns a pricePerHour (in units) per one hour of parking or throws an exception
     * if the pricePerHour hasn't been set (if it's still the default -1.0f value).
     *
     * @return the pricePerHour per an hour of parking
     * @throws PriceNotSetException thrown if price is not set
     */
    float getPricePerHour() throws PriceNotSetException {
        if (this.pricePerHour == -1.0f) {
            throw new PriceNotSetException();
        } else {
            return this.pricePerHour;
        }
    }

    /**
     * Sets the pricePerHour (in units) per one hour of parking for the Vehicle.
     *
     * @param p pricePerHour per hour
     */
    void setPricePerHour(float p) {
        this.pricePerHour = p;
    }
}
