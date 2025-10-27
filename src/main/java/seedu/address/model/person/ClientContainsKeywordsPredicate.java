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
        for (Map.Entry<String, List<String>> entry : searchCriteria.entrySet()) {
            String fieldType = entry.getKey();
            List<String> keywords = entry.getValue();

            // Empty keyword list = wildcard (matches everyone)
            if (keywords.isEmpty()) {
                continue;
            }

            boolean matches = switch (fieldType) {
            case "name" -> matchesName(person, keywords);
            case "tag" -> matchesTag(person, keywords);
            case "date" -> matchesDate(person, keywords);
            default -> false;
            };

            if (!matches) {
                return false; // if any filter fails, skip
            }
        }
        return true; // all criteria passed (or were wildcards)
    }

    private boolean matchesName(Person person, List<String> keywords) {
        // Each entry in keywords is a phrase from a single prefix occurrence
        // (e.g. "David Li").
        String fullName = person.getName().fullName.toLowerCase();

        // If multiple name phrases provided (n/A n/B), treat them as OR: match
        // if any phrase is contained.
        return keywords.stream().map(String::toLowerCase).anyMatch(phrase -> fullName.contains(phrase));
    }

    private boolean matchesTag(Person person, List<String> keywords) {
        // Tags: if any of the person's tags contain any of the provided tag
        // phrases -> match
        return person.getTags().stream().anyMatch(tag -> keywords.stream().map(String::toLowerCase)
                                        .anyMatch(phrase -> tag.tagName.toLowerCase().contains(phrase)));
    }

    private boolean matchesDate(Person person, List<String> keywords) {
        // Each person may have multiple bookings; match if any booking date
        // (LocalDate) contains any phrase.
        return person.getBookings().stream().anyMatch(booking -> {
            String bookingDate = booking.getDateTime().toLocalDate().toString();

            return keywords.stream().anyMatch(kw -> bookingDate.contains(kw));
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
}
