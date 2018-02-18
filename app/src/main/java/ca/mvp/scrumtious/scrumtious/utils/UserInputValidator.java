package ca.mvp.scrumtious.scrumtious.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInputValidator {

    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 254;

    // https://stackoverflow.com/questions/8204680/java-regex-email
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    // https://stackoverflow.com/questions/11780022/regex-in-java-for-password-validation
    private static final Pattern PASSWORD_CRITERIA_REGEX =
            Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}");

    /**
     * Checks if the email is valid
     * @param emailToValidate String, the email to validate
     * @return -1 on null string, -2 on invalid string length, -3 for invalid email, 0 for valid email
     */
    public static int isValidEmail(String emailToValidate) {
        if (emailToValidate == null) return -1;
        if (emailToValidate.length() > MAX_EMAIL_LENGTH || emailToValidate.length() <= 0) return -2;

        emailToValidate = emailToValidate.toLowerCase().trim();

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailToValidate);
        if (matcher.find()) return 0;

        return -3;
    }

    /**
     * Checks if the password meets the valid password criteria
     * @param passwordToCheck String, the password to check
     * @return -1 on null password, -2 on invalid password length, -3 for invalid password (including
     * length), 0 for valid password
     */
    public static int meetsPasswordCriteria(String passwordToCheck) {
        if (passwordToCheck == null) return -1;
        if (passwordToCheck.length() > MAX_PASSWORD_LENGTH || passwordToCheck.length() < MIN_PASSWORD_LENGTH) return -2;

        Matcher matcher = PASSWORD_CRITERIA_REGEX.matcher(passwordToCheck);
        if (matcher.find()) return 0;

        return -3;
    }

}
