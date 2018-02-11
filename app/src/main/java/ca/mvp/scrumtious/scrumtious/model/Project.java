package ca.mvp.scrumtious.scrumtious.model;

/**
 * Created by badsh on 2/11/2018.
 */

public class Project {

    private String projectTitle;
    private String projectOwnerEmail;
    private String projectDesc;

    private final int MAX_DESC_SIZE = 254;

    public Project(String projectTitle, String projectOwnerEmail, String projectDesc) {

        if (projectTitle == null || projectOwnerEmail == null || projectDesc == null) return;

        this.projectTitle = projectTitle;
        this.projectOwnerEmail = projectOwnerEmail;
        this.projectDesc = projectDesc;
    }

    public String getProjectTitle() { return this.projectTitle; }
    public String getProjectDesc() { return this.projectDesc; }
    public String getProjectOwnerEmail() { return this.projectOwnerEmail; }

    public int setProjectDesc(String newProjectDescToSet) {
        if (newProjectDescToSet == null) return -3;

        if (newProjectDescToSet.length() > MAX_DESC_SIZE || newProjectDescToSet.length() <= 0) return -2;
        else {
            this.projectDesc = newProjectDescToSet;
            return 0;
        }
    }
}
