package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only

        // valid phone numbers - numeric only
        assertTrue(Phone.isValidPhone("911")); // exactly 3 numbers
        assertTrue(Phone.isValidPhone("91")); // 2 numbers
        assertTrue(Phone.isValidPhone("93121534")); // 8 numbers
        assertTrue(Phone.isValidPhone("124293842033123")); // long phone numbers

        // valid phone numbers - with special characters
        assertTrue(Phone.isValidPhone("+65 1234 5678")); // plus sign with spaces
        assertTrue(Phone.isValidPhone("+6512345678")); // plus sign without spaces
        assertTrue(Phone.isValidPhone("(123) 456-7890")); // parentheses and hyphens
        assertTrue(Phone.isValidPhone("123-456-7890")); // hyphens
        assertTrue(Phone.isValidPhone("123 456 7890")); // spaces
        assertTrue(Phone.isValidPhone("+1 (123) 456-7890")); // combination of special characters

        // valid phone numbers - with text
        assertTrue(Phone.isValidPhone("phone")); // text only
        assertTrue(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertTrue(Phone.isValidPhone("ext. 123")); // extension format
        assertTrue(Phone.isValidPhone("#1234")); // hash symbol
        assertTrue(Phone.isValidPhone("123 ext 456")); // extension with text
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("995")));
    }
}
