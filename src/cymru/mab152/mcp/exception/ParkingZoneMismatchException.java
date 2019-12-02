package cymru.mab152.mcp.exception;

public class ParkingZoneMismatchException extends Throwable {


    public ParkingZoneMismatchException() {
        super("This parking zone cannot support this parking space.\nCheck the ID and try again!");
    }

}

