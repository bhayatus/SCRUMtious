package ca.mvp.scrumtious.scrumtious.model;

public class Project {

    private String projectOwnerUid;
    private String projectTitle;
    private String projectOwnerEmail;
    private String projectDesc;
    private long creationTimeStamp;
    private long numMembers;
    private long numSprints;

    // empty constructor is needed for firebase
    public Project() {

    }

    public String getProjectOwnerUid() { return this.projectOwnerUid; }
    public String getProjectTitle() { return this.projectTitle; }
    public String getProjectDesc() { return this.projectDesc; }
    public String getProjectOwnerEmail() { return this.projectOwnerEmail; }
    public long getCreationTimeStamp() {return creationTimeStamp;}
    public long getNumMembers(){ return numMembers;}
    public long getNumSprints(){ return numSprints;}


}
