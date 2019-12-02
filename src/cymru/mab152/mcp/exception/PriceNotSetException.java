package cymru.mab152.mcp.exception;

public class PriceNotSetException extends Throwable {

    public PriceNotSetException() {
        super("The price hasn't been set for this ParkingReceipt!");
    }
}
