package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class ClientContainsKeywordsPredicate implements Predicate<Person> {

    /**
     * The type of search to be performed.
     */
    public enum SearchType {
        NAME, TAG, DATE
    }

    private final List<String> keywords;
    private final SearchType type;

    /**
     * Constructs a ClientContainsKeywordsPredicate with the given search type
     * and keywords.
     */
    public ClientContainsKeywordsPredicate(SearchType type, List<String> keywords) {
        // Developer checks — these should never be null
        assert type != null : "SearchType must not be null";
        assert keywords != null : "Keywords list must not be null";

        this.keywords = keywords;
        this.type = type;
    }

    @Override
    public boolean test(Person person) {
        switch (type) {

        case NAME:
            // Filter out empty keywords
            List<String> validKeywords = keywords.stream().filter(kw -> !kw.trim().isEmpty()).toList();

            // If there are no valid keywords, return false (no match)
            if (validKeywords.isEmpty()) {
                return true;
            }
            String combinedKeyword = String.join(" ", keywords).trim();
            assert combinedKeyword != null : "Combined keyword string should not be null";
            return person.getName().fullName.toLowerCase().contains(combinedKeyword.toLowerCase());

        case TAG:
            // Ensure tag list is non-null
            assert person.getTags() != null : "Person should have a non-null tag list";

            return person.getTags().stream().map(tag -> tag.tagName.toLowerCase()).anyMatch(tagName -> keywords.stream()
                                            .filter(keyword -> !keyword.trim().isEmpty())
                                            .anyMatch(keyword -> tagName.equals(keyword.toLowerCase())));

        case DATE:
            // Ensure booking list is non-null
            assert person.getBookings() != null : "Person should have a non-null booking list";

            return keywords.stream().filter(keyword -> !keyword.trim().isEmpty()).anyMatch(keyword -> person
                                            .getBookings().stream()
                                            .map(booking -> booking.getDateTime().toLocalDate().toString())
                                            .anyMatch(date -> date.equals(keyword)));

        default:
            throw new IllegalStateException("Unexpected Value:" + type);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClientContainsKeywordsPredicate)) {
            return false;
        }

        ClientContainsKeywordsPredicate otherNameContainsKeywordsPredicate =
                                            (ClientContainsKeywordsPredicate) other;

        // Sanity check for equality invariants
        assert this.type != null : "SearchType should not be null when comparing predicates";
        assert this.keywords != null : "Keywords list should not be null when comparing predicates";

        return (this.type == otherNameContainsKeywordsPredicate.type)
                                        && (keywords.equals(otherNameContainsKeywordsPredicate.keywords));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("searchType", type)
                    .add("keywords", keywords).toString();
    }
}
