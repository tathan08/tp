package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();

        CommandResult commandResult = new AddCommand(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_duplicatePerson_addsTagsToExistingPerson() throws Exception {
        Person existingPerson = new PersonBuilder().withName("Alice").withTags("friends").build();
        Person personWithNewTags = new PersonBuilder().withName("Alice").withTags("colleagues").build();
        AddCommand addCommand = new AddCommand(personWithNewTags);
        ModelStubWithPerson modelStub = new ModelStubWithPerson(existingPerson);

        CommandResult commandResult = addCommand.execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_TAGS_ADDED, Messages.format(modelStub.getPerson())),
                commandResult.getFeedbackToUser());
        assertTrue(modelStub.isPersonUpdated());
    }

    @Test
    public void execute_duplicatePersonNoTags_throwsCommandException() {
        Person existingPerson = new PersonBuilder().withName("Alice").withTags("friends").build();
        Person personWithoutTags = new PersonBuilder().withName("Alice").build(); // No tags
        AddCommand addCommand = new AddCommand(personWithoutTags);
        ModelStubWithPerson modelStub = new ModelStubWithPerson(existingPerson);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_duplicatePersonWithTags_addsTagsSuccessfully() throws Exception {
        Person existingPerson = new PersonBuilder().withName("Alice").withTags("friends").build();
        Person personWithNewTags = new PersonBuilder().withName("Alice").withTags("colleagues").build();
        AddCommand addCommand = new AddCommand(personWithNewTags);
        ModelStubWithPerson modelStub = new ModelStubWithPerson(existingPerson);

        CommandResult commandResult = addCommand.execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_TAGS_ADDED, Messages.format(modelStub.getPerson())),
                commandResult.getFeedbackToUser());
        assertTrue(modelStub.isPersonUpdated());
    }

    @Test
    public void execute_duplicatePersonWithDuplicateTags_addsOnlyNewTags() throws Exception {
        Person existingPerson = new PersonBuilder().withName("Alice").withTags("friends", "colleagues").build();
        Person personWithMixedTags = new PersonBuilder().withName("Alice").withTags("friends", "client").build();
        AddCommand addCommand = new AddCommand(personWithMixedTags);
        ModelStubWithPerson modelStub = new ModelStubWithPerson(existingPerson);

        CommandResult commandResult = addCommand.execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_TAGS_ADDED, Messages.format(modelStub.getPerson())),
                commandResult.getFeedbackToUser());
        assertTrue(modelStub.isPersonUpdated());

        // Verify that the updated person has all unique tags
        Person updatedPerson = modelStub.getPerson();
        assertEquals(3, updatedPerson.getTags().size()); // friends, colleagues, client
        assertTrue(updatedPerson.getTags().contains(new seedu.address.model.tag.Tag("friends")));
        assertTrue(updatedPerson.getTags().contains(new seedu.address.model.tag.Tag("colleagues")));
        assertTrue(updatedPerson.getTags().contains(new seedu.address.model.tag.Tag("client")));
    }

    @Test
    public void execute_duplicatePersonExceedsTagLimit_throwsCommandException() {
        // Create a person with 19 tags (1 below limit)
        String[] manyTagNames = new String[19];
        for (int i = 0; i < 19; i++) {
            manyTagNames[i] = "tag" + (i + 1);
        }
        Person existingPerson = new PersonBuilder().withName("Alice").withTags(manyTagNames).build();

        // Try to add 2 more tags (would exceed 20 limit)
        Person personWithTooManyTags = new PersonBuilder().withName("Alice").withTags("tag20", "tag21").build();
        AddCommand addCommand = new AddCommand(personWithTooManyTags);
        ModelStubWithPerson modelStub = new ModelStubWithPerson(existingPerson);

        assertThrows(CommandException.class, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_duplicatePersonAtTagLimit_throwsCommandException() {
        // Create a person with exactly 20 tags (at limit)
        String[] maxTagNames = new String[20];
        for (int i = 0; i < 20; i++) {
            maxTagNames[i] = "tag" + (i + 1);
        }
        Person existingPerson = new PersonBuilder().withName("Alice").withTags(maxTagNames).build();

        // Try to add 1 more tag (would exceed limit)
        Person personWithExtraTag = new PersonBuilder().withName("Alice").withTags("tag21").build();
        AddCommand addCommand = new AddCommand(personWithExtraTag);
        ModelStubWithPerson modelStub = new ModelStubWithPerson(existingPerson);

        assertThrows(CommandException.class, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_duplicatePersonWithEmptyTagSet_throwsCommandException() {
        Person existingPerson = new PersonBuilder().withName("Alice").withTags("friends").build();
        Person personWithEmptyTags = new PersonBuilder().withName("Alice").withTags().build(); // Empty tag set
        AddCommand addCommand = new AddCommand(personWithEmptyTags);
        ModelStubWithPerson modelStub = new ModelStubWithPerson(existingPerson);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_newPersonWithTags_addsPersonSuccessfully() throws Exception {
        Person newPerson = new PersonBuilder().withName("Bob").withTags("friends", "colleagues").build();
        AddCommand addCommand = new AddCommand(newPerson);
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        CommandResult commandResult = addCommand.execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(newPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(1, modelStub.personsAdded.size());
        assertEquals(newPerson, modelStub.personsAdded.get(0));
    }

    @Test
    public void execute_newPersonWithoutTags_addsPersonSuccessfully() throws Exception {
        Person newPerson = new PersonBuilder().withName("Bob").build(); // No tags
        AddCommand addCommand = new AddCommand(newPerson);
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();

        CommandResult commandResult = addCommand.execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(newPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(1, modelStub.personsAdded.size());
        assertEquals(newPerson, modelStub.personsAdded.get(0));
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different person -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Person person;
        private Person updatedPerson;
        private boolean personUpdated = false;

        ModelStubWithPerson(Person person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return this.person.isSamePerson(person);
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            requireNonNull(editedPerson);
            this.updatedPerson = editedPerson;
            this.personUpdated = true;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return javafx.collections.FXCollections.observableArrayList(person);
        }

        public Person getPerson() {
            return updatedPerson != null ? updatedPerson : person;
        }

        public boolean isPersonUpdated() {
            return personUpdated;
        }
    }

    /**
     * A Model stub that always accept the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return personsAdded.stream().anyMatch(person::isSamePerson);
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
