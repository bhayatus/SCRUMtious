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

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectOverviewPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectOverviewViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectOverviewPresenter;

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
        projectOverviewPresenter.setupDetails();

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

    public void showMessage(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        // Dismisses automatically
                    }
                }).show();
    }

    @Override
    public void setDetails(String titleViewText, String descriptionViewText) {

        projectTitle.setText(titleViewText);
        projectDescription.setText(descriptionViewText);

    }
}
