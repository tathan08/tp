package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.ClientContainsKeywordsPredicate;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    /** Helper to create a FindCommand with a single field and its keywords. */
    private FindCommand buildExpectedCommand(String field, List<String> keywords) {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put(field, keywords);
        return new FindCommand(new ClientContainsKeywordsPredicate(criteria));
    }

    /** Helper to create a FindCommand with multiple criteria. */
    private FindCommand buildExpectedCommand(Map<String, List<String>> criteria) {
        return new FindCommand(new ClientContainsKeywordsPredicate(criteria));
    }

    // ------------------------------ Failure Tests
    // ------------------------------

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingPrefix_throwsParseException() {
        assertParseFailure(parser, "Alice Bob",
                                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        assertParseFailure(parser, "x/Alice", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    // ------------------------------- Single Field Success Tests
    // -------------------------------

    @Test
    public void parse_singleName_success() {
        FindCommand expected = buildExpectedCommand("name", List.of("Alice"));
        assertParseSuccess(parser, "n/Alice", expected);

        expected = buildExpectedCommand("name", List.of("Alice Pauline"));
        assertParseSuccess(parser, "n/Alice Pauline", expected);
    }

    @Test
    public void parse_multipleNames_success() {
        FindCommand expected = buildExpectedCommand("name", List.of("Alice Bob"));
        assertParseSuccess(parser, "n/Alice Bob", expected);
    }

    @Test
    public void parse_singleTag_success() {
        FindCommand expected = buildExpectedCommand("tag", List.of("friend"));
        assertParseSuccess(parser, "t/friend", expected);
    }

    @Test
    public void parse_multipleTags_success() {
        FindCommand expected = buildExpectedCommand("tag", List.of("friend good"));
        assertParseSuccess(parser, "t/friend good", expected);
    }

    @Test
    public void parse_singleDate_success() {
        FindCommand expected = buildExpectedCommand("date", List.of("2025-10-15"));
        assertParseSuccess(parser, "d/2025-10-15", expected);
    }

    @Test
    public void parse_multipleDates_success() {
        // Multiple dates treated as one search term
        FindCommand expected = buildExpectedCommand("date", List.of("2025-10-15 2025-10-20"));
        assertParseSuccess(parser, "d/2025-10-15 2025-10-20", expected);
    }

    // ------------------------------- Multiple Field Success Tests
    // -------------------------------

    @Test
    public void parse_multipleFields_success() {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("name", List.of("Alice Bob"));
        criteria.put("tag", List.of("friend"));
        criteria.put("date", List.of("2025-10-15"));

        FindCommand expected = buildExpectedCommand(criteria);

        assertParseSuccess(parser, "n/Alice Bob t/friend d/2025-10-15", expected);
        assertParseSuccess(parser, "t/friend d/2025-10-15 n/Alice Bob", expected);
    }

    // ------------------------------- Wildcard Success Tests
    // -------------------------------

    @Test
    public void parse_wildcard_success() {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("name", List.of());

        FindCommand expected = buildExpectedCommand(criteria);
        assertParseSuccess(parser, "n/", expected);
    }

    @Test
    public void parse_multipleFieldsWithWildcard_success() {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("name", List.of()); // Wildcard
        criteria.put("tag", List.of("friend")); // Specific tag

        FindCommand expected = buildExpectedCommand(criteria);
        assertParseSuccess(parser, "n/ t/friend", expected);
        assertParseSuccess(parser, "t/friend n/", expected);
    }
}
