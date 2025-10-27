package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_FORCE;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ClearCommand object
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ClearCommand
     * and returns a ClearCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public ClearCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_FORCE);

        boolean isForced = argMultimap.getValue(PREFIX_FORCE).isPresent();

        return new ClearCommand(isForced);
    }

}
