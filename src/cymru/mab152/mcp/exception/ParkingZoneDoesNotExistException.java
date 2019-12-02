package cymru.mab152.mcp.exception;

public class ParkingZoneDoesNotExistException extends Throwable {

    public ParkingZoneDoesNotExistException() {
        super("The parking zone specified does not exist!");
    }

}
