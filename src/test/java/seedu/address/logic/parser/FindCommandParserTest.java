package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.ClientContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommandName() {
        // no leading and trailing whitespaces - searching by name
        FindCommand expectedFindCommand =
                new FindCommand(new ClientContainsKeywordsPredicate(
                        ClientContainsKeywordsPredicate.SearchType.NAME,
                        Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "n/ Alice Bob", expectedFindCommand);

        // Multiple whitespaces between keywords
        assertParseSuccess(parser, " n/ \n Alice \n \t Bob  ", expectedFindCommand);

        // No space after prefix
        assertParseSuccess(parser, "n/Alice Bob", expectedFindCommand);
        assertParseSuccess(parser, "n/Alice  Bob", expectedFindCommand);
    }

    @Test
    public void parse_validArgs_returnsFindCommandTag() {
        // no leading and trailing whitespaces - searching by tag
        FindCommand expectedFindCommand =
                new FindCommand(new ClientContainsKeywordsPredicate(
                        ClientContainsKeywordsPredicate.SearchType.TAG,
                        Arrays.asList("good", "friend")));
        assertParseSuccess(parser, "t/ good friend", expectedFindCommand);

        // Multiple whitespaces between keywords
        assertParseSuccess(parser, " t/   good friend   ", expectedFindCommand);

        // No space after prefix
        assertParseSuccess(parser, "t/good friend", expectedFindCommand);
    }

    @Test
    public void parse_validDateArgs_returnsFindCommandDate() {
        // no leading and trailing whitespaces - searching by booking date
        FindCommand expectedFindCommand =
                new FindCommand(new ClientContainsKeywordsPredicate(
                        ClientContainsKeywordsPredicate.SearchType.DATE,
                        Arrays.asList("2025-10-15", "2025-10-20")));
        assertParseSuccess(parser, "d/ 2025-10-15 2025-10-20", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " d/    2025-10-15    2025-10-20  ", expectedFindCommand);

        // No space after prefix
        assertParseSuccess(parser, "d/2025-10-15 2025-10-20", expectedFindCommand);
    }

    @Test
    public void parse_missingPrefix_throwsParseException() {
        // should fail if prefix (n/, t/, d/) is missing
        assertParseFailure(parser, "Alice Bob",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        // invalid prefix for command
        assertParseFailure(parser, "x/ Alice",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

}
