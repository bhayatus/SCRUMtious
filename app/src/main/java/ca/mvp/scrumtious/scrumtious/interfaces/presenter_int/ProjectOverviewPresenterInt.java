package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import ca.mvp.scrumtious.scrumtious.model.UserStory;
import ca.mvp.scrumtious.scrumtious.view_impl.BacklogFragment;

public interface ProjectOverviewPresenterInt {
    void setupProjectDetailsListener();
    void removeProjectDetailsListener();

    void setupCurrentSprintListener();
    void removeCurrentSprintListener();

    void getUserStoryProgress();
}
