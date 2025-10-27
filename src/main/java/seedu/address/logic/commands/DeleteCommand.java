package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.booking.Booking;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person, tag or booking specified from the displayed person list.\n"
            + "Parameters:\n NAME (must be a saved contact)\n"
            + "TAG (optional, an available tag saved with the contact) \n"
            + "BOOKING (Optional, an available booking saved under the contact) \n"
            + "Example:\n" + COMMAND_WORD + " n/" + "Alex" + " (to delete a whole contact)\n"
            + COMMAND_WORD + " n/" + "Alex" + " t/tag_1 t/tag_2... (to delete 1 or more specific tags from 'Alex')\n"
            + COMMAND_WORD + " n/" + "Alex" + " b/booking_ID (to delete a specified booking with 'Alex')";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_PERSON_NOT_FOUND = "No such person found: %s";
    public static final String MESSAGE_DELETE_PERSON_MULTIPLE_MATCH = "Multiple matches for %s: \n%s\n";
    public static final String MESSAGE_DELETE_TAG_SUCCESS = "Removed tags %1$s from %2$s!";
    public static final String MESSAGE_DELETE_TAG_PARTIAL = "Removed %1$s. Not found: %2$s from %3$s";
    public static final String MESSAGE_DELETE_TAG_NOT_FOUND = "'%1$s' does not have the tag(s) '%2$s'";
    public static final String MESSAGE_DELETE_TAG_USAGE = "Please provide a tag after 't/'!";
    public static final String MESSAGE_DELETE_BOOKING_SUCCESS = "Removed booking: %1$s with %2$s for %3$s!";
    public static final String MESSAGE_DELETE_BOOKING_NOT_FOUND = "'%1$s' does not have booking ID %2$d!";
    public static final String MESSAGE_DELETE_BOOKING_USAGE = "Please provide a valid booking ID after b/!";
    public static final String MESSAGE_DELETE_BOOKING_OR_TAG = "Only use either 'b/' or 't/', and not both!";

    private static final Logger logger = LogsCenter.getLogger(DeleteCommand.class);

    private final Name targetName;
    private final Optional<Set<Tag>> tags;
    private final Integer targetBooking;

    /**
     * @brief               Constructor for deleting name or tags
     * @param targetName    Name of the contact we are deleting from
     * @param tags          Optionals containing the set of Tags we want to delete from the contact
     */
    public DeleteCommand(Name targetName, Optional<Set<Tag>> tags) {
        requireNonNull(targetName);
        requireNonNull(tags);
        this.targetName = targetName;
        this.tags = tags;
        this.targetBooking = 0;
    }

    /**
     * @brief               constructor for deleting bookings
     * @param targetName    the contact which we are deleting the booking from
     * @param targetBooking the booking ID which we want to delete
     */
    public DeleteCommand(Name targetName, Integer targetBooking) {
        requireNonNull(targetName);
        requireNonNull(targetBooking);
        this.targetName = targetName;
        this.tags = Optional.empty();
        this.targetBooking = targetBooking;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        logger.info(String.format("Executing DeleteCommand for person: %s", targetName.fullName));

        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToDelete = findUniquePerson(lastShownList, targetName);

        if (targetBooking > 0) {
            List<Booking> bookingList = personToDelete.getBookings();
            if (bookingList.size() < targetBooking) {
                throw new CommandException(String.format(MESSAGE_DELETE_BOOKING_NOT_FOUND,
                        personToDelete.getName().fullName, targetBooking));
            }
            List<Booking> newBookings = new ArrayList<>(bookingList);
            Booking removedBooking = newBookings.get(targetBooking - 1);
            newBookings.remove(targetBooking - 1);

            Person updatedPerson = new Person(
                    personToDelete.getName(),
                    personToDelete.getPhone(),
                    personToDelete.getEmail(),
                    personToDelete.getTags(),
                    newBookings
            );
            model.setPerson(personToDelete, updatedPerson);
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);

            return new CommandResult(String.format(MESSAGE_DELETE_BOOKING_SUCCESS, removedBooking.getDateTimeString(),
                    removedBooking.getClientName(), personToDelete.getName().fullName));
        }

        if (tags.isEmpty()) {
            // Full person deletion
            logger.info(String.format("Deleting entire person: %s", personToDelete.getName()));
            model.deletePerson(personToDelete);

            logger.info(String.format("Successfully deleted person: %s", personToDelete.getName()));
            return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
        }

        // Partial deletion (tags only)
        Set<Tag> tagsToRemove = tags.get();
        logger.info(String.format("Removing tags %s from person: %s", tagsToRemove, personToDelete.getName()));

        Set<Tag> curr = new LinkedHashSet<>(personToDelete.getTags());

        Set<Tag> present = new LinkedHashSet<>();
        Set<Tag> missing = new LinkedHashSet<>();

        for (Tag t : tagsToRemove) {
            if (curr.contains(t)) {
                present.add(t);
            } else {
                missing.add(t);
            }
        }

        if (present.isEmpty()) {
            logger.warning(String.format("No tags found to remove from person: %s", personToDelete.getName()));
            throw new CommandException(String.format(MESSAGE_DELETE_TAG_NOT_FOUND,
                    personToDelete.getName().fullName, missing));
        }

        curr.removeAll(present);

        Person updatedPerson = new Person(
                personToDelete.getName(),
                personToDelete.getPhone(),
                personToDelete.getEmail(),
                curr,
                personToDelete.getBookings()
        );

        model.setPerson(personToDelete, updatedPerson);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);

        String removed = present.stream().map(Tag::toString).collect(Collectors.joining(", "));
        logger.info(String.format("Successfully removed tags %s from person: %s", removed, personToDelete.getName()));

        if (!missing.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_DELETE_TAG_PARTIAL, present,
                    missing, personToDelete.getName().fullName));
        }

        return new CommandResult(String.format(MESSAGE_DELETE_TAG_SUCCESS, removed, updatedPerson.getName().fullName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetName.equals(otherDeleteCommand.targetName) && tags.equals(otherDeleteCommand.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetName, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", targetName)
                .toString();
    }

    /**
     * @brief           This method checks if target name is contained in another name.
     * @param name      current name in list
     * @param target    name that we want to find
     * @return          true if current name in list contains the target, else return false
     */
    public Boolean containsIgnoreCase(Name name, Name target) {
        if (name == null) {
            return false;
        }
        String firstName = name.fullName.toLowerCase();
        String targetNameLower = target.fullName.toLowerCase();
        return firstName.contains(targetNameLower);
    }

    private Person findUniquePerson(List<Person> list, Name targetName) throws CommandException {
        String queryName = targetName.toString().trim().replaceAll("\\s+", " ");

        List<Person> exactMatch = list.stream()
                .filter(x -> x.getName().toString().replaceAll("\\s+", " ")
                        .equalsIgnoreCase(queryName))
                .toList();
        if (exactMatch.size() == 1) {
            logger.fine(String.format("Found exact match for person: %s", targetName.fullName));
            return exactMatch.get(0);
        }
        if (exactMatch.size() > 1) {
            logger.warning(String.format("Multiple exact matches found for person: %s", targetName.fullName));
            String allMatches = exactMatch.stream()
                    .map(Messages::format)
                    .collect(Collectors.joining("\n"));
            throw new CommandException(
                    String.format(MESSAGE_DELETE_PERSON_MULTIPLE_MATCH, targetName.fullName, allMatches));
        }

        List<Person> contains = list.stream()
                .filter(x -> x.getName().toString().toLowerCase().replaceAll("\\s+", " ")
                        .contains(queryName.toLowerCase()))
                .toList();
        if (contains.size() == 1) {
            logger.fine(String.format("Found partial match for person: %s", targetName.fullName));
            return contains.get(0);
        }

        if (contains.isEmpty()) {
            logger.warning(String.format("No person found matching: %s", targetName.fullName));
            throw new CommandException(String.format(MESSAGE_DELETE_PERSON_NOT_FOUND, targetName.fullName));
        }

        logger.warning(String.format("Multiple partial matches found for person: %s", targetName.fullName));
        String containsMultiple = contains.stream()
                .map(Messages::format)
                .collect(Collectors.joining("\n"));
        throw new CommandException(String.format(MESSAGE_DELETE_PERSON_MULTIPLE_MATCH,
                targetName.fullName, containsMultiple));
    }
}
