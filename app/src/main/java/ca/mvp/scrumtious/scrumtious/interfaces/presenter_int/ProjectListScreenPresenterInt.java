package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.view_impl.ProjectListScreenFragment;

public interface ProjectListScreenPresenterInt {
    FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> setupGeneralProjectsAdapter
            (RecyclerView projectList, ProgressDialog loadingProjectsDialog);
    FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> setupMyProjectsAdapter
            (RecyclerView projectList, ProgressDialog loadingProjectsDialog);

}
