package seedu.address.model.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class BookingTest {

    @Test
    public void parseDateTime_validDate_success() {
        // Valid date should parse successfully
        LocalDateTime result = Booking.parseDateTime("2026-02-28 10:00");
        assertNotNull(result);
        assertEquals(2026, result.getYear());
        assertEquals(2, result.getMonthValue());
        assertEquals(28, result.getDayOfMonth());
        assertEquals(10, result.getHour());
        assertEquals(0, result.getMinute());
    }

    @Test
    public void parseDateTime_invalidDate_returnsNull() {
        // Invalid date like February 31st should return null
        LocalDateTime result = Booking.parseDateTime("2026-02-31 10:00");
        assertNull(result, "Invalid date 2026-02-31 should return null, not auto-correct to Feb 28");
    }

    @Test
    public void parseDateTime_invalidDateLeapYear_returnsNull() {
        // February 30th should return null even in leap years
        LocalDateTime result = Booking.parseDateTime("2024-02-30 10:00");
        assertNull(result, "Invalid date 2024-02-30 should return null");
    }

    @Test
    public void parseDateTime_invalidMonth_returnsNull() {
        // Month 13 should return null
        LocalDateTime result = Booking.parseDateTime("2026-13-01 10:00");
        assertNull(result, "Invalid month 13 should return null");
    }

    @Test
    public void parseDateTime_invalidDay_returnsNull() {
        // April 31st doesn't exist (April has 30 days)
        LocalDateTime result = Booking.parseDateTime("2026-04-31 10:00");
        assertNull(result, "Invalid date 2026-04-31 should return null");
    }

    @Test
    public void parseDateTime_invalidFormat_returnsNull() {
        // Wrong format should return null
        LocalDateTime result = Booking.parseDateTime("31-02-2026 10:00");
        assertNull(result, "Wrong date format should return null");
    }

    @Test
    public void parseDateTime_validLeapYearDate_success() {
        // February 29th in a leap year should work
        LocalDateTime result = Booking.parseDateTime("2024-02-29 10:00");
        assertNotNull(result);
        assertEquals(29, result.getDayOfMonth());
    }

    @Test
    public void parseDateTime_invalidLeapYearDate_returnsNull() {
        // February 29th in a non-leap year should return null
        LocalDateTime result = Booking.parseDateTime("2025-02-29 10:00");
        assertNull(result, "Feb 29 in non-leap year 2025 should return null");
    }

    @Test
    public void isFutureDateTime_futureDate_returnsTrue() {
        // A date far in the future
        LocalDateTime futureDate = LocalDateTime.of(2099, 12, 31, 23, 59);
        assertTrue(Booking.isFutureDateTime(futureDate));
    }

    @Test
    public void isFutureDateTime_pastDate_returnsFalse() {
        // A date in the past
        LocalDateTime pastDate = LocalDateTime.of(2020, 1, 1, 10, 0);
        assertFalse(Booking.isFutureDateTime(pastDate), "Past date should return false");
    }

    @Test
    public void parseDateTime_pastDateFormat_successfullyParsed() {
        // Past dates should parse successfully (validation happens separately)
        LocalDateTime result = Booking.parseDateTime("2020-01-01 10:00");
        assertNotNull(result, "Past date string should parse successfully");
        assertEquals(2020, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(1, result.getDayOfMonth());
    }

    @Test
    public void validateDateTime_invalidDate_returnsSpecificErrorMessage() {
        // Test that invalid dates return specific error messages
        String error = Booking.validateDateTime("2026-02-31 10:00");
        assertNotNull(error, "Invalid date should return error message");
        assertTrue(error.contains("February 31st 2026"),
                "Error message should contain 'February 31st 2026'");
        assertTrue(error.contains("does not exist"),
                "Error message should mention date does not exist");
    }

    @Test
    public void validateDateTime_pastDate_returnsPastDateError() {
        // Test that past dates return past date error
        String error = Booking.validateDateTime("2020-01-01 10:00");
        assertNotNull(error, "Past date should return error message");
        assertEquals(Booking.MESSAGE_CONSTRAINTS_PAST_DATETIME, error);
    }

    @Test
    public void validateDateTime_validFutureDate_returnsNull() {
        // Test that valid future dates return null (no error)
        String error = Booking.validateDateTime("2099-12-31 23:59");
        assertNull(error, "Valid future date should return null");
    }

    @Test
    public void validateDateTime_invalidFormat_returnsFormatError() {
        // Test that invalid format returns format error
        String error = Booking.validateDateTime("31-02-2026 10:00");
        assertNotNull(error, "Invalid format should return error message");
        assertEquals(Booking.MESSAGE_CONSTRAINTS_DATETIME, error);
    }
}

