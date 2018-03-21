package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

public interface ProjectOverviewPresenterInt {
    void setupProjectDetailsListener();
    void removeProjectDetailsListener();

    void setupCurrentSprintListener();
    void removeCurrentSprintListener();

    void setupCurrentVelocityListener();
    void removeCurrentVelocityListener();

    void setupDaysListener();
    void removeDaysListener();

    void getUserStoryProgress();

    void changeCurrentVelocity(long newVelocity);

}
