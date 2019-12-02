package cymru.mab152.mcp;

/**
 * A class for parking employees that extends the class Driver and inherits
 * an instance variable "vehicle" from it.
 */
public class DriverEmployee extends Driver {

    private int id;
    private String name;

    /**
     * A constructor for DriverEmployee objects.
     * It allows to convert a FreeEmployee into DriverEmployee.
     *
     * @param fe FreeEmployee to convert
     */
    DriverEmployee(FreeEmployee fe) {
        this.id = fe.getId();
        this.name = fe.getName();
        this.vehicle = null;
    }

    /**
     * Returns all info about the Employee as a string.
     *
     * @return all info about the Employee
     */
    @Override
    public String toString() {
        StringBuilder driverEmployee_sb = new StringBuilder();
        driverEmployee_sb.append("Driver employee ");
        driverEmployee_sb.append(name);
        driverEmployee_sb.append(" (ID ");
        driverEmployee_sb.append(id);
        driverEmployee_sb.append(").");
        driverEmployee_sb.append("\nHas a vehicle:\n");
        driverEmployee_sb.append(vehicle.toString());
        return driverEmployee_sb.toString();
    }

    /**
     * Returns the Employee's ID
     *
     * @return Employee's ID
     */
    int getId() {
        return this.id;
    }

    /**
     * Returns the Employee's name.
     *
     * @return Employee's name
     */
    String getName() {
        return this.name;
    }
}
