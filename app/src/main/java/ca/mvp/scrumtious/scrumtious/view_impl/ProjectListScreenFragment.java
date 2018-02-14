package ca.mvp.scrumtious.scrumtious.view_impl;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectListScreenPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectListScreenViewInt;
import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectListScreenPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectListScreenFragment extends Fragment implements ProjectListScreenViewInt {

    private ProjectListScreenPresenterInt projectListScreenPresenterInt;

    private RecyclerView projectList;
    private ProgressDialog loadingProjectsDialog;
    private Switch showOnlyMyProjects;
    private FloatingActionButton fab;

    public ProjectListScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectListScreenPresenterInt = new ProjectListScreenPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_list_screen, container, false);
        projectList = (RecyclerView) view.findViewById(R.id.projectListScreenRecyclerView);
        showOnlyMyProjects = (Switch) view.findViewById(R.id.projectListScreenSwitch);

        fab = (FloatingActionButton) view.findViewById(R.id.projectListScreenFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), CreateProjectScreenActivity.class);
//                getActivity().startActivity(intent);
                onClickAddNewProject(view);
            }
        });

        setupRecyclerView();
        return view;

    }

    private void setupRecyclerView(){

        projectListScreenPresenterInt.setupAuthenticationListener();
        // Creates a dialog that appears to tell the user that the sign in is occurring
        loadingProjectsDialog = new ProgressDialog(getActivity());
        loadingProjectsDialog.setTitle("Loading Projects");
        loadingProjectsDialog.setCancelable(false);
        loadingProjectsDialog.setMessage("Now loading your projects...");
        loadingProjectsDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingProjectsDialog.show();

        projectList.setLayoutManager(new LinearLayoutManager(getActivity()));
        final FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> myProjectsAdapter;
        final FirebaseRecyclerAdapter<Project, ProjectListScreenFragment.ProjectsViewHolder> allProjectsAdapter;

        myProjectsAdapter = projectListScreenPresenterInt.setupMyProjectsAdapter(projectList, loadingProjectsDialog);
        allProjectsAdapter = projectListScreenPresenterInt.setupGeneralProjectsAdapter(projectList, loadingProjectsDialog);
        projectList.setAdapter(allProjectsAdapter);
        showOnlyMyProjects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // User only wants to see the projects that they own
                if (isChecked){
                    projectList.setAdapter(myProjectsAdapter);
                }
                else{
                    projectList.setAdapter(allProjectsAdapter);
                }
            }
        });
    }

    @Override
    public void goToProjectScreen(String pid) {
        Intent intent = new Intent(getActivity(), IndividualProjectScreenActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
        getActivity().finish();
    }
 
    public void returnToLoginScreen(){
        Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void onClickAddNewProject(View view){
        Intent intent = new Intent(this.getActivity(), CreateProjectScreenActivity.class);
        this.getActivity().startActivity(intent);
    }

    public static class ProjectsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView titleView, ownerEmailAddressView, descriptionView;

        public ProjectsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            titleView = (TextView) mView.findViewById(R.id.projectRowTitle);
            ownerEmailAddressView = (TextView) mView.findViewById(R.id.projectRowEmailAddress);
            descriptionView = (TextView) mView.findViewById(R.id.projectRowDescription);
        }


        // Populates each row of the recycler view with the project details
        public void setDetails(String title, String ownerEmailAddress, String description){
            titleView.setText(title);
            ownerEmailAddressView.setText("Owner: "+ ownerEmailAddress);
            descriptionView.setText(description);
        }
    }
}
