package cymru.mab152.mcp;

import java.util.Date;

/**
 * ExitToken is a 4-digit-long number that allows the Customer to exit the Parking after they paid.
 * The amount of time Customers have to exit the Parking can be altered by changing the MINUTES_ALLOWED variable.
 */
public class ExitToken {

    private int id;
    private Date date;
    private final int MINUTES_ALLOWED = 15;

    /**
     * The constructor for the ExitToken objects.
     *
     * @param id ExitToken's unique ID. The constructor does NOT check if the ID is unique, so this MUST be
     *           done before calling it (i.e. using generateExitToken() method).
     * @param d ExitToken's Date (this is the Date when the token was generated).
     */
    ExitToken(int id, Date d) {
        this.id = id;
        this.date = d;
    }

    /**
     * Returns ExitToken's ID.
     *
     * @return token's id
     */
    int getId() {
        return this.id;
    }

    /**
     * Returns ExitToken's Date's timestamp (UNIX time format).
     *
     * @return timestamp
     */
    long getDateTimestamp() {
        return this.date.getTime();
    }

    /**
     * Checks if the ExitToken is still valid [has more time passed than it's allowed (default 15 mins)].
     *
     * @param currentDate current date
     * @return a boolean whether the token is still valid
     */
    boolean canExit(Date currentDate) {
        long currentDateTimestamp = currentDate.getTime();
        long dateTimeStamp = this.date.getTime();
        return currentDateTimestamp - dateTimeStamp > MINUTES_ALLOWED * 60;
    }

    /**
     * Returns all info about the ExitToken as a String.
     *
     * @return all info about the token
     */
    public String toString() {
        StringBuilder exit_token_sb = new StringBuilder();
        exit_token_sb.append("An ExitToken (ID ");
        exit_token_sb.append(this.id);
        exit_token_sb.append(") with date ");
        exit_token_sb.append(this.date.toString());
        return exit_token_sb.toString();
    }

}