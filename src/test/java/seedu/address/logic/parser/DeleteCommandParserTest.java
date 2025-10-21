package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.person.Name;
import seedu.address.model.tag.Tag;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "delete n/ Alex Yeoh",
                new DeleteCommand(new Name("Alex Yeoh"), Optional.empty()));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyName_throwsParseException() {
        assertParseFailure(parser, "/n", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidNameType_throwsParseException() {
        assertParseFailure(parser, "delete n/R@chel",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Name.MESSAGE_CONSTRAINTS));
    }

    @Test
    public void parse_deleteTag_success() {
        String input = "delete n/Alex Yeoh t/tag";
        DeleteCommand expected = new DeleteCommand(new Name("Alex Yeoh"), Optional.of(Set.of(new Tag(("tag")))));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_deleteMultipleTag_success() {
        String input = "delete n/Alex Yeoh t/tag1 t/tag2";
        DeleteCommand expected = new DeleteCommand(new Name("Alex Yeoh"),
                Optional.of(Set.of(new Tag("tag1"), new Tag("tag2"))));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_deleteEmptyTag_throwsParseException() {
        String input = "delete n/Alex Yeoh t/ ";
        assertParseFailure(parser, input, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteCommand.MESSAGE_DELETE_TAG_USAGE));
    }

    @Test
    public void parse_deleteBooking_success() {
        String input = "delete n/Alex Yeoh b/1";
        DeleteCommand expected = new DeleteCommand(new Name("Alex Yeoh"), 1);
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_deleteEmptyBooking_throwsParseException() {
        String input = "delete n/Alex Yeoh b/ ";
        assertParseFailure(parser, input, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteCommand.MESSAGE_DELETE_BOOKING_USAGE));
    }

    @Test
    public void parse_deleteBookingAndTag_throwsParseException() {
        String input = "delete n/Alex Yeoh b/1 t/1 ";
        assertParseFailure(parser, input, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteCommand.MESSAGE_DELETE_BOOKING_OR_TAG));
    }

}
