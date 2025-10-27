package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";
    public static final String MESSAGE_WARNING = "WARNING: This will delete all data in the address book!\n"
            + "If you are sure, please use: clear f/";

    private static final Logger logger = LogsCenter.getLogger(ClearCommand.class);

    private final boolean isForced;

    /**
     * Creates a ClearCommand with the specified force flag.
     * @param isForced whether the command should execute without warning
     */
    public ClearCommand(boolean isForced) {
        this.isForced = isForced;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        if (!isForced) {
            logger.info("Clear command attempted without force flag");
            return new CommandResult(MESSAGE_WARNING);
        }

        logger.warning("Executing ClearCommand - this will delete all data!");

        // Log the number of persons being deleted for audit trail
        int personCount = model.getAddressBook().getPersonList().size();
        logger.info(String.format("Clearing address book with %d persons", personCount));

        model.setAddressBook(new AddressBook());

        logger.warning("Address book successfully cleared - all data deleted");
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClearCommand)) {
            return false;
        }

        ClearCommand otherClearCommand = (ClearCommand) other;
        return isForced == otherClearCommand.isForced;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(isForced);
    }
}
