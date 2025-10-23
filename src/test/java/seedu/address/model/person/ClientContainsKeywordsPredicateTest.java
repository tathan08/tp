package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.booking.Booking;
import seedu.address.testutil.PersonBuilder;

public class ClientContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstKeywordList = Collections.singletonList("first");
        List<String> secondKeywordList = Arrays.asList("first", "second");

        ClientContainsKeywordsPredicate firstPredicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME, firstKeywordList);
        ClientContainsKeywordsPredicate secondPredicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME, secondKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        ClientContainsKeywordsPredicate firstPredicateCopy = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME, firstKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keyword list -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));

        // different search type -> returns false
        ClientContainsKeywordsPredicate tagPredicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.TAG, firstKeywordList);
        assertFalse(firstPredicate.equals(tagPredicate));
    }

    @Test
    public void constructor_nullType_throwsAssertionError() {
        List<String> keywords = List.of("Alice");
        AssertionError thrown = assertThrows(AssertionError.class, () ->
                new ClientContainsKeywordsPredicate(null, keywords));
        assertEquals("SearchType must not be null", thrown.getMessage());
    }

    // ===============================
    // NAME TESTS
    // ===============================

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // Single keyword: should match any name containing "Alice"
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME,
                                        Collections.singletonList("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Bob Alice").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Lim").build()));

        // Combined keywords: "Alice Bob" should match names containing that
        // full phrase
        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.NAME,
                                        Arrays.asList("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // "Alice Bob" should NOT match "Bob Alice" anymore, since we now join
        // keywords into one phrase
        assertFalse(predicate.test(new PersonBuilder().withName("Bob Alice").build()));

        // Combined keywords "Bob Carol" should match names containing that full
        // phrase, case-insensitive
        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.NAME,
                                        Arrays.asList("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Mr Bob Carol").build()));

        // Case-insensitivity check
        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.NAME,
                                        Arrays.asList("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Empty keyword → all persons should pass
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME, Collections.emptyList());
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword → false
        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.NAME,
                                        Arrays.asList("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Combined keywords that don't appear together → false
        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.NAME,
                                        Arrays.asList("Alice", "Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Partial match still counts → true
        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.NAME,
                                        Arrays.asList("carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Carolyn").build()));
    }

    @Test
    public void constructor_nullKeywords_throwsAssertionError() {
        AssertionError thrown = assertThrows(AssertionError.class, () -> new ClientContainsKeywordsPredicate(
                                                                        ClientContainsKeywordsPredicate.SearchType.NAME,
                                                                        null));
        assertEquals("Keywords list must not be null", thrown.getMessage());
    }

    @Test
    public void test_invalidSearchType_throwsException() {
        // Use reflection to bypass the enum and simulate an invalid state
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME, List.of("Alice")) {
            @Override
            public boolean test(Person person) {
                throw new IllegalStateException("Unexpected Value:" + "INVALID");
            }
        };

        assertThrows(IllegalStateException.class, () -> predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    public void test_assertions_validInputs() {
        // Valid case just to hit the asserts
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME, List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    // ===============================
    // TAG TESTS
    // ===============================

    @Test
    public void test_tagContainsKeywords_returnsTrue() {
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.TAG,
                                        Collections.singletonList("vip"));
        assertTrue(predicate.test(new PersonBuilder().withTags("vip", "friendly").build()));

        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.TAG,
                                        Arrays.asList("vip", "regular"));
        assertTrue(predicate.test(new PersonBuilder().withTags("regular", "active").build()));

        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.TAG,
                                        Arrays.asList("ViP"));
        assertTrue(predicate.test(new PersonBuilder().withTags("vip").build())); // case-insensitive
    }

    @Test
    public void test_tagDoesNotContainKeywords_returnsFalse() {
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.TAG,
                                        Collections.singletonList("vip"));
        assertFalse(predicate.test(new PersonBuilder().withTags("friend").build()));

        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.TAG,
                                        Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withTags("vip").build()));
    }

    // ===============================
    // DATE TESTS
    // ===============================

    @Test
    public void test_dateContainsKeywords_returnsTrue() {
        // Assuming booking date stored as "2025-10-15T10:00" in your Booking
        // class
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.DATE,
                                        Collections.singletonList("2025-10-15"));
        assertTrue(predicate.test(new PersonBuilder().withBookings(Arrays.asList(new Booking("100", "Test Client",
                                        LocalDateTime.of(2025, 10, 15, 10, 0), "Test booking"))).build()));

        // multiple possible dates
        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.DATE,
                                        Arrays.asList("2025-10-15", "2025-11-01"));
        assertTrue(predicate.test(new PersonBuilder().withBookings(Arrays.asList(new Booking("101", "Test Client",
                                        LocalDateTime.of(2025, 10, 15, 10, 0), "Test booking"))).build()));
    }

    @Test
    public void test_dateDoesNotContainKeywords_returnsFalse() {
        // Person with no bookings
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.DATE,
                                        Collections.singletonList("2025-10-15"));
        assertFalse(predicate.test(new PersonBuilder().build()));

        // Person with booking on different date
        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.DATE,
                                        Collections.singletonList("2025-10-15"));
        assertFalse(predicate.test(new PersonBuilder().withBookings(Arrays.asList(new Booking("102", "Test Client",
                                        LocalDateTime.of(2025, 11, 20, 10, 0), "Different date"))).build()));

        predicate = new ClientContainsKeywordsPredicate(ClientContainsKeywordsPredicate.SearchType.DATE,
                                        Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        ClientContainsKeywordsPredicate predicate = new ClientContainsKeywordsPredicate(
                                        ClientContainsKeywordsPredicate.SearchType.NAME, keywords);

        String expected = ClientContainsKeywordsPredicate.class.getCanonicalName() + "{searchType=NAME, keywords="
                                        + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
