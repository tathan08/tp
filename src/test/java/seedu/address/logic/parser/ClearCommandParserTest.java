package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearCommand;

public class ClearCommandParserTest {

    private ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_withForceFlag_success() {
        assertParseSuccess(parser, " f/", new ClearCommand(true));
    }

    @Test
    public void parse_withoutForceFlag_success() {
        assertParseSuccess(parser, "", new ClearCommand(false));
    }

    @Test
    public void parse_extraArguments_success() {
        // Extra arguments after force flag are ignored
        assertParseSuccess(parser, " f/ extra", new ClearCommand(true));
    }
}
