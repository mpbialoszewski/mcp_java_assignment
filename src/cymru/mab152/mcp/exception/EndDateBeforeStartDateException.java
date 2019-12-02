package cymru.mab152.mcp.exception;

public class EndDateBeforeStartDateException extends Throwable {


    public EndDateBeforeStartDateException() {
        super("ERROR: The @endDate cannot be before @startDate!. Check the date time and try again.");
    }

}
