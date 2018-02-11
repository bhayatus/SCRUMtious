package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.view_impl.ProjectListScreenFragment;

/**
 * Created by muham on 2018-02-11.
 */

public interface ProjectListScreenPresenterInt {
    void setupAuthenticationListener();
    FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> setupAdapter(Context appContext, RecyclerView projectList, ProgressDialog loadingProjectsDialog);
    void signOut();
}
