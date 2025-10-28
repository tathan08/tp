package seedu.address.model.person;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Tests that a {@code Person} matches all of the given search criteria. The
 * search criteria map contains field types (e.g. "name", "tag", "date") mapped
 * to lists of keywords.
 */
public class ClientContainsKeywordsPredicate implements Predicate<Person> {

    private final Map<String, List<String>> searchCriteria;

    public ClientContainsKeywordsPredicate(Map<String, List<String>> searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public boolean test(Person person) {
        // If searchCriteria is empty, match all persons
        if (searchCriteria.isEmpty()) {
            return true;
        }

        // Otherwise, check each field
        return searchCriteria.entrySet().stream().anyMatch(entry -> {
            String fieldType = entry.getKey();
            List<String> keywords = entry.getValue();

            // Empty keyword list = wildcard = match everyone
            if (keywords.isEmpty()) {
                return true;
            }

            return switch (fieldType) {
            case "name" -> matchesName(person, keywords);
            case "tag" -> matchesTag(person, keywords);
            case "date" -> matchesDate(person, keywords);
            default -> false;
            };
        });
    }

    private boolean matchesName(Person person, List<String> keywords) {
        String fullName = person.getName().fullName.toLowerCase();
        return keywords.stream().map(String::toLowerCase).anyMatch(fullName::contains);
    }

    private boolean matchesTag(Person person, List<String> keywords) {
        return person.getTags().stream().anyMatch(tag -> keywords.stream().map(String::toLowerCase)
                                        .anyMatch(tag.tagName.toLowerCase()::contains));
    }

    private boolean matchesDate(Person person, List<String> keywords) {
        return person.getBookings().stream().anyMatch(booking -> {
            String bookingDate = booking.getDateTime().toLocalDate().toString();
            return keywords.stream().anyMatch(bookingDate::contains);
        });
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ClientContainsKeywordsPredicate otherPredicate)) {
            return false;
        }

        return Objects.equals(this.searchCriteria, otherPredicate.searchCriteria);
    }

    // ✅ toString() override
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ClientContainsKeywordsPredicate with criteria: ");
        searchCriteria.forEach((key, value) ->
                sb.append(key).append("=").append(value).append("; "));
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchCriteria);
    }

    /**
     * Returns the search criteria used in this predicate.
     */
    public Map<String, List<String>> getSearchCriteria() {
        return searchCriteria;
    }
}
