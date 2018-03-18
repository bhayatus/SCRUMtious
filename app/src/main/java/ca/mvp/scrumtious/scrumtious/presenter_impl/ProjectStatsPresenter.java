package ca.mvp.scrumtious.scrumtious.presenter_impl;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectStatsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectStatsViewInt;

public class ProjectStatsPresenter implements ProjectStatsPresenterInt{

    private ProjectStatsViewInt projectStatsView;
    private String pid;

    public ProjectStatsPresenter(ProjectStatsViewInt projectStatsView, String pid){
        this.projectStatsView = projectStatsView;
        this.pid = pid;
    }

}
