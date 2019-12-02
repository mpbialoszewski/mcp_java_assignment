package cymru.mab152.mcp.exception;


public class VehicleSizeNotSupportedException extends Throwable {


    public VehicleSizeNotSupportedException() {
        super("Your vehicle is not supported or the dimensions you typed are incorrect!");
    }

}
