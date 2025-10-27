package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.model.booking.Booking;
import seedu.address.testutil.PersonBuilder;

public class ClientContainsKeywordsPredicateTest {

    // Helper to build predicate from field→keywords map
    private ClientContainsKeywordsPredicate buildPredicate(Map<String, List<String>> criteria) {
        return new ClientContainsKeywordsPredicate(criteria);
    }

    @Test
    public void equals_sameAndDifferentPredicate() {
        Map<String, List<String>> map1 = new HashMap<>();
        map1.put("name", List.of("Alice"));
        Map<String, List<String>> map2 = new HashMap<>();
        map2.put("name", List.of("Bob"));

        ClientContainsKeywordsPredicate pred1 = new ClientContainsKeywordsPredicate(map1);
        ClientContainsKeywordsPredicate pred2 = new ClientContainsKeywordsPredicate(map2);

        // same object
        assertTrue(pred1.equals(pred1));

        // same content → use the same map instance to avoid subtle differences
        assertTrue(pred1.equals(new ClientContainsKeywordsPredicate(map1)));

        // different content
        assertFalse(pred1.equals(pred2));

        // null and different type
        assertFalse(pred1.equals(null));
        assertFalse(pred1.equals("string"));
    }

    @Test
    public void test_emptyMap_matchesAll() {
        ClientContainsKeywordsPredicate predicate = buildPredicate(Collections.emptyMap());
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Bob").build()));
    }

    @Test
    public void test_nameMatching() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("name", List.of("Alice"));
        ClientContainsKeywordsPredicate predicate = buildPredicate(map);

        // matches
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Bob Alice").build()));

        // does not match
        assertFalse(predicate.test(new PersonBuilder().withName("Charlie").build()));
    }

    @Test
    public void test_tagMatching() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("tag", List.of("vip"));
        ClientContainsKeywordsPredicate predicate = buildPredicate(map);

        assertTrue(predicate.test(new PersonBuilder().withTags("vip", "friend").build()));
        assertFalse(predicate.test(new PersonBuilder().withTags("friend").build()));
    }

    @Test
    public void test_dateMatching() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("date", List.of("2025-10-15"));
        ClientContainsKeywordsPredicate predicate = buildPredicate(map);

        // Person with matching booking
        PersonBuilder personWithBooking = new PersonBuilder().withBookings(Arrays.asList(new Booking("100",
                                        "Test Client", LocalDateTime.of(2025, 10, 15, 10, 0), "desc")));
        assertTrue(predicate.test(personWithBooking.build()));

        // Person with different booking date
        PersonBuilder personOtherDate = new PersonBuilder().withBookings(Arrays.asList(new Booking("101", "Test Client",
                                        LocalDateTime.of(2025, 11, 20, 10, 0), "desc")));
        assertFalse(predicate.test(personOtherDate.build()));

        // Person with no bookings
        assertFalse(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void test_orAcrossFields() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("name", List.of("Alice"));
        map.put("tag", List.of("friend"));
        ClientContainsKeywordsPredicate predicate = buildPredicate(map);

        // Should match if name matches
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withTags("vip").build()));

        // Should match if tag matches
        assertTrue(predicate.test(new PersonBuilder().withName("Bob").withTags("friend").build()));

        // Should match if both match
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withTags("friend").build()));

        // Should not match if neither match
        assertFalse(predicate.test(new PersonBuilder().withName("Charlie").withTags("vip").build()));
    }

    @Test
    public void test_emptyKeywordListWildcard() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("name", List.of()); // wildcard
        map.put("tag", List.of()); // wildcard
        ClientContainsKeywordsPredicate predicate = buildPredicate(map);

        // Everything should match
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withTags("vip").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Bob").withTags("friend").build()));
    }

    @Test
    public void test_toStringContainsAllCriteria() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("name", List.of("Alice"));
        map.put("tag", List.of("friend"));
        ClientContainsKeywordsPredicate predicate = buildPredicate(map);

        String str = predicate.toString();
        assertTrue(str.contains("name"));
        assertTrue(str.contains("Alice"));
        assertTrue(str.contains("tag"));
        assertTrue(str.contains("friend"));
    }
}
