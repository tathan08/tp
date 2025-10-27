package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void constructor_nameTooLong_throwsIllegalArgumentException() {
        String longName = "a".repeat(51); // 51 characters, exceeds max of 50
        assertThrows(IllegalArgumentException.class, () -> new Name(longName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphabetic characters
        assertFalse(Name.isValidName("peter*")); // contains invalid characters
        assertFalse(Name.isValidName("12345")); // numbers only
        assertFalse(Name.isValidName("peter the 2nd")); // contains numbers
        assertFalse(Name.isValidName("David123")); // contains numbers

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr")); // long names
        assertTrue(Name.isValidName("O'Brien")); // with apostrophe
        assertTrue(Name.isValidName("Mary-Jane")); // with hyphen
        assertTrue(Name.isValidName("Jean-Paul O'Connor")); // with both apostrophe and hyphen
        assertTrue(Name.isValidName("John/Doe")); // with forward slash
        assertTrue(Name.isValidName("John\\Doe")); // with backslash
        assertTrue(Name.isValidName("Jean-Paul O'Connor/Mary-Jane")); // with all special chars
        assertTrue(Name.isValidName("a".repeat(50))); // exactly 50 chars (max length)
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
