package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import ca.mvp.scrumtious.scrumtious.model.Sprint;
import ca.mvp.scrumtious.scrumtious.view_impl.SprintListActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public interface SprintListPresenterInt {

    FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder> setupSprintListAdapter
            (String sortBy);
}
