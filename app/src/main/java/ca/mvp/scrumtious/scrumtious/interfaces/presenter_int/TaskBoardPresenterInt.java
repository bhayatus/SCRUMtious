package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.view_impl.TaskBoardFragment;

public interface TaskBoardPresenterInt {

    FirebaseRecyclerAdapter<ca.mvp.scrumtious.scrumtious.model.Task, TaskBoardFragment.TaskBoardViewHolder> setupTaskBoardAdapter(String type);
    void changeStatus(String tid, String newStatus);
    void deleteTask(String tid);
}
