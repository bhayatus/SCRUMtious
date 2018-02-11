package ca.mvp.scrumtious.scrumtious.model;

import ca.mvp.scrumtious.scrumtious.utils.UserInputValidator;

/**
 * Created by badsh on 2/10/2018.
 */

public class User {

    private String userId;
    private String userEmailAddress;

    public User(String userId, String userEmailAddress) {

        if (userId == null || userEmailAddress == null) return;

        userEmailAddress = userEmailAddress.trim();

        if (UserInputValidator.isValidEmail(userEmailAddress) == 0) {
            this.userId = userId;
            this.userEmailAddress = userEmailAddress;
        } else return;
    }

    public String getUserEmailAddress() { return this.userEmailAddress; }
    public String getUserId() { return this.userId; }

    /**
     * Checks if the email is valid
     * @param newEmailAddressToSet String, the email to validate
     * @return -1 on null string, -2 on invalid string length, -3 for invalid email, 0 if email is set
     */
    public int setUserEmailAddress(String newEmailAddressToSet) {
        if (newEmailAddressToSet == null) return -1;
        int isValidStatus = UserInputValidator.isValidEmail(newEmailAddressToSet);

        if (isValidStatus == 0) {
            this.userEmailAddress = newEmailAddressToSet;
            return 0;
        }

        return isValidStatus;
    }
}
