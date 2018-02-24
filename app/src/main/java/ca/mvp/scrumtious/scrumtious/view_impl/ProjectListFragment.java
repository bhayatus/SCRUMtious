package ca.mvp.scrumtious.scrumtious.view_impl;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectListPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectListViewInt;
import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectListPresenter;

public class ProjectListFragment extends Fragment implements ProjectListViewInt {

    private ProjectListPresenterInt projectListPresenterInt;

    private RecyclerView projectList;
    private ProgressDialog loadingProjectsDialog;
    private Switch showOnlyMyProjects;
    private Button addProjectBtn;
    private LinearLayout emptyStateView;

    private FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> allProjectsAdapter;
    private FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> myProjectsAdapter;


    public ProjectListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectListPresenterInt = new ProjectListPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_list, container, false);
        projectList = (RecyclerView) view.findViewById(R.id.projectListRecyclerView);
        showOnlyMyProjects = (Switch) view.findViewById(R.id.projectListSwitch);
        setupRecyclerView();
        addProjectBtn = (Button) view.findViewById(R.id.projectListAddProjectBtn);

        addProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddNewProject(view);
            }
        });

        emptyStateView = (LinearLayout) view.findViewById(R.id.projectListEmptyStateView);

        return view;

    }

    private void setupRecyclerView(){

        // Creates a dialog that appears to tell the user that the sign in is occurring
        loadingProjectsDialog = new ProgressDialog(getActivity());
        loadingProjectsDialog.setTitle("Load Projects");
        loadingProjectsDialog.setCancelable(false);
        loadingProjectsDialog.setMessage("Now loading your projects...");
        loadingProjectsDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingProjectsDialog.show();

        projectList.setLayoutManager(new LinearLayoutManager(getActivity()));

        myProjectsAdapter = projectListPresenterInt.setupMyProjectsAdapter(projectList, loadingProjectsDialog);
        allProjectsAdapter = projectListPresenterInt.setupGeneralProjectsAdapter(projectList, loadingProjectsDialog);

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

    // Switches between empty state screen and regular adapters based on item count being empty or not
    @Override
    public void setView(){
        if (allProjectsAdapter.getItemCount() == 0 && myProjectsAdapter.getItemCount() == 0){
            emptyStateView.setVisibility(View.VISIBLE);
            projectList.setVisibility(View.GONE);
        }
        else{
            emptyStateView.setVisibility(View.GONE);
            projectList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void goToProjectScreen(String pid) {
        Intent intent = new Intent(getActivity(), IndividualProjectActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
    }


    public void onClickAddNewProject(View view){
        Intent intent = new Intent(this.getActivity(), CreateProjectActivity.class);
        this.getActivity().startActivity(intent);
    }

    public static class ProjectsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView titleView, ownerEmailAddressView, descriptionView, creationDateView;

        public ProjectsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            titleView = (TextView) mView.findViewById(R.id.projectRowTitle);
            ownerEmailAddressView = (TextView) mView.findViewById(R.id.projectRowEmailAddress);
            descriptionView = (TextView) mView.findViewById(R.id.projectRowDescription);
            creationDateView = (TextView) mView.findViewById(R.id.projectRowCreatedDate);
        }


        // Populates each row of the recycler view with the project details
        public void setDetails(String title, String ownerEmailAddress, String description, String creationDate){
            titleView.setText(title);
            ownerEmailAddressView.setText("Owner: "+ ownerEmailAddress);
            descriptionView.setText(description);
            creationDateView.setText(creationDate);
        }


    }
}
