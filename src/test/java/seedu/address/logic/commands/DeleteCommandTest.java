package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.booking.Booking;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validDeletion_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getName(), Optional.empty());

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidNameUnfilteredList_throwsCommandException() {
        Name nonexistentName = new Name("Nonexistent Person");
        DeleteCommand deleteCommand = new DeleteCommand(nonexistentName, Optional.empty());

        assertCommandFailure(deleteCommand, model,
                String.format(DeleteCommand.MESSAGE_DELETE_PERSON_NOT_FOUND, nonexistentName.fullName));
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getName(), Optional.empty());

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    // Tests for deleting tags
    @Test
    public void execute_deleteMultipleTags_success() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        //Create new tags
        Set<Tag> targetTag = new LinkedHashSet<>();
        targetTag.add(new Tag("tagOne"));
        targetTag.add(new Tag("tagTwo"));

        //Add tags to target
        Person newPerson = new Person(
                target.getName(),
                target.getPhone(),
                target.getEmail(),
                targetTag,
                target.getBookings()
        );
        model.setPerson(target, newPerson);

        DeleteCommand deleteCommand = new DeleteCommand(newPerson.getName(),
                Optional.of(new LinkedHashSet<>(targetTag)));

        deleteCommand.execute(model);

        Person after = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(after.getTags().isEmpty());
    }

    @Test
    public void execute_deleteSomeTags_success() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        //Create new tags
        Set<Tag> targetTag = new LinkedHashSet<>();
        targetTag.add(new Tag("tagOne"));
        targetTag.add(new Tag("tagTwo"));
        targetTag.add(new Tag("tagThree"));

        Set<Tag> testTag = Set.of(new Tag("tagOne"), new Tag("tagTwo"));

        //Add tags to target
        Person newPerson = new Person(
                target.getName(),
                target.getPhone(),
                target.getEmail(),
                targetTag,
                target.getBookings()
        );
        model.setPerson(target, newPerson);

        DeleteCommand deleteCommand = new DeleteCommand(newPerson.getName(),
                Optional.of(new LinkedHashSet<>(testTag)));

        deleteCommand.execute(model);

        Person after = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(after.getTags().contains(new Tag("tagThree")));
        assertFalse(after.getTags().contains(new Tag("tagOne")));
        assertFalse(after.getTags().contains(new Tag("tagTwo")));
    }

    @Test
    public void execute_deleteTags_someMissing() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        //Create new tag
        Set<Tag> targetTag = new LinkedHashSet<>();
        targetTag.add(new Tag("tag"));

        Set<Tag> testTag = Set.of(new Tag("tag"), new Tag("vip"), new Tag("friends"));

        //Add tag to target
        Person newPerson = new Person(
                target.getName(),
                target.getPhone(),
                target.getEmail(),
                targetTag,
                target.getBookings()
        );
        model.setPerson(target, newPerson);

        DeleteCommand deleteCommand = new DeleteCommand(newPerson.getName(),
                Optional.of(new LinkedHashSet<>(testTag)));

        CommandResult res = deleteCommand.execute(model);
        String msg = res.getFeedbackToUser().toLowerCase();

        Person after = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(msg.contains("not found:"));
        assertTrue(msg.contains("vip"));
        assertTrue(msg.contains("friends"));
        assertFalse(after.getTags().contains(new Tag("tag")));
    }

    @Test
    public void execute_deleteTag_allMissing() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        //Create new tag
        Set<Tag> targetTag = new LinkedHashSet<>();
        targetTag.add(new Tag("tag"));

        Set<Tag> testTag = Set.of(new Tag("vip"), new Tag("friends"));

        //Add tag to target
        Person newPerson = new Person(
                target.getName(),
                target.getPhone(),
                target.getEmail(),
                targetTag,
                target.getBookings()
        );
        model.setPerson(target, newPerson);

        DeleteCommand deleteCommand = new DeleteCommand(newPerson.getName(),
                Optional.of(new LinkedHashSet<>(testTag)));

        assertThrows(CommandException.class, () -> deleteCommand.execute(model));
    }

    // Tests for deleting bookings
    @Test
    public void execute_deleteBooking_success() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        //Create new booking
        LocalDateTime targetTiming = LocalDateTime.of(3000, 10, 10, 10, 10);
        Booking booking = new Booking("mr tan", targetTiming, "meeting");

        List<Booking> targetBooking = List.of(booking);

        //Add booking to target
        Person newPerson = new Person(
                target.getName(),
                target.getPhone(),
                target.getEmail(),
                target.getTags(),
                targetBooking
        );
        model.setPerson(target, newPerson);

        //Attempt to delete booking
        DeleteCommand deleteCommand = new DeleteCommand(newPerson.getName(), 1);

        deleteCommand.execute(model);

        Person after = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertFalse(after.getBookings().contains(booking));
    }

    @Test
    public void execute_deleteBooking_noSuchID() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        //Create new booking
        LocalDateTime targetTiming = LocalDateTime.of(3000, 10, 10, 10, 10);
        Booking booking = new Booking("mr tan", targetTiming, "meeting");

        List<Booking> targetBooking = List.of(booking);

        //Add booking to target
        Person newPerson = new Person(
                target.getName(),
                target.getPhone(),
                target.getEmail(),
                target.getTags(),
                targetBooking
        );
        model.setPerson(target, newPerson);

        //Attempt to delete a booking
        DeleteCommand deleteCommand = new DeleteCommand(newPerson.getName(), 2);

        assertThrows(CommandException.class, () -> deleteCommand.execute(model));
    }

    @Test
    public void execute_deleteB1_bugExists() throws CommandException {
        // BUG DEMONSTRATION TEST
        // This test exposes a bug where b/1 refers to storage order, not display order
        
        // UI displays bookings with: Future first (ID 1), then Past (ID 2)
        // But DeleteCommand uses: Storage order (first added is ID 1)
        
        // Scenario: Add past booking, then future booking
        // User sees in UI: ID 1 = Future booking, ID 2 = Past booking
        // User deletes b/1 expecting to delete Future booking
        // BUG: Actually deletes Past booking (first in storage order)
        
        // Create a fresh person with no existing bookings
        Person personWithNoBookings = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Name originalName = personWithNoBookings.getName();
        
        // Create a unique name
        Person freshPerson = new Person(
                new Name(originalName.fullName + "Bug"),
                personWithNoBookings.getPhone(),
                personWithNoBookings.getEmail(),
                personWithNoBookings.getTags(),
                new ArrayList<>()
        );

        model.addPerson(freshPerson);

        // Add a past booking first (stored at index 0)
        LocalDateTime pastDateTime = LocalDateTime.of(2020, 1, 15, 10, 0);
        Booking pastBooking = new Booking("Past Client", pastDateTime, "Past meeting");
        
        // Add a future booking second (stored at index 1)
        LocalDateTime futureDateTime = LocalDateTime.of(2026, 12, 25, 14, 30);
        Booking futureBooking = new Booking("Future Client", futureDateTime, "Future meeting");
        
        List<Booking> bookings = new ArrayList<>();
        bookings.add(pastBooking);
        bookings.add(futureBooking);

        Person personWithBothBookings = new Person(
                freshPerson.getName(),
                freshPerson.getPhone(),
                freshPerson.getEmail(),
                freshPerson.getTags(),
                bookings
        );
        model.setPerson(freshPerson, personWithBothBookings);

        // Verify both bookings exist
        Person beforeDeletion = model.getFilteredPersonList().stream()
                .filter(p -> p.getName().equals(freshPerson.getName()))
                .findFirst()
                .orElse(null);
        assertTrue(beforeDeletion != null, "Person should exist");
        assertEquals(2, beforeDeletion.getBookings().size(), "Should have 2 bookings");
        
        // BUG: In UI, b/1 should refer to the FUTURE booking (shown first)
        // But DeleteCommand uses storage order, so b/1 actually deletes the PAST booking
        // This test demonstrates the incorrect behavior
        DeleteCommand deleteCommand = new DeleteCommand(personWithBothBookings.getName(), 1);
        deleteCommand.execute(model);

        // Verify which booking remains after deleting b/1
        Person after = model.getFilteredPersonList().stream()
                .filter(p -> p.getName().equals(freshPerson.getName()))
                .findFirst()
                .orElse(null);
        assertTrue(after != null, "Person should still exist");
        assertEquals(1, after.getBookings().size(), "Should have only 1 booking remaining");
        
        Booking remainingBooking = after.getBookings().get(0);
        
        // EXPECTED BEHAVIOR: After deleting b/1 (which should delete future booking in UI),
        // the PAST booking should remain
        // This test will FAIL because b/1 currently deletes the past booking (bug)
        assertEquals(pastBooking, remainingBooking, 
                "EXPECTED: After deleting b/1, the past booking should remain (but bug causes future to remain)");
        assertFalse(Booking.isFutureDateTime(remainingBooking.getDateTime()), 
                "EXPECTED: Remaining booking should be past (but bug causes it to be future)");
    }



    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(new Name("Alex Yeoh"), Optional.empty());
        DeleteCommand deleteSecondCommand = new DeleteCommand(new Name("Bernice Tan"), Optional.empty());

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(new Name("Alex Yeoh"), Optional.empty());
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Name targetName = new Name("Alex Yeoh");
        DeleteCommand deleteCommand = new DeleteCommand(targetName, Optional.empty());
        String expected = DeleteCommand.class.getCanonicalName() + "{targetName=" + targetName + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
