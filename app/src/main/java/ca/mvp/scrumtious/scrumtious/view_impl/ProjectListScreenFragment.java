package ca.mvp.scrumtious.scrumtious.view_impl;


import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.TextView;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectListScreenViewInt;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectListScreenFragment extends Fragment implements ProjectListScreenViewInt {

    private ProjectListScreenPresenterInt projectListScreenPresenterInt;
    private RecyclerView projectList;
    private ProgressDialog loadingProjectsDialog;

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
        View v = inflater.inflate(R.layout.fragment_project_list_screen, container, false);
        projectList = (RecyclerView) v.findViewById(R.id.projectListScreenRecyclerView);
        setupRecyclerView();
        return inflater.inflate(R.layout.fragment_project_list_screen, container, false);

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
        projectList.setAdapter(projectListScreenPresenterInt.setupAdapter(getActivity().getApplicationContext(), projectList, loadingProjectsDialog));

    }

    @Override
    public void goToProjectScreen(String pid) {
    }

    public void returnToLoginScreen(){
        Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
        startActivity(intent);
        getActivity().finish();

    }

    public static class ProjectsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView titleView, ownerEmailAddressView, descriptionView;
        Button projectRowButton;

        public ProjectsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            titleView = (TextView) mView.findViewById(R.id.projectRowTitle);
            ownerEmailAddressView = (TextView) mView.findViewById(R.id.projectRowEmailAddress);
            descriptionView = (TextView) mView.findViewById(R.id.projectRowDescription);
        }


        // Populates each row of the recycler view with the project details
        public void setDetails(Context context, String title, String ownerEmailAddress, String description){
            titleView.setText(title);
            ownerEmailAddressView.setText(ownerEmailAddress);
            descriptionView.setText(description);
        }
    }

    public void onClickAddNewProject(View view){
      //  Intent intent = new Intent(getActivity(), CreateProjectScreenActivity.class);
        // startActivity(intent);
    }
}
