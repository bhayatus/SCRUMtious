package ca.mvp.scrumtious.scrumtious.model;

import java.util.Map;

public class Project {

    private String projectOwnerUid;
    private String projectTitle;
    private String projectOwnerEmail;
    private String projectDesc;
    private long creationTimeStamp;

    private final int MAX_DESC_SIZE = 254;

    // empty constructor is needed for firebase
    public Project() {

    }

    public Project(String projectOwnerUid, String projectTitle, String projectOwnerEmail, String projectDesc) {

        if (projectOwnerUid == null || projectTitle == null || projectOwnerEmail == null || projectDesc == null) return;

        this.projectOwnerUid = projectOwnerUid;
        this.projectTitle = projectTitle;
        this.projectOwnerEmail = projectOwnerEmail;
        this.projectDesc = projectDesc;
    }

    public String getProjectOwnerUid() { return this.projectOwnerUid; }
    public String getProjectTitle() { return this.projectTitle; }
    public String getProjectDesc() { return this.projectDesc; }
    public String getProjectOwnerEmail() { return this.projectOwnerEmail; }
    public long getCreationTimeStamp() {return creationTimeStamp;}

    public int setProjectDesc(String newProjectDescToSet) {
        if (newProjectDescToSet == null) return -3;

        if (newProjectDescToSet.length() > MAX_DESC_SIZE || newProjectDescToSet.length() <= 0) return -2;
        else {
            this.projectDesc = newProjectDescToSet;
            return 0;
        }
    }
}
