package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BOOKING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.ErrorMessage;
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

    public static final ErrorMessage MESSAGE_USAGE = new ErrorMessage(
            "Deletes the person, tag or booking specified in the address book.",
            PREFIX_NAME + "NAME ["
                    + PREFIX_TAG + "TAG]... OR [" + PREFIX_BOOKING + "BOOKING ID]",
            "\n"
                    + COMMAND_WORD + " " + PREFIX_NAME + "Alex (to delete a whole contact)\n"
                    + COMMAND_WORD + " " + PREFIX_NAME + "Alex "
                    + PREFIX_TAG + "tag1 " + PREFIX_TAG + "tag2 (to delete specific tags)\n"
                    + COMMAND_WORD + " " + PREFIX_NAME + "Alex "
                    + PREFIX_BOOKING + "1 (to delete booking with ID 1)"
    );

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_PERSON_NOT_FOUND =
            "Could not find anyone named '%s' in your address book.\n"
            + "Please check the name and try again.";
    public static final String MESSAGE_DELETE_PERSON_MULTIPLE_MATCH =
            "Found multiple people matching '%s':\n%s\n"
            + "Please be more specific with the full name.";
    public static final String MESSAGE_DELETE_PERSON_PARTIAL_FOUND =
            "We found someone whose name contains '%1$s': '%2$s'\n"
            + "Did you mean this person? Please use their full name to delete.";
    public static final String MESSAGE_DELETE_TAG_SUCCESS = "Removed tags %1$s from %2$s!";
    public static final String MESSAGE_DELETE_TAG_PARTIAL = "Removed %1$s.\n"
            + "However, these tags were not found: %2$s from %3$s";
    public static final String MESSAGE_DELETE_TAG_NOT_FOUND = "'%1$s' doesn't have the tag(s) '%2$s'.\n"
            + "Please check the tag names and try again.";
    public static final String MESSAGE_DELETE_TAG_NO_SPACES = "Tag names cannot contain spaces!\n"
            + "If you want to delete multiple tags, use separate t/ prefixes.\n"
            + "Example: delete n/John t/friend t/colleague";
    public static final String MESSAGE_DELETE_TAG_USAGE = "Please specify a tag name after 't/'!";
    public static final String MESSAGE_DELETE_BOOKING_SUCCESS = "Removed booking: %1$s with %2$s for %3$s!";
    public static final String MESSAGE_DELETE_BOOKING_NOT_FOUND =
            "'%1$s' doesn't have a booking with ID %2$d.\n"
            + "Please check the booking ID and try again.";
    public static final String MESSAGE_DELETE_BOOKING_USAGE = "Please provide a valid booking ID after 'b/'!";
    public static final String MESSAGE_DELETE_BOOKING_OR_TAG =
            "You can only delete either tags (t/) OR bookings (b/), not both at the same time!";

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

            // Sort bookings using the same comparator as the UI
            // Future bookings first (ascending), then past bookings (ascending)
            Comparator<Booking> bookingComparator = (b1, b2) -> {
                boolean b1IsFuture = Booking.isFutureDateTime(b1.getDateTime());
                boolean b2IsFuture = Booking.isFutureDateTime(b2.getDateTime());

                if (b1IsFuture && b2IsFuture) {
                    // Both future: sort ascending
                    return b1.getDateTime().compareTo(b2.getDateTime());
                } else if (!b1IsFuture && !b2IsFuture) {
                    // Both past: sort ascending
                    return b1.getDateTime().compareTo(b2.getDateTime());
                } else {
                    // One future, one past: future comes first
                    return b1IsFuture ? -1 : 1;
                }
            };

            // Sort the booking list to match display order before accessing by index
            List<Booking> sortedBookings = new ArrayList<>(bookingList);
            sortedBookings.sort(bookingComparator);

            if (sortedBookings.size() < targetBooking) {
                throw new CommandException(String.format(MESSAGE_DELETE_BOOKING_NOT_FOUND,
                        personToDelete.getName().fullName, targetBooking));
            }

            // Get the booking to remove based on display order
            Booking removedBooking = sortedBookings.get(targetBooking - 1);

            // Remove the booking from the original storage order
            List<Booking> newBookings = new ArrayList<>(bookingList);
            newBookings.remove(removedBooking);

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
                        .equals(queryName))
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
                .filter(x -> x.getName().toString().replaceAll("\\s+", " ")
                        .contains(queryName))
                .toList();
        if (contains.size() == 1) {
            logger.fine(String.format("Found partial match for person: %s", targetName.fullName));
            throw new CommandException(
                    String.format(MESSAGE_DELETE_PERSON_PARTIAL_FOUND, queryName, contains.get(0).getName()));
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
