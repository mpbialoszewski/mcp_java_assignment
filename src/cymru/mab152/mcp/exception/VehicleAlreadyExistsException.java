package cymru.mab152.mcp.exception;

public class VehicleAlreadyExistsException extends Throwable {

    public VehicleAlreadyExistsException() {
        super("There is already a vehicle with the same license plate parked in here." +
                "\nIf you believe this is a mistake, contact the parking staff.");
    }
}

