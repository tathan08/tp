package seedu.address.commons;

/**
 * Represents a standardized error message structure for commands.
 * Each error message consists of 3 components displayed in a MessageBox:
 * 1. Command function - What the command does
 * 2. Parameters - Parameters including delimiters and brackets to indicate optional fields
 * 3. Example - A concrete example of how to use the command
 *
 * Note: The "Invalid command format!" prefix is automatically added by Messages.MESSAGE_INVALID_COMMAND_FORMAT
 */
public class ErrorMessage {
    private final String commandFunction;
    private final String parameters;
    private final String example;

    /**
     * Creates an ErrorMessage with the 3 required components.
     *
     * @param commandFunction Description of what the command does
     * @param parameters Parameters format with delimiters and brackets for optional fields
     * @param example A concrete example of the command
     */
    public ErrorMessage(String commandFunction, String parameters, String example) {
        this.commandFunction = commandFunction;
        this.parameters = parameters;
        this.example = example;
    }

    /**
     * Returns the complete error message formatted for display in a MessageBox.
     * Note: This returns only the details (function, parameters, example).
     * The actual error prefix is added by Messages.MESSAGE_INVALID_COMMAND_FORMAT.
     *
     * @return Formatted error message with command details
     */
    @Override
    public String toString() {
        return "\nFunction: " + commandFunction + "\n"
                + "Parameters: " + parameters + "\n"
                + "Example: " + example;
    }

    /**
     * Returns the command function component.
     *
     * @return The command function description
     */
    public String getCommandFunction() {
        return commandFunction;
    }

    /**
     * Returns just the parameters component.
     *
     * @return The parameters format
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * Returns just the example component.
     *
     * @return The example
     */
    public String getExample() {
        return example;
    }
}

