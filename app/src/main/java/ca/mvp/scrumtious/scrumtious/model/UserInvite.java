package ca.mvp.scrumtious.scrumtious.model;

public class UserInvite {

    private String projectId, projectTitle, invitingUid, invitedUid, invitingEmail;

    public UserInvite(){

    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getInvitingEmail() {
        return invitingEmail;
    }
}
