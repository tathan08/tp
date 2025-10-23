package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ClientContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for
 * {@code FindCommand}.
 */
public class FindCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        ClientContainsKeywordsPredicate firstPredicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME,
                                        Collections.singletonList("first"));
        ClientContainsKeywordsPredicate secondPredicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME,
                                        Collections.singletonList("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different predicate -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_blankKeyword_allPersonsListed() {
        // Expect all persons to be shown
        int expectedListSize = model.getFilteredPersonList().size();
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedListSize);
        ClientContainsKeywordsPredicate predicate = preparePredicate(ClientContainsKeywordsPredicate.SearchType.NAME,
                                        "   "); // multiple spaces
        FindCommand command = new FindCommand(predicate);

        // When no valid keywords are given, predicate returns true for all
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(model.getFilteredPersonList(), expectedModel.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        ClientContainsKeywordsPredicate predicate = preparePredicate(ClientContainsKeywordsPredicate.SearchType.NAME,
                                        "Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_tagKeyword_personsFoundByTag() {
        // ALICE, BENSON and DANIEL have tag "friends" in TypicalPersons
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        ClientContainsKeywordsPredicate predicate = preparePredicate(ClientContainsKeywordsPredicate.SearchType.TAG,
                                        "friends");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_dateKeyword_personsFoundByBookingDate() {
        // Suppose FIONA and CARL have a booking on 2025-10-20
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);
        ClientContainsKeywordsPredicate predicate = preparePredicate(ClientContainsKeywordsPredicate.SearchType.DATE,
                                        "2025-10-20");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME, Arrays.asList("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    @Test
    public void constructor_nullPredicate_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new FindCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ClientContainsKeywordsPredicate predicate =
                new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.NAME, List.of("Alice"));
        FindCommand command = new FindCommand(predicate);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_samePredicate_returnsTrue() {
        ClientContainsKeywordsPredicate predicate =
                new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.NAME, List.of("Alice"));
        FindCommand command1 = new FindCommand(predicate);
        FindCommand command2 = new FindCommand(predicate);
        assertEquals(command1, command2);
    }

    @Test
    public void equals_differentPredicate_returnsFalse() {
        FindCommand command1 = new FindCommand(new ClientContainsKeywordsPredicate(
                ClientContainsKeywordsPredicate.SearchType.NAME, List.of("Alice")));
        FindCommand command2 = new FindCommand(new ClientContainsKeywordsPredicate(
                ClientContainsKeywordsPredicate.SearchType.NAME, List.of("Bob")));
        assertNotEquals(command1, command2);
    }

    @Test
    public void toString_includesPredicateDetails() {
        ClientContainsKeywordsPredicate predicate =
                new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.TAG, List.of("friends"));
        FindCommand command = new FindCommand(predicate);
        String result = command.toString();
        assertTrue(result.contains("friends"));
        assertTrue(result.contains("TAG"));
    }


    /**
     * Parses {@code userInput} into a {@code ClientContainsKeywordsPredicate}.
     */
    private ClientContainsKeywordsPredicate preparePredicate(ClientContainsKeywordsPredicate.SearchType type,
                                    String userInput) {
        return new ClientContainsKeywordsPredicate(type, Arrays.asList(userInput.trim().split("\\s+")));
    }
}
