package ca.mvp.scrumtious.scrumtious.model;

public class UserStory {

    private String userStoryName, userStoryPoints, userStoryDetails, completed, assignedToName;
    public UserStory(){

    }

    public String getUserStoryName() {
        return userStoryName;
    }

    public String getUserStoryPoints() {
        return userStoryPoints;
    }

    public String getUserStoryDetails(){ return userStoryDetails; }

    public String getCompleted(){ return completed; }

    public String getAssignedToName(){ return assignedToName; }
}
