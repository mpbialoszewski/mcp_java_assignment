package cymru.mab152.mcp;

/**
 * A class for Employees, who are in the "free" state.
 * They cannot park any Vehicles, thus they need to be converted
 * into DriverEmployees before they can park a Vehicle.
 */
public class FreeEmployee {

    private int id;
    private String name;

    /**
     * A constructor for FreeEmployees.
     * Used when Employees are loaded form JSON database.
     *
     * @param id becomes Employee's ID
     * @param name becomes Employee's name
     */
    FreeEmployee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * A constructor for FreeEmployees.
     * Used when converting DriverEmployee back into FreeEmployee
     * after they are done with a Vehicle.
     *
     * @param de DriverEmployee that will be converted into a FreeEmployee
     */
    FreeEmployee(DriverEmployee de) {
        this.id = de.getId();
        this.name = de.getName();
    }

    /**
     * Returns FreeEmployee's unique ID.
     *
     * @return Employee's ID.
     */
    int getId() {
        return this.id;
    }

    /**
     * Returns FreeEmployee's name.
     *
     * @return Employee's name
     */
    String getName() {
        return this.name;
    }

    /**
     * Returns all info about an Employee as a String.
     *
     * @return all info about an Employee
     */
    public String toString() {
        StringBuilder Free_employer_sb = new StringBuilder();
        Free_employer_sb.append("Employee ");
        Free_employer_sb.append(this.name);
        Free_employer_sb.append(" (ID ");
        Free_employer_sb.append(this.id);
        Free_employer_sb.append(").");
        return Free_employer_sb.toString();
    }


}
