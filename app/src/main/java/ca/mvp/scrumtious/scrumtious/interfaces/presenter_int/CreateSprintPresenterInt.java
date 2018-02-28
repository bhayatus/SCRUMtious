package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

public interface CreateSprintPresenterInt {
    void checkConflictingSprintDates(String sprintName, String sprintDesc, long sprintStartDate, long sprintEndDate);
}
