package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_addsTagsToExistingPerson() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person personWithNewTags = new PersonBuilder()
                .withName(personInList.getName().fullName)
                .withPhone(personInList.getPhone() != null ? personInList.getPhone().value : null)
                .withEmail(personInList.getEmail() != null ? personInList.getEmail().value : null)
                .withTags("newTag")
                .build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        // Create updated person with merged tags
        java.util.Set<seedu.address.model.tag.Tag> mergedTags = new java.util.LinkedHashSet<>(personInList.getTags());
        mergedTags.addAll(personWithNewTags.getTags());
        Person updatedPerson = new Person(
                personInList.getName(),
                personInList.getPhone(),
                personInList.getEmail(),
                mergedTags,
                personInList.getBookings()
        );
        expectedModel.setPerson(personInList, updatedPerson);

        assertCommandSuccess(new AddCommand(personWithNewTags), model,
                String.format(AddCommand.MESSAGE_TAGS_ADDED, Messages.format(updatedPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePersonNoTags_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person personWithoutTags = new PersonBuilder()
                .withName(personInList.getName().fullName)
                .withPhone(personInList.getPhone() != null ? personInList.getPhone().value : null)
                .withEmail(personInList.getEmail() != null ? personInList.getEmail().value : null)
                .build(); // No tags

        assertCommandFailure(new AddCommand(personWithoutTags), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

}
