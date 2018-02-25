package ca.mvp.scrumtious.scrumtious.model;

public class UserStory {

    private String userStoryName, userStoryPoints, completed, assignedToName;
    public UserStory(){

    }

    public String getUserStoryName() {
        return userStoryName;
    }

    public String getUserStoryPoints() {
        return userStoryPoints;
    }

    public String getCompleted(){ return completed; }

    public String getAssignedToName(){ return assignedToName; }
}
