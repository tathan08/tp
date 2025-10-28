package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClientContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_DATE = new Prefix("d/");

    /**
     * Parses the given {@code String} of arguments in the context of the
     * FindCommand and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindCommand parse(String args) throws ParseException {
        assert args != null : "args must not be null";

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG, PREFIX_DATE);

        Map<String, List<String>> searchCriteria = new HashMap<>();

        // === NAME ===
        List<String> allNames = argMultimap.getAllValues(PREFIX_NAME).stream()
                .map(String::trim)
                .toList();

        boolean nameWildcard = allNames.stream().anyMatch(String::isEmpty);
        if (nameWildcard) {
            searchCriteria.put("name", List.of());
        } else if (!allNames.isEmpty()) {
            searchCriteria.put("name", allNames.stream().distinct().toList());
        }

        // === TAG ===
        List<String> allTags = argMultimap.getAllValues(PREFIX_TAG).stream()
                .map(String::trim)
                .toList();

        boolean tagWildcard = allTags.stream().anyMatch(String::isEmpty);
        if (tagWildcard) {
            searchCriteria.put("tag", List.of());
        } else if (!allTags.isEmpty()) {
            searchCriteria.put("tag", allTags.stream().distinct().toList());
        }

        // === DATE ===
        List<String> allDates = argMultimap.getValue(PREFIX_DATE).map(String::trim).map(List::of).orElse(List.of());

        // Defensive validation: ensure all date strings are valid
        for (String dateStr : allDates) {
            if (!dateStr.isEmpty() && !isValidDate(dateStr)) {
                throw new ParseException("Invalid date!");
            }
        }

        boolean dateWildcard = allDates.stream().anyMatch(String::isEmpty);
        if (dateWildcard) {
            searchCriteria.put("date", List.of());
        } else if (!allDates.isEmpty()) {
            searchCriteria.put("date", allDates.stream().distinct().toList());
        }

        // If user didn't provide any prefixes at all
        if (searchCriteria.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return new FindCommand(new ClientContainsKeywordsPredicate(searchCriteria));
    }

    /**
     * Helper method to validate ISO date format yyyy-MM-dd
     */
    private boolean isValidDate(String dateStr) {
        try {
            java.time.LocalDate.parse(dateStr);
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }

}
