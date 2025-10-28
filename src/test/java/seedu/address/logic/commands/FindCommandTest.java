package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ClientContainsKeywordsPredicate;
import seedu.address.model.person.Person;

/**
 * Integration tests for {@code FindCommand} with new OR semantics and wildcard
 * support.
 */
public class FindCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    // Helper to create a predicate from a search criteria map
    private ClientContainsKeywordsPredicate preparePredicate(Map<String, List<String>> criteria) {
        return new ClientContainsKeywordsPredicate(criteria);
    }

    @Test
    public void execute_singleName_returnsMatchingPerson() {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("name", List.of("Alice"));
        ClientContainsKeywordsPredicate predicate = preparePredicate(criteria);

        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = "Searching for contacts with:\n  Name containing: Alice\n"
                + String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertTrue(model.getFilteredPersonList().contains(ALICE));
    }

    @Test
    public void execute_singleTag_returnsMatchingPersons() {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("tag", List.of("friends"));
        ClientContainsKeywordsPredicate predicate = preparePredicate(criteria);

        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = "Searching for contacts with:\n  Tag containing: friends\n"
                + String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_singleDate_returnsMatchingPersons() {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("date", List.of("2026-10-20"));
        ClientContainsKeywordsPredicate predicate = preparePredicate(criteria);

        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = "Searching for contacts with:\n  Booking date: 2026-10-20\n"
                + String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleFields() {
        // Suppose we want to filter by name "Alice" and tag "friend"
        Map<String, List<String>> searchCriteria = new HashMap<>();
        searchCriteria.put("name", List.of("Alice"));
        searchCriteria.put("tag", List.of("friend"));

        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(searchCriteria);
        FindCommand findCommand = new FindCommand(predicate);

        // Create an expected model with the same AddressBook
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(predicate);

        // Execute the FindCommand - since we have multiple fields, one should have " OR" at the end
        CommandResult result = null;
        try {
            result = findCommand.execute(model);
        } catch (Exception e) {
            // Should not happen
        }

        // Verify the result contains " OR" since there are multiple criteria
        assertTrue(result.getFeedbackToUser().contains(" OR"));

        // Verify the model state matches expected
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());

        // Optionally, verify the filtered list contains the expected persons
        List<Person> filteredList = model.getFilteredPersonList();
        for (Person p : filteredList) {
            assertTrue(predicate.test(p));
        }
    }

    @Test
    public void execute_blankNameWildcard_listsAllPersons() {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("name", List.of()); // empty list = wildcard
        ClientContainsKeywordsPredicate predicate = preparePredicate(criteria);

        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = "Searching for contacts with:\n  Name containing: any\n"
                + String.format(MESSAGE_PERSONS_LISTED_OVERVIEW,
                model.getAddressBook().getPersonList().size());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals_samePredicate_returnsTrue() {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("name", List.of("Alice"));
        ClientContainsKeywordsPredicate predicate = preparePredicate(criteria);

        FindCommand command1 = new FindCommand(predicate);
        FindCommand command2 = new FindCommand(predicate);
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_differentPredicate_returnsFalse() {
        Map<String, List<String>> criteria1 = new HashMap<>();
        criteria1.put("name", List.of("Alice"));
        Map<String, List<String>> criteria2 = new HashMap<>();
        criteria2.put("name", List.of("Bob"));

        FindCommand command1 = new FindCommand(preparePredicate(criteria1));
        FindCommand command2 = new FindCommand(preparePredicate(criteria2));
        assertTrue(!command1.equals(command2));
    }

    @Test
    public void toString_containsPredicateDetails() {
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("tag", List.of("friends"));
        ClientContainsKeywordsPredicate predicate = preparePredicate(criteria);
        FindCommand command = new FindCommand(predicate);

        String str = command.toString();
        assertTrue(str.contains("friends"));
        assertTrue(str.contains("ClientContainsKeywordsPredicate"));
    }

}
