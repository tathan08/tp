package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the name saved in the displayed person list.\n"
            + "Parameters: NAME (must be a saved contact)\n"
            + "Example: " + COMMAND_WORD + " /n" + " name";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_PERSON_NOT_FOUND = "No such person found: %s";
    public static final String MESSAGE_DELETE_PERSON_MULTIPLE_MATCH = "Multiple matches for %s: \n%s\n";

    private final Name targetName;

    public DeleteCommand(Name targetName) {
        this.targetName = targetName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        List<Person> matches = lastShownList.stream()
                                .filter(p -> p.getName().equals(targetName))
                                .toList();

        if (matches.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_DELETE_PERSON_NOT_FOUND, targetName.fullName));
        }

        if (matches.size() > 1) {
            String allMatches = matches.stream()
                    .map(Messages::format)
                    .collect(Collectors.joining("\n"));
            throw new CommandException(
                    String.format(MESSAGE_DELETE_PERSON_MULTIPLE_MATCH, targetName.fullName, allMatches));
        }

        Person personToDelete = matches.get(0);
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetName.equals(otherDeleteCommand.targetName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetName)
                .toString();
    }
}
