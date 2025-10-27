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
        // FIX: Replaced argMultimap.getAllValues(...) with
        // getValue().map(List::of).orElse(List.of())
        // to retrieve the single value correctly when Prefix.equals() is
        // broken.
        List<String> allNames = argMultimap.getValue(PREFIX_NAME).map(String::trim).map(List::of).orElse(List.of());

        // If the user typed n/ with no actual value, treat as wildcard -> match
        // all persons
        boolean nameWildcard = allNames.stream().anyMatch(String::isEmpty);
        if (nameWildcard) {
            searchCriteria.put("name", List.of()); // empty list means wildcard
        } else if (!allNames.isEmpty()) {
            searchCriteria.put("name", allNames.stream().distinct().toList());
        }

        // === TAG ===
        // FIX: Same change applied here.
        List<String> allTags = argMultimap.getValue(PREFIX_TAG).map(String::trim).map(List::of).orElse(List.of());

        boolean tagWildcard = allTags.stream().anyMatch(String::isEmpty);
        if (tagWildcard) {
            searchCriteria.put("tag", List.of());
        } else if (!allTags.isEmpty()) {
            searchCriteria.put("tag", allTags.stream().distinct().toList());
        }

        // === DATE ===
        // FIX: Same change applied here.
        List<String> allDates = argMultimap.getValue(PREFIX_DATE).map(String::trim).map(List::of).orElse(List.of());

        boolean dateWildcard = allDates.stream().anyMatch(String::isEmpty);
        if (dateWildcard) {
            searchCriteria.put("date", List.of());
        } else if (!allDates.isEmpty()) {
            searchCriteria.put("date", allDates.stream().distinct().toList());
        }

        // If user didn't provide any prefixes at all (like plain "find")
        if (searchCriteria.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return new FindCommand(new ClientContainsKeywordsPredicate(searchCriteria));
    }

    /**
     * Extracts the prefix from the arguments.
     *
     * @param args The trimmed arguments string.
     *
     * @return The prefix string (e.g., "n/", "t/", "d/").
     * @throws ParseException if no valid prefix is found.
     */
    private String extractPrefix(String args) throws ParseException {
        if (args.startsWith("n/")) {
            return "n/";
        } else if (args.startsWith("t/")) {
            return "t/";
        } else if (args.startsWith("d/")) {
            return "d/";
        } else {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE.toString()));
        }
    }

    /**
     * Extracts the keywords from the arguments after removing the prefix.
     *
     * @param args The trimmed arguments string.
     * @param prefix The prefix to remove.
     *
     * @return The keyword string.
     * @throws ParseException if no keywords are provided.
     */
    private String extractKeywords(String args, String prefix) {
        String keywordArgs = args.substring(prefix.length()).trim();
        // Do not throw exception; allow empty string
        return keywordArgs;
    }

    /**
     * Creates a FindCommand based on the prefix and keywords.
     *
     * @param prefix The search prefix.
     * @param keywords The list of keywords.
     *
     * @return A FindCommand with the appropriate predicate.
     * @throws ParseException if the prefix is invalid.
     */
    private FindCommand createFindCommand(Map<String, List<String>> searchCriteria) {
        return new FindCommand(new ClientContainsKeywordsPredicate(searchCriteria));
    }

}
