package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import com.google.firebase.database.ValueEventListener;

public interface ProjectOverviewPresenterInt {
    ValueEventListener getProjectDetailsListener();
    void removeProjectDetailsListener(ValueEventListener projectDetailsListener);
}
