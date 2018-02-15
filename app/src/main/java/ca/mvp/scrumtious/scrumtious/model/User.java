package ca.mvp.scrumtious.scrumtious.model;

import ca.mvp.scrumtious.scrumtious.utils.UserInputValidator;


public class User {

    private String userID;
    private String emailAddress;

    // empty constructor is needed for firebase
    public User() {

    }

    public User(String userID, String emailAddress) {

        if (userID == null || emailAddress == null) return;

        emailAddress = emailAddress.trim();

        if (UserInputValidator.isValidEmail(emailAddress) == 0) {
            this.userID = userID;
            this.emailAddress = emailAddress;
        } else return;
    }

    public String getEmailAddress() { return this.emailAddress; }
    public String getUserID() { return this.userID; }

    /**
     * Checks if the email is valid
     * @param newEmailAddressToSet String, the email to validate
     * @return -1 on null string, -2 on invalid string length, -3 for invalid email, 0 if email is set
     */
    public int setUserEmailAddress(String newEmailAddressToSet) {
        if (newEmailAddressToSet == null) return -1;
        int isValidStatus = UserInputValidator.isValidEmail(newEmailAddressToSet);

        if (isValidStatus == 0) {
            this.emailAddress = newEmailAddressToSet;
            return 0;
        }

        return isValidStatus;
    }
}
