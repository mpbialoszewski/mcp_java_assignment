package cymru.mab152.mcp.exception;


public class ParkingSpaceOccupiedException extends Throwable {


    public ParkingSpaceOccupiedException() {
        super("This parking space is already taken.\nTry another one.");
    }

}
