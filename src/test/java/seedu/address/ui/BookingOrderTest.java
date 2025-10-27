package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import org.junit.jupiter.api.Test;

import seedu.address.model.booking.Booking;

/**
 * Test class for booking order functionality in PersonCard.
 */
public class BookingOrderTest {

    @Test
    public void bookingComparator_futureComesBeforePast() {
        // Create bookings
        LocalDateTime future = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime past = LocalDateTime.of(2023, 3, 4, 10, 0);

        Booking futureBooking = new Booking("1", "Client A", future, "Future");
        Booking pastBooking = new Booking("2", "Client B", past, "Past");

        // Use the same comparator logic from PersonCard
        Comparator<Booking> bookingComparator = (b1, b2) -> {
            boolean b1IsFuture = Booking.isFutureDateTime(b1.getDateTime());
            boolean b2IsFuture = Booking.isFutureDateTime(b2.getDateTime());

            if (b1IsFuture && b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else if (!b1IsFuture && !b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else {
                return b1IsFuture ? -1 : 1;
            }
        };

        int result = bookingComparator.compare(futureBooking, pastBooking);
        assertTrue(result < 0, "Future booking should come before past booking");

        result = bookingComparator.compare(pastBooking, futureBooking);
        assertTrue(result > 0, "Past booking should come after future booking");
    }

    @Test
    public void bookingComparator_futureBookingsSortedAscending() {
        // Create future bookings in wrong order
        LocalDateTime future1 = LocalDateTime.of(2026, 1, 20, 10, 0);
        LocalDateTime future2 = LocalDateTime.of(2026, 1, 15, 14, 0);

        Booking booking1 = new Booking("1", "Client A", future1, "Later");
        Booking booking2 = new Booking("2", "Client B", future2, "Earlier");

        Comparator<Booking> bookingComparator = (b1, b2) -> {
            boolean b1IsFuture = Booking.isFutureDateTime(b1.getDateTime());
            boolean b2IsFuture = Booking.isFutureDateTime(b2.getDateTime());

            if (b1IsFuture && b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else if (!b1IsFuture && !b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else {
                return b1IsFuture ? -1 : 1;
            }
        };

        int result = bookingComparator.compare(booking1, booking2);
        assertTrue(result > 0, "Later future booking should come after earlier future booking");
    }

    @Test
    public void bookingComparator_pastBookingsSortedAscending() {
        // Create past bookings in wrong order
        LocalDateTime past1 = LocalDateTime.of(2024, 4, 4, 10, 0);
        LocalDateTime past2 = LocalDateTime.of(2023, 3, 4, 14, 0);

        Booking booking1 = new Booking("1", "Client A", past1, "Later");
        Booking booking2 = new Booking("2", "Client B", past2, "Earlier");

        Comparator<Booking> bookingComparator = (b1, b2) -> {
            boolean b1IsFuture = Booking.isFutureDateTime(b1.getDateTime());
            boolean b2IsFuture = Booking.isFutureDateTime(b2.getDateTime());

            if (b1IsFuture && b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else if (!b1IsFuture && !b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else {
                return b1IsFuture ? -1 : 1;
            }
        };

        int result = bookingComparator.compare(booking1, booking2);
        assertTrue(result > 0, "Later past booking should come after earlier past booking");
    }

    @Test
    public void bookingComparator_mixedOrdering() {
        // Create mixed future and past bookings
        ArrayList<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking("1", "Client A", LocalDateTime.of(2023, 3, 4, 10, 0), "Past 1"));
        bookings.add(new Booking("2", "Client B", LocalDateTime.of(2026, 1, 20, 14, 0), "Future 2"));
        bookings.add(new Booking("3", "Client C", LocalDateTime.of(2026, 1, 15, 10, 0), "Future 1"));
        bookings.add(new Booking("4", "Client D", LocalDateTime.of(2024, 4, 4, 14, 0), "Past 2"));

        Comparator<Booking> bookingComparator = (b1, b2) -> {
            boolean b1IsFuture = Booking.isFutureDateTime(b1.getDateTime());
            boolean b2IsFuture = Booking.isFutureDateTime(b2.getDateTime());

            if (b1IsFuture && b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else if (!b1IsFuture && !b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else {
                return b1IsFuture ? -1 : 1;
            }
        };

        bookings.sort(bookingComparator);

        // Verify order: Future 1, Future 2, Past 1, Past 2
        assertTrue(Booking.isFutureDateTime(bookings.get(0).getDateTime()),
                "First booking should be future");
        assertTrue(Booking.isFutureDateTime(bookings.get(1).getDateTime()),
                "Second booking should be future");
        assertTrue(!Booking.isFutureDateTime(bookings.get(2).getDateTime()),
                "Third booking should be past");
        assertTrue(!Booking.isFutureDateTime(bookings.get(3).getDateTime()),
                "Fourth booking should be past");

        // Verify future bookings are in ascending order
        assertTrue(bookings.get(0).getDateTime().isBefore(bookings.get(1).getDateTime()),
                "Future bookings should be in ascending order");

        // Verify past bookings are in ascending order
        assertTrue(bookings.get(2).getDateTime().isBefore(bookings.get(3).getDateTime()),
                "Past bookings should be in ascending order");
    }

    @Test
    public void bookingComparator_futureBeforeNowGoesToFuture() {
        LocalDateTime nearFuture = LocalDateTime.of(2025, 12, 31, 23, 59);
        LocalDateTime past = LocalDateTime.of(2024, 1, 1, 10, 0);

        Booking futureBooking = new Booking("1", "Client A", nearFuture, "Near Future");
        Booking pastBooking = new Booking("2", "Client B", past, "Past");

        Comparator<Booking> bookingComparator = (b1, b2) -> {
            boolean b1IsFuture = Booking.isFutureDateTime(b1.getDateTime());
            boolean b2IsFuture = Booking.isFutureDateTime(b2.getDateTime());

            if (b1IsFuture && b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else if (!b1IsFuture && !b2IsFuture) {
                return b1.getDateTime().compareTo(b2.getDateTime());
            } else {
                return b1IsFuture ? -1 : 1;
            }
        };

        int result = bookingComparator.compare(futureBooking, pastBooking);
        assertTrue(result < 0, "Future booking should come before past booking");
    }
}

