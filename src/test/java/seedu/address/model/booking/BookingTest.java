package seedu.address.model.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    @Test
    public void isValidClientName() {
        // invalid client names
        assertFalse(Booking.isValidClientName("")); // empty string
        assertFalse(Booking.isValidClientName(" ")); // spaces only
        assertFalse(Booking.isValidClientName("!")); // only invalid characters
        assertFalse(Booking.isValidClientName("John@Doe")); // contains invalid character
        assertFalse(Booking.isValidClientName("".repeat(101))); // too long (101 characters)

        // valid client names
        assertTrue(Booking.isValidClientName("John Doe")); // alphabets only
        assertTrue(Booking.isValidClientName("John O'Brien")); // with apostrophe
        assertTrue(Booking.isValidClientName("Mary-Jane")); // with hyphen
        assertTrue(Booking.isValidClientName("Dr. Smith")); // with period
        assertTrue(Booking.isValidClientName("Abhijay s/o Abhi")); // with forward slash
        assertTrue(Booking.isValidClientName("John\\Doe")); // with backslash
        assertTrue(Booking.isValidClientName("Jean-Paul O'Connor/Smith")); // with all special chars
        assertTrue(Booking.isValidClientName("a".repeat(100))); // exactly 100 chars (max length)
    }

    @Test
    public void constructor_clientNameWithSlashes_success() {
        // Test that booking can be created with slashes in client name
        String clientName = "Raj s/o Kumar";
        LocalDateTime datetime = LocalDateTime.of(2026, 12, 25, 10, 0);
        String description = "Consultation";

        Booking booking = new Booking(clientName, datetime, description);
        assertEquals(clientName, booking.getClientName());
    }

    @Test
    public void isValidDescription() {
        // invalid descriptions
        assertFalse(Booking.isValidDescription(null)); // null
        assertFalse(Booking.isValidDescription("")); // empty string
        assertFalse(Booking.isValidDescription("   ")); // only spaces
        assertFalse(Booking.isValidDescription("a".repeat(501))); // too long (501 characters)

        // valid descriptions
        assertTrue(Booking.isValidDescription("Consultation")); // normal description
        assertTrue(Booking.isValidDescription("a")); // minimum length (1 char)
        assertTrue(Booking.isValidDescription("a".repeat(500))); // maximum length (500 chars)
        assertTrue(Booking.isValidDescription("Follow-up appointment with client")); // long description
    }

    @Test
    public void validateDateTime_edgeCase_invalidMonthName() {
        // Test that invalid month (e.g., month > 12) returns proper error message
        String error = Booking.validateDateTime("2026-13-01 10:00");
        assertNotNull(error, "Invalid month should return error message");
        assertTrue(error.contains("does not exist"), "Should contain 'does not exist'");
    }

    @Test
    public void validateDateTime_edgeCase_invalidDateExtraction() {
        // Test date that matches format but cannot be parsed for day extraction
        String error = Booking.validateDateTime("2026-02-31 10:00");
        assertNotNull(error, "Invalid date should return error message");
        assertTrue(error.contains("does not exist"), "Should contain 'does not exist'");
    }

    @Test
    public void gettersTest() {
        String clientName = "John Doe";
        LocalDateTime datetime = LocalDateTime.of(2026, 12, 25, 10, 0);
        String description = "Consultation";

        Booking booking = new Booking("999", clientName, datetime, description);

        assertEquals("999", booking.getId());
        assertEquals(clientName, booking.getClientName());
        assertEquals(datetime, booking.getDateTime());
        assertEquals(description, booking.getDescription());
        assertEquals("2026-12-25 10:00", booking.getDateTimeString());
    }

    @Test
    public void conflictsWithTest() {
        LocalDateTime datetime1 = LocalDateTime.of(2026, 12, 25, 10, 0);
        LocalDateTime datetime2 = LocalDateTime.of(2026, 12, 25, 14, 0);

        Booking booking1 = new Booking("Client1", datetime1, "Description1");
        Booking booking2 = new Booking("Client2", datetime1, "Description2");
        Booking booking3 = new Booking("Client3", datetime2, "Description3");

        assertTrue(booking1.conflictsWith(booking2), "Should conflict at same time");
        assertTrue(booking2.conflictsWith(booking1), "Should conflict at same time");
        assertFalse(booking1.conflictsWith(booking3), "Should not conflict at different times");
        assertFalse(booking3.conflictsWith(booking1), "Should not conflict at different times");
    }

    @Test
    public void equalsTest() {
        LocalDateTime datetime1 = LocalDateTime.of(2026, 12, 25, 10, 0);
        LocalDateTime datetime2 = LocalDateTime.of(2026, 12, 25, 14, 0);

        Booking booking1 = new Booking("1", "Client1", datetime1, "Description1");
        Booking booking2 = new Booking("1", "Client1", datetime1, "Description1");
        Booking booking3 = new Booking("2", "Client1", datetime1, "Description1");
        Booking booking4 = new Booking("1", "Client2", datetime1, "Description1");

        assertEquals(booking1, booking2); // same values
        assertTrue(booking1.equals(booking1)); // same object
        assertFalse(booking1.equals(null)); // null
        assertFalse(booking1.equals("not a booking")); // different type
        assertFalse(booking1.equals(booking3)); // different ID
        assertFalse(booking1.equals(booking4)); // different client name
    }

    @Test
    public void hashCodeTest() {
        LocalDateTime datetime = LocalDateTime.of(2026, 12, 25, 10, 0);
        Booking booking1 = new Booking("1", "Client1", datetime, "Description1");
        Booking booking2 = new Booking("1", "Client1", datetime, "Description1");
        Booking booking3 = new Booking("2", "Client1", datetime, "Description1");

        assertEquals(booking1.hashCode(), booking2.hashCode()); // same values should have same hash
        assertNotEquals(booking1.hashCode(), booking3.hashCode()); // different ID should have different hash
    }

    @Test
    public void toStringTest() {
        Booking booking = new Booking("5", "Raj s/o Kumar", 
            LocalDateTime.of(2026, 12, 25, 10, 0), "Follow-up consultation");
        
        String result = booking.toString();
        assertTrue(result.contains("5"));
        assertTrue(result.contains("Raj s/o Kumar"));
        assertTrue(result.contains("2026-12-25 10:00"));
        assertTrue(result.contains("Follow-up consultation"));
    }
}

