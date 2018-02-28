package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import android.support.v7.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import ca.mvp.scrumtious.scrumtious.model.UserStory;
import ca.mvp.scrumtious.scrumtious.view_impl.BacklogFragment;

public interface BacklogPresenterInt {
    FirebaseRecyclerAdapter<UserStory, BacklogFragment.BacklogViewHolder> setupBacklogAdapter();

    void changeCompletedStatus(String usid, boolean newStatus);
    void deleteUserStory(String usid);
    void validatePassword(String password, String usid);
}
