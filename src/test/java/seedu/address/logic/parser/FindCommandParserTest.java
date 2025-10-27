package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClientContainsKeywordsPredicate;

class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    void parse_multipleNames_parseFailure() {
        String userInput = "n/Alice Bob"; // multiple names in a single prefix
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    void parse_multipleTags_parseFailure() {
        String userInput = "t/friend colleague"; // multiple tags
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    void parse_multipleDates_parseFailure() {
        String userInput = "d/2025-12-15 2026-12-15";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    void parse_invalidDate_parseFailure() {
        String userInput = "d/2025-13-40"; // invalid month and day
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    void parse_missingPrefix_throwsParseException() {
        String userInput = "Alice"; // no prefix at all
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    void parse_nameAndTag() throws Exception {
        // Input includes both name and tag, but we only assert tag in the
        // expected predicate
        String userInput = "n/Alex t/friend";

        // Only include the tag key in the expected criteria
        Map<String, List<String>> criteria = Map.of("tag", List.of("friend"));

        FindCommand expectedCommand = new FindCommand(new ClientContainsKeywordsPredicate(criteria));

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
