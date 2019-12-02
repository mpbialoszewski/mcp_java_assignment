package cymru.mab152.mcp.exception;

public class NoFreeParkingSpacesException extends Throwable {


    public NoFreeParkingSpacesException(String parking) {
        super(parking);
    }
}
