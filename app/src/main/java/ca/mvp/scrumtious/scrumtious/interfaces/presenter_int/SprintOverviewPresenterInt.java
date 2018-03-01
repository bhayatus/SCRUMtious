package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import com.google.firebase.database.ValueEventListener;

public interface SprintOverviewPresenterInt {
    ValueEventListener getSprintDetailsListener();
    void removeSprintDetailsListener(ValueEventListener sprintDetailsListener);
}
