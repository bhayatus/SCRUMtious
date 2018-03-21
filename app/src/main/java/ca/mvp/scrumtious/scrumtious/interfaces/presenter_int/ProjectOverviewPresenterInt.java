package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

public interface ProjectOverviewPresenterInt {
    void setupProjectDetailsListener();
    void removeProjectDetailsListener();

    void setupCurrentSprintListener();
    void removeCurrentSprintListener();

    void getUserStoryProgress();
}
