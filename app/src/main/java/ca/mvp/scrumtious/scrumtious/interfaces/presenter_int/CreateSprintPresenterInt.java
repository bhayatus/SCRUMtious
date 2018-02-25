package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

public interface CreateSprintPresenterInt {
    void setupProjectDeletedListener();
    void addSprintToDatabase(String sprintName, String sprintDesc, long sprintStartDate, long sprintEndDate);
    void onCheckConflictingSprintDates(long startDate, long endDate);
}
