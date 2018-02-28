package ca.mvp.scrumtious.scrumtious.view_impl;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectOverviewPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectOverviewViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectOverviewPresenter;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class ProjectOverviewFragment extends Fragment implements ProjectOverviewViewInt{

    private ProjectOverviewPresenterInt projectOverviewPresenter;
    private String pid;

    private TextView projectTitle, projectDescription;

    public ProjectOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pid = getArguments().getString("projectId");

        this.projectOverviewPresenter = new ProjectOverviewPresenter(this, pid);
        projectOverviewPresenter.getProjectDetails();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_overview, container, false);

        projectTitle = view.findViewById(R.id.projectOverviewTitle);
        projectDescription = view.findViewById(R.id.projectOverviewDesc);

        return view;
    }

    // Set the provided details into the respective views
    @Override
    public void setProjectDetails(String titleViewText, String descriptionViewText) {

        projectTitle.setText(titleViewText);
        projectDescription.setText(descriptionViewText);

    }

}
