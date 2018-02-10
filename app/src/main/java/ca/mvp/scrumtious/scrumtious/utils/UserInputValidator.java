package ca.mvp.scrumtious.scrumtious.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by badsh on 2/10/2018.
 */

public class UserInputValidator {

    // https://stackoverflow.com/questions/8204680/java-regex-email
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final int MAX_EMAIL_LENGTH = 254;


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
}
