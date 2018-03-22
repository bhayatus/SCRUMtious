package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface ProjectOverviewViewInt {
    void setProjectDetails(String titleViewText, String descriptionViewText);
    void setCurrentSprintDetails(String currentSprintId, String sprintTitle, String sprintDesc, String dates);
    void setCurrentProgressCircle(long total, long completed);
    void setCurrentVelocity(long currentVelocity);
    void setDays(long days);
    void showMessage(String message, boolean showAsToast);
}
