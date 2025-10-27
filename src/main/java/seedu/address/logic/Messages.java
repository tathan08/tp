package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Sorry, I don't recognize that command.\n"
            + "Type 'help' to see the list of available commands.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index you provided is invalid.\n"
            + "Please use a valid number from the displayed list.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "Found %1$d person(s) matching your search!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "You've specified multiple values for these fields that should only have one value: ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName());
        if (person.getPhone() != null) {
            builder.append("; Phone: ").append(person.getPhone());
        }
        if (person.getEmail() != null) {
            builder.append("; Email: ").append(person.getEmail());
        }
        builder.append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}
