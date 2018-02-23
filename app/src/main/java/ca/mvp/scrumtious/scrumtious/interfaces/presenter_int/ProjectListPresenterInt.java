package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.view_impl.ProjectListFragment;

public interface ProjectListPresenterInt {
    FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> setupGeneralProjectsAdapter
            (RecyclerView projectList, ProgressDialog loadingProjectsDialog);
    FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> setupMyProjectsAdapter
            (RecyclerView projectList, ProgressDialog loadingProjectsDialog);

}
