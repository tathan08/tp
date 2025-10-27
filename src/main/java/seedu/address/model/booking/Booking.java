package seedu.address.model.booking;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a Booking in the address book.
 * Guarantees: immutable; fields are validated and not null.
 */
public class Booking {
    public static final String MESSAGE_CONSTRAINTS_DATETIME =
            "Invalid date/time format or value!\n"
            + "Please use the format: YYYY-MM-DD HH:MM (e.g., 2024-12-25 14:30)\n"
            + "The booking must be scheduled for a future date and time.";
    public static final String MESSAGE_CONSTRAINTS_PAST_DATETIME =
            "Booking date and time must be in the future.\n"
            + "You cannot create bookings for past dates.";
    public static final String MESSAGE_CONSTRAINTS_CLIENT =
            "Client name is invalid!\n"
            + "Requirements:\n"
            + "• Must be 1-100 characters long\n"
            + "• Must contain at least one letter\n"
            + "• Can include letters, numbers, spaces, hyphens (-), apostrophes ('), periods (.), and slashes (/)\n"
            + "Examples: 'John Doe', 'Mary-Jane O'Brien', 'Ahmad S/O Rahman'";
    public static final String MESSAGE_CONSTRAINTS_DESCRIPTION =
            "Booking description must be between 1 and 500 characters long.";

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    // Validation regex for client name
    private static final String CLIENT_NAME_VALIDATION_REGEX = "^[a-zA-Z0-9 .'\\\\/\\-]+$";

    private static final AtomicLong idCounter = new AtomicLong(1);

    private final String id;
    private final String clientName;
    private final LocalDateTime datetime;
    private final String description;

    /**
     * Constructs a {@code Booking} with auto-generated ID.
     *
     * @param clientName Client name for the booking.
     * @param datetime DateTime of the booking.
     * @param description Description of the booking.
     */
    public Booking(String clientName, LocalDateTime datetime, String description) {
        this(String.valueOf(idCounter.getAndIncrement()), clientName, datetime, description);
    }

    /**
     * Constructs a {@code Booking} with specified ID (for loading from storage).
     *
     * @param id Unique identifier for the booking.
     * @param clientName Client name for the booking.
     * @param datetime DateTime of the booking.
     * @param description Description of the booking.
     */
    public Booking(String id, String clientName, LocalDateTime datetime, String description) {
        requireNonNull(id);
        requireNonNull(clientName);
        requireNonNull(datetime);
        requireNonNull(description);

        checkArgument(isValidClientName(clientName), MESSAGE_CONSTRAINTS_CLIENT);
        checkArgument(isValidDescription(description), MESSAGE_CONSTRAINTS_DESCRIPTION);

        this.id = id;
        this.clientName = clientName.trim().replaceAll("\\s+", " "); // Normalize spaces
        this.datetime = datetime;
        this.description = description.trim();

        // Update counter if loading from storage
        try {
            long loadedId = Long.parseLong(id);
            idCounter.updateAndGet(current -> Math.max(current, loadedId + 1));
        } catch (NumberFormatException e) {
            // Non-numeric ID, ignore
        }
    }

    /**
     * Returns true if a given string is a valid client name.
     */
    public static boolean isValidClientName(String test) {
        if (test == null || test.trim().isEmpty()) {
            return false;
        }
        String trimmed = test.trim();
        if (trimmed.length() < 1 || trimmed.length() > 100) {
            return false;
        }
        if (!trimmed.matches(CLIENT_NAME_VALIDATION_REGEX)) {
            return false;
        }
        // Must contain at least one letter
        return trimmed.matches(".*[a-zA-Z].*");
    }

    /**
     * Returns true if a given string is a valid description.
     */
    public static boolean isValidDescription(String test) {
        if (test == null) {
            return false;
        }
        String trimmed = test.trim();
        return trimmed.length() >= 1 && trimmed.length() <= 500;
    }

    /**
     * Parses a datetime string in the format YYYY-MM-DD HH:MM.
     * Returns null if parsing fails.
     */
    public static LocalDateTime parseDateTime(String datetimeStr) {
        try {
            return LocalDateTime.parse(datetimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Validates a datetime string and returns a specific error message if invalid.
     * Returns null if the datetime string is valid.
     *
     * @param datetimeStr The datetime string to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateDateTime(String datetimeStr) {
        LocalDateTime parsed = parseDateTime(datetimeStr);
        if (parsed == null) {
            // Try to extract the date part for a better error message
            if (datetimeStr.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}")) {
                String datePart = datetimeStr.split("\\s+")[0];
                String[] parts = datePart.split("-");
                if (parts.length == 3) {
                    try {
                        int year = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);
                        int day = Integer.parseInt(parts[2]);
                        String monthName = getMonthName(month);
                        String dayOrdinal = getDayOrdinal(day);
                        return String.format("Invalid date \"%s %s %d\", that date does not exist "
                                + "in the (Gregorian) calendar.", monthName, dayOrdinal, year);
                    } catch (NumberFormatException e) {
                        return MESSAGE_CONSTRAINTS_DATETIME;
                    }
                }
            }
            return MESSAGE_CONSTRAINTS_DATETIME;
        }
        if (!isFutureDateTime(parsed)) {
            return MESSAGE_CONSTRAINTS_PAST_DATETIME;
        }
        return null; // Valid
    }

    /**
     * Returns the month name for a given month number (1-12).
     */
    private static String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                           "July", "August", "September", "October", "November", "December"};
        if (month < 1 || month > 12) {
            return "Month " + month;
        }
        return months[month];
    }

    /**
     * Returns the ordinal form of a day (e.g., "1st", "2nd", "31st").
     */
    private static String getDayOrdinal(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
        case 1:
            return day + "st";
        case 2:
            return day + "nd";
        case 3:
            return day + "rd";
        default:
            return day + "th";
        }
    }

    /**
     * Returns true if the datetime is in the future.
     */
    public static boolean isFutureDateTime(LocalDateTime datetime) {
        return datetime.isAfter(LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public LocalDateTime getDateTime() {
        return datetime;
    }

    public String getDescription() {
        return description;
    }

    public String getDateTimeString() {
        return datetime.format(DATETIME_FORMATTER);
    }

    /**
     * Returns true if this booking conflicts with another booking (same datetime).
     */
    public boolean conflictsWith(Booking other) {
        return this.datetime.equals(other.datetime);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Booking)) {
            return false;
        }

        Booking otherBooking = (Booking) other;
        return id.equals(otherBooking.id)
                && clientName.equals(otherBooking.clientName)
                && datetime.equals(otherBooking.datetime)
                && description.equals(otherBooking.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientName, datetime, description);
    }

    @Override
    public String toString() {
        return String.format("[ID: %s] Client: %s at %s - %s",
                id, clientName, getDateTimeString(), description);
    }
}

