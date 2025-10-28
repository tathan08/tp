package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.commons.ErrorMessage;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.ClientContainsKeywordsPredicate;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in address book whose name contains any of the
 * argument keywords. Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final ErrorMessage MESSAGE_USAGE = new ErrorMessage(
            "Finds all persons whose names partially contain any of the specified keywords "
                    + "(case-insensitive) and displays them as a list with index numbers.",
            "find [n/NAME] [t/TAG] [d/DATE]",
            COMMAND_WORD + " n/Alex"
    );

    private final Predicate<Person> predicate;

    /**
     * Creates a FindCommand to be executed with the specified
     * {@code Predicate}.
     */
    public FindCommand(Predicate<Person> predicate) {
        // Defensive check — the predicate should never be null
        assert predicate != null : "Predicate passed to FindCommand must not be null";

        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        assert model.getFilteredPersonList() != null
                    : "Model's filtered person list should not be null before updating";

        model.updateFilteredPersonList(predicate);
        int resultCount = model.getFilteredPersonList().size();
        // Ensure the count is non-negative
        assert resultCount >= 0 : "Result count of filtered list should never be negative";

        String searchParamsMessage = formatSearchParameters();
        String resultMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, resultCount);
        
        return new CommandResult(searchParamsMessage + "\n" + resultMessage);
    }

    /**
     * Formats the search parameters into a user-friendly string.
     */
    private String formatSearchParameters() {
        if (!(predicate instanceof ClientContainsKeywordsPredicate)) {
            return "Searching with custom criteria";
        }

        ClientContainsKeywordsPredicate clientPredicate = (ClientContainsKeywordsPredicate) predicate;
        Map<String, List<String>> searchCriteria = clientPredicate.getSearchCriteria();

        if (searchCriteria.isEmpty()) {
            return "Searching for: all contacts";
        }

        StringBuilder sb = new StringBuilder("Searching for contacts with:");
        
        searchCriteria.forEach((fieldType, keywords) -> {
            sb.append("\n  ");
            switch (fieldType) {
            case "name":
                sb.append("Name containing: ");
                break;
            case "tag":
                sb.append("Tag containing: ");
                break;
            case "date":
                sb.append("Booking date: ");
                break;
            default:
                sb.append(fieldType).append(": ");
            }
            
            if (keywords.isEmpty()) {
                sb.append("any");
            } else {
                sb.append(keywords.stream().collect(Collectors.joining(", ")));
            }
        });

        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        // Sanity check
        assert this.predicate != null : "Predicate should not be null when comparing commands";

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
