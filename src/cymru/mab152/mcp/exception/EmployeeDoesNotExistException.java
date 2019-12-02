package cymru.mab152.mcp.exception;

public class EmployeeDoesNotExistException extends Throwable {


    public EmployeeDoesNotExistException() {
        super("ERROR: There is no employee with that ID. Check if the Employee ID was inputted correctly");
    }
}

