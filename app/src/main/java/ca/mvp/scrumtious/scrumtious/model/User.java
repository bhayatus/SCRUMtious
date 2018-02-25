package ca.mvp.scrumtious.scrumtious.model;

public class User {

    private String userID;
    private String emailAddress;

    // empty constructor is needed for firebase
    public User() {

    }

    public String getEmailAddress() { return this.emailAddress; }
    public String getUserID() { return this.userID; }

}
