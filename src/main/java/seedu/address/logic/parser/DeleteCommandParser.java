package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BOOKING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.tag.Tag;


/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            ArgumentMultimap multimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG, PREFIX_BOOKING);
            String name = multimap.getValue(PREFIX_NAME).orElse("").trim();
            List<String> allTags = multimap.getAllValues(PREFIX_TAG);

            for (String raw : allTags) {
                String r = raw.trim();
                if (r.chars().anyMatch(Character::isWhitespace)) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            DeleteCommand.MESSAGE_DELETE_TAG_NO_SPACES));
                }
            }

            List<String> rawTags = allTags.stream()
                    .map(String::trim)
                    .filter(y -> !y.isEmpty())
                    .toList();


            if (name.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
            }

            if (!Name.isValidName(name)) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, Name.MESSAGE_CONSTRAINTS));
            }

            Name targetName = new Name(name);
            boolean prefixTagExists = !allTags.isEmpty();

            if (multimap.getValue(PREFIX_BOOKING).isPresent()) {
                String id = multimap.getValue(PREFIX_BOOKING).orElse("").trim();
                if (id.isEmpty()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            DeleteCommand.MESSAGE_DELETE_BOOKING_USAGE));
                }
                Integer bookingID = Integer.valueOf(id);
                if (prefixTagExists) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            DeleteCommand.MESSAGE_DELETE_BOOKING_OR_TAG));
                }
                return new DeleteCommand(targetName, bookingID);
            }

            if (rawTags.isEmpty()) {
                if (prefixTagExists) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            DeleteCommand.MESSAGE_DELETE_TAG_USAGE));
                } else {
                    return new DeleteCommand(targetName, Optional.empty());
                }
            }

            Set<Tag> tags = ParserUtil.parseTags(rawTags);
            Optional<Set<Tag>> targetTag = Optional.of(tags);

            return new DeleteCommand(targetName, targetTag);

        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage(), pe);
        }
    }

}
