package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLIENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.BookCommand;
import seedu.address.model.booking.Booking;
import seedu.address.model.person.Name;

public class BookCommandParserTest {
    private BookCommandParser parser = new BookCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        String personName = "Alice Tan";
        String clientName = "John Doe";
        String datetime = "2025-12-25 14:00";
        String description = "Annual review";

        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 12, 25, 14, 0);
        BookCommand expectedCommand = new BookCommand(new Name(personName), clientName,
                expectedDateTime, description);

        // All fields present
        assertParseSuccess(parser,
                " " + PREFIX_DATETIME + datetime + " " + PREFIX_CLIENT + clientName
                        + " " + PREFIX_NAME + personName + " " + PREFIX_DESCRIPTION + description,
                expectedCommand);
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        String personName = "Alice Tan";
        String clientName = "John Doe";
        String datetime = "2025-12-25 14:00";

        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 12, 25, 14, 0);
        BookCommand expectedCommand = new BookCommand(new Name(personName), clientName,
                expectedDateTime, "No description provided");

        // No description
        assertParseSuccess(parser,
                " " + PREFIX_DATETIME + datetime + " " + PREFIX_CLIENT + clientName
                        + " " + PREFIX_NAME + personName,
                expectedCommand);
    }

    @Test
    public void parse_clientNameWithMultipleSpaces_success() {
        String personName = "Alice Tan";
        String clientNameWithSpaces = "John     Doe"; // Multiple spaces
        String datetime = "2025-12-25 14:00";

        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 12, 25, 14, 0);
        // Expected to be normalized to single space
        BookCommand expectedCommand = new BookCommand(new Name(personName), "John Doe",
                expectedDateTime, "No description provided");

        assertParseSuccess(parser,
                " " + PREFIX_DATETIME + datetime + " " + PREFIX_CLIENT + clientNameWithSpaces
                        + " " + PREFIX_NAME + personName,
                expectedCommand);
    }

    @Test
    public void parse_clientNameWithLeadingTrailingSpaces_success() {
        String personName = "Alice Tan";
        String clientNameWithSpaces = "  John Doe  "; // Leading and trailing spaces
        String datetime = "2025-12-25 14:00";

        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 12, 25, 14, 0);
        // Expected to be trimmed
        BookCommand expectedCommand = new BookCommand(new Name(personName), "John Doe",
                expectedDateTime, "No description provided");

        assertParseSuccess(parser,
                " " + PREFIX_DATETIME + datetime + " " + PREFIX_CLIENT + clientNameWithSpaces
                        + " " + PREFIX_NAME + personName,
                expectedCommand);
    }

    @Test
    public void parse_clientNameWithMixedWhitespace_success() {
        String personName = "Alice Tan";
        String clientNameWithSpaces = "  John    Q    Doe  "; // Multiple spaces, leading, trailing
        String datetime = "2025-12-25 14:00";

        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 12, 25, 14, 0);
        // Expected to be normalized: trimmed and multiple spaces reduced to single
        BookCommand expectedCommand = new BookCommand(new Name(personName), "John Q Doe",
                expectedDateTime, "No description provided");

        assertParseSuccess(parser,
                " " + PREFIX_DATETIME + datetime + " " + PREFIX_CLIENT + clientNameWithSpaces
                        + " " + PREFIX_NAME + personName,
                expectedCommand);
    }

    @Test
    public void parse_clientNameWithSpecialCharacters_success() {
        String personName = "Alice Tan";
        String clientName = "O'Brien-Smith"; // Special characters allowed
        String datetime = "2025-12-25 14:00";

        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 12, 25, 14, 0);
        BookCommand expectedCommand = new BookCommand(new Name(personName), clientName,
                expectedDateTime, "No description provided");

        assertParseSuccess(parser,
                " " + PREFIX_DATETIME + datetime + " " + PREFIX_CLIENT + clientName
                        + " " + PREFIX_NAME + personName,
                expectedCommand);
    }

    @Test
    public void parse_invalidClientName_failure() {
        String personName = "Alice Tan";
        String datetime = "2025-12-25 14:00";

        // Empty client name
        assertParseFailure(parser,
                " " + PREFIX_DATETIME + datetime + " " + PREFIX_CLIENT + ""
                        + " " + PREFIX_NAME + personName,
                Booking.MESSAGE_CONSTRAINTS_CLIENT);

        // Client name with only spaces
        assertParseFailure(parser,
                " " + PREFIX_DATETIME + datetime + " " + PREFIX_CLIENT + "   "
                        + " " + PREFIX_NAME + personName,
                Booking.MESSAGE_CONSTRAINTS_CLIENT);

        // Client name with only special characters
        assertParseFailure(parser,
                " " + PREFIX_DATETIME + datetime + " " + PREFIX_CLIENT + "!!!"
                        + " " + PREFIX_NAME + personName,
                Booking.MESSAGE_CONSTRAINTS_CLIENT);
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                BookCommand.MESSAGE_USAGE.toString());

        // Missing datetime
        assertParseFailure(parser,
                " " + PREFIX_CLIENT + "John Doe" + " " + PREFIX_NAME + "Alice Tan",
                expectedMessage);

        // Missing client name
        assertParseFailure(parser,
                " " + PREFIX_DATETIME + "2025-12-25 14:00" + " " + PREFIX_NAME + "Alice Tan",
                expectedMessage);

        // Missing person name
        assertParseFailure(parser,
                " " + PREFIX_DATETIME + "2025-12-25 14:00" + " " + PREFIX_CLIENT + "John Doe",
                expectedMessage);

        // All fields missing
        assertParseFailure(parser, "", expectedMessage);
    }

    @Test
    public void parse_invalidDateTime_failure() {
        String personName = "Alice Tan";
        String clientName = "John Doe";

        // Invalid format
        assertParseFailure(parser,
                " " + PREFIX_DATETIME + "25-12-2025 14:00" + " " + PREFIX_CLIENT + clientName
                        + " " + PREFIX_NAME + personName,
                "Invalid date/time format or value!\n"
                        + "Please use the format: YYYY-MM-DD HH:MM (e.g., 2024-12-25 14:30)");

        // Invalid date (February 31st doesn't exist)
        assertParseFailure(parser,
                " " + PREFIX_DATETIME + "2025-02-31 14:00" + " " + PREFIX_CLIENT + clientName
                        + " " + PREFIX_NAME + personName,
                "Invalid date \"February 31st 2025\", that date does not exist in the (Gregorian) calendar.");
    }

    @Test
    public void parse_preamblePresent_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                BookCommand.MESSAGE_USAGE.toString());

        // Non-empty preamble
        assertParseFailure(parser,
                "some random text " + PREFIX_DATETIME + "2025-12-25 14:00"
                        + " " + PREFIX_CLIENT + "John Doe" + " " + PREFIX_NAME + "Alice Tan",
                expectedMessage);
    }
}
