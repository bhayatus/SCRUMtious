package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import ca.mvp.scrumtious.scrumtious.model.Sprint;
import ca.mvp.scrumtious.scrumtious.view_impl.SprintListActivity;
import android.support.v7.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public interface SprintListPresenterInt {

    FirebaseRecyclerAdapter<Sprint, SprintListActivity.SprintsViewHolder> setupSprintListAdapter
            (RecyclerView sprintList, String sortBy);
}
