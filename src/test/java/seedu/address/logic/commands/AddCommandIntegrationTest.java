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

    @Test
    public void execute_duplicatePersonWithDuplicateTags_addsOnlyNewTags() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person personWithMixedTags = new PersonBuilder()
                .withName(personInList.getName().fullName)
                .withPhone(personInList.getPhone() != null ? personInList.getPhone().value : null)
                .withEmail(personInList.getEmail() != null ? personInList.getEmail().value : null)
                .withTags("newTag1", "newTag2")
                .build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        // Create updated person with merged tags
        java.util.Set<seedu.address.model.tag.Tag> mergedTags = new java.util.LinkedHashSet<>(personInList.getTags());
        mergedTags.addAll(personWithMixedTags.getTags());
        Person updatedPerson = new Person(
                personInList.getName(),
                personInList.getPhone(),
                personInList.getEmail(),
                mergedTags,
                personInList.getBookings()
        );
        expectedModel.setPerson(personInList, updatedPerson);

        assertCommandSuccess(new AddCommand(personWithMixedTags), model,
                String.format(AddCommand.MESSAGE_TAGS_ADDED, Messages.format(updatedPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePersonExceedsTagLimit_throwsCommandException() {
        // Create a person with many tags
        Person personWithManyTags = new PersonBuilder()
                .withName("TestPerson")
                .withTags("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10",
                         "tag11", "tag12", "tag13", "tag14", "tag15", "tag16", "tag17", "tag18", "tag19", "tag20")
                .build();

        // Add this person first
        model.addPerson(personWithManyTags);

        // Try to add more tags (would exceed limit)
        Person personWithExtraTags = new PersonBuilder()
                .withName("TestPerson")
                .withPhone(personWithManyTags.getPhone() != null ? personWithManyTags.getPhone().value : null)
                .withEmail(personWithManyTags.getEmail() != null ? personWithManyTags.getEmail().value : null)
                .withTags("tag21")
                .build();

        assertCommandFailure(new AddCommand(personWithExtraTags), model,
                "Tag limit reached for TestPerson. Maximum 20 tags allowed. "
                + "Current tags: 20, trying to add: 1. Remove existing tags before adding new ones.");
    }

    @Test
    public void execute_newPersonWithTags_addsPersonSuccessfully() {
        Person newPerson = new PersonBuilder()
                .withName("NewPerson")
                .withPhone("12345678")
                .withEmail("new@example.com")
                .withTags("friends", "colleagues")
                .build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(newPerson);

        assertCommandSuccess(new AddCommand(newPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(newPerson)),
                expectedModel);
    }

    @Test
    public void execute_newPersonWithoutTags_addsPersonSuccessfully() {
        Person newPerson = new PersonBuilder()
                .withName("NewPerson")
                .withPhone("12345678")
                .withEmail("new@example.com")
                .build(); // No tags

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(newPerson);

        assertCommandSuccess(new AddCommand(newPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(newPerson)),
                expectedModel);
    }

    @Test
    public void execute_newPersonWithSlashesInName_addsPersonSuccessfully() {
        Person personWithSlash = new PersonBuilder()
                .withName("Raj s/o Kumar")
                .withPhone("81234567")
                .withEmail("raj@example.com")
                .withTags("family")
                .build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(personWithSlash);

        assertCommandSuccess(new AddCommand(personWithSlash), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(personWithSlash)),
                expectedModel);
    }

}
