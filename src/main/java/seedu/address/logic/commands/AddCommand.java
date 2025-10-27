package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.ErrorMessage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final ErrorMessage MESSAGE_USAGE = new ErrorMessage(
            "Adds a person or adds tags to an existing person in the address book.",
            PREFIX_NAME + "NAME "
                    + "[" + PREFIX_PHONE + "PHONE] "
                    + "[" + PREFIX_EMAIL + "EMAIL] "
                    + "[" + PREFIX_TAG + "TAG]...",
            COMMAND_WORD + " "
                    + PREFIX_NAME + "John Doe "
                    + PREFIX_PHONE + "98765432 "
                    + PREFIX_EMAIL + "johnd@example.com "
                    + PREFIX_TAG + "friends"
    );

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_TAGS_ADDED = "Tags added to existing person: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON =
            "A person with this name already exists in your address book!\n"
            + "If you want to add tags to this person, please include a "
            + PREFIX_TAG + "TAG field in your command.\n"
            + "Example: add n/John Doe t/VIP";

    private static final Logger logger = LogsCenter.getLogger(AddCommand.class);

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        logger.info(String.format("Executing AddCommand for person: %s", toAdd.getName()));

        // Check if person already exists
        if (model.hasPerson(toAdd)) {

            logger.info(String.format("Person already exists: %s, adding tags to existing person", toAdd.getName()));
            // Find the existing person by name
            Person existingPerson = findPersonByName(model, toAdd.getName());

            if (existingPerson != null) {
                // Only add tags if there are tags specified
                if (toAdd.getTags().isEmpty()) {
                    throw new CommandException(MESSAGE_DUPLICATE_PERSON);
                } else {
                    // Add new tags to existing person
                    Set<Tag> existingTags = new LinkedHashSet<>(existingPerson.getTags());
                    Set<Tag> newTags = toAdd.getTags();

                    // Check if any new tags would exceed the limit
                    if (existingTags.size() + newTags.size() > Person.MAX_TAGS) {
                        throw new CommandException(String.format("Cannot add tags - tag limit reached!\n"
                                + "Contact '%s' already has %d tag(s), and you're trying to add %d more.\n"
                                + "Maximum allowed: %d tags per contact.\n"
                                + "Please remove some existing tags before adding new ones.",
                                toAdd.getName(), existingTags.size(), newTags.size(), Person.MAX_TAGS));
                    }

                    // Add new tags (duplicates will be automatically handled by Set)
                    existingTags.addAll(newTags);

                    // Create updated person with merged tags
                    Person updatedPerson = new Person(
                            existingPerson.getName(),
                            existingPerson.getPhone(),
                            existingPerson.getEmail(),
                            existingTags,
                            existingPerson.getBookings()
                    );

                    model.setPerson(existingPerson, updatedPerson);
                    return new CommandResult(String.format(MESSAGE_TAGS_ADDED, Messages.format(updatedPerson)));
                }
            }
        }

        // If person doesn't exist, add as new person
        model.addPerson(toAdd);

        logger.info(String.format("Successfully added person: %s", toAdd.getName()));
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public int hashCode() {
        return toAdd.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }

    /**
     * Finds a person by name in the model's filtered person list.
     *
     * @param model The model to search in
     * @param name The name to search for
     * @return The person if found, null otherwise
     */
    private Person findPersonByName(Model model, seedu.address.model.person.Name name) {
        List<Person> personList = model.getFilteredPersonList();
        for (Person person : personList) {
            if (person.getName().equals(name)) {
                return person;
            }
        }
        return null;
    }
}
