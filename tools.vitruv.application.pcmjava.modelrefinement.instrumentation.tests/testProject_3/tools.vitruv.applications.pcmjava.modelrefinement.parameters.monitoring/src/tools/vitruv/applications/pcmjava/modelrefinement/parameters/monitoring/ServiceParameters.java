package tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring;

/**
 * Service parameter serialization.
 *
 * @author JP
 * changed by ND
 *
 */
public class ServiceParameters {

    /**
     * Empty service parameters.
     */
    public static ServiceParameters EMPTY = new ServiceParameters();

    private final StringBuilder stringBuilder;

    /**
     * Initializes a new instance of {@link ServiceParameters} class.
     */
    public ServiceParameters() {
        this.stringBuilder = new StringBuilder();
    }

    /**
     *
     * @param name
     *            Parameter name.
     * @param value
     *            Parameter value.
     */
    public void addValue(final String name, final Object value) {
        this.stringBuilder.append("\"").append(name).append("\":").append(value.toString()).append(",");
    }


    /**
     * Gets the serialized parameters.
     */
    @Override
    public String toString() {
        return "{" + this.stringBuilder.toString() + "}";
    }
}
