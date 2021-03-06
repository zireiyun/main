package seedu.address.model.task;

import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Represents remarks for a task in doitdoit!!. Guarantees: immutable; is valid
 * as declared in {@link #isValidRemarks(String)}
 */
public class Remarks {
    // @@author A0135795R
    public static final String MESSAGE_REMARKS_CONSTRAINTS = "Remarks cannot contain #.";
    public static final String REMARKS_VALIDATION_REGEX = "[^#]+";
    // @@author
    public final String value;

    /**
     * Validates given remarks.
     *
     * @throws IllegalValueException
     *             if given remarks string is invalid.
     */
    public Remarks(String remarks) throws IllegalValueException {
        assert remarks != null;
        String trimmedRemarks = remarks.trim();
        if (!isValidRemarks(trimmedRemarks)) {
            throw new IllegalValueException(MESSAGE_REMARKS_CONSTRAINTS);
        }
        this.value = trimmedRemarks;
    }

    /**
     * Returns if a given string is a valid task remarks.
     */
    public static boolean isValidRemarks(String test) {
        test = test.trim();
        return test.matches(REMARKS_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Remarks // instanceof handles nulls
                        && this.value.equals(((Remarks) other).value)); // state
                                                                        // check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
