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

    private static final Logger logger = LogsCenter.getLogger(ClearCommand.class);

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        
        logger.warning("Executing ClearCommand - this will delete all data!");
        
        // Invariant assertion: model should be in valid state
        assert model.getAddressBook() != null : "Model address book should not be null";
        assert model.getFilteredPersonList() != null : "Model filtered person list should not be null";
        
        // Log the number of persons being deleted for audit trail
        int personCount = model.getAddressBook().getPersonList().size();
        logger.info(String.format("Clearing address book with %d persons", personCount));
        
        model.setAddressBook(new AddressBook());
        
        // Post-condition assertion: address book should be empty after clearing
        assert model.getAddressBook().getPersonList().isEmpty() : "Address book should be empty after clearing";
        assert model.getFilteredPersonList().isEmpty() : "Filtered person list should be empty after clearing";
        
        logger.warning("Address book successfully cleared - all data deleted");
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
