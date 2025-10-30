package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ABHIJAY;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    // all fields specified on unfiltered list (happy path)
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        // Use ALICE's name as the old name
        EditCommand editCommand = new EditCommand(ALICE.getName(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                seedu.address.logic.Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    // some fields specified on unfiltered list
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        // Use GEORGE (the last person in typical persons list)
        Person lastPerson = GEORGE;

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(lastPerson.getName(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                seedu.address.logic.Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    // no fields specified retains existing values
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(ALICE.getName(), new EditPersonDescriptor());
        Person editedPerson = ALICE;

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                seedu.address.logic.Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    // edit within filtered list succeeds
    public void execute_filteredList_success() {
        Person personInFilteredList = ALICE;
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(ALICE.getName(),
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                seedu.address.logic.Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    // editing results in duplicate person (unfiltered)
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = ALICE;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(BENSON.getName(), descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    // editing within filtered list results in duplicate
    public void execute_duplicatePersonFilteredList_failure() {
        // edit person in filtered list into a duplicate in address book
        Person personInList = BENSON;
        EditCommand editCommand = new EditCommand(ALICE.getName(),
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    // target person (by old name) not found
    public void execute_personNotFoundUnfilteredList_failure() {
        Name nonExistentName = new Name("Nonexistent Person");
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(nonExistentName, descriptor);

        assertCommandFailure(editCommand, model,
                String.format(EditCommand.MESSAGE_PERSON_NOT_FOUND, nonExistentName.fullName));
    }

    @Test
    // bookings are preserved after editing non-booking fields
    public void execute_editPersonWithBookings_bookingsPreserved() {
        // CARL has bookings in the typical address book
        Person personWithBookings = model.getFilteredPersonList().stream()
                .filter(person -> person.getName().fullName.equals("Carl Kurz"))
                .findFirst()
                .orElseThrow();

        // Edit Carl's phone and email
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone("98765432")
                .withEmail("newemail@example.com")
                .build();
        EditCommand editCommand = new EditCommand(personWithBookings.getName(), descriptor);

        Person editedPerson = new PersonBuilder(personWithBookings)
                .withPhone("98765432")
                .withEmail("newemail@example.com")
                .build();

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                seedu.address.logic.Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithBookings, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);

        // Verify that bookings are preserved
        Person updatedPerson = model.getFilteredPersonList().stream()
                .filter(person -> person.getEmail() != null
                        && person.getEmail().value.equals("newemail@example.com"))
                .findFirst()
                .orElseThrow();

        assertEquals(personWithBookings.getBookings().size(), updatedPerson.getBookings().size());
        assertEquals(personWithBookings.getBookings(), updatedPerson.getBookings());
    }

    @Test
    // edit succeeds for person with slash in name
    public void execute_editPersonWithSlash_success() {
        // Edit a person with slash in name
        Person personWithSlash = ABHIJAY;
        Person editedPerson = new PersonBuilder(personWithSlash)
                .withPhone("98888888")
                .withEmail("newabhijay@example.com")
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone("98888888")
                .withEmail("newabhijay@example.com")
                .build();

        EditCommand editCommand = new EditCommand(personWithSlash.getName(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                seedu.address.logic.Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithSlash, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(new Name(VALID_NAME_AMY), DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(new Name(VALID_NAME_AMY), copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand(true)));

        // different name -> returns false
        assertFalse(standardCommand.equals(new EditCommand(new Name(VALID_NAME_BOB), DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(new Name(VALID_NAME_AMY), DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Name name = new Name(VALID_NAME_AMY);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(name, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{oldName=" + name + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
