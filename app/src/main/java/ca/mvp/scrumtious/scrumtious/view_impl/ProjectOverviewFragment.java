package ca.mvp.scrumtious.scrumtious.view_impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectOverviewPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectOverviewViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectOverviewPresenter;

public class ProjectOverviewFragment extends Fragment implements ProjectOverviewViewInt{

    private ProjectOverviewPresenterInt projectOverviewPresenter;
    private String pid;

    public CardView currentSprintCard, currentUserStoryCard;
    private TextView projectTitle, projectDescription, sprintName, sprintDescription, sprintDates, emptySprintView,
    emptyProgressView;
    private ProgressBar userStoryProgressCircle;
    private TextView userStoryProgressPercent;

    public ProjectOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pid = getArguments().getString("projectId");

        this.projectOverviewPresenter = new ProjectOverviewPresenter(this, pid);
    }

    @Override
    public void onResume() {
        projectOverviewPresenter.setupProjectDetailsListener();
        projectOverviewPresenter.setupCurrentSprintListener();
        projectOverviewPresenter.getUserStoryProgress();
        super.onResume();
    }

    @Override
    public void onPause() {
        projectOverviewPresenter.removeProjectDetailsListener();
        projectOverviewPresenter.removeCurrentSprintListener();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_overview, container, false);

        currentSprintCard = view.findViewById(R.id.projectOverviewSprintCard);
        currentUserStoryCard = view.findViewById(R.id.projectOverviewUserStoryCard);
        emptySprintView = view.findViewById(R.id.projectOverviewEmptyCurrentSprint);
        emptyProgressView = view.findViewById(R.id.projectOverviewEmptyProgressView);
        projectTitle = view.findViewById(R.id.projectOverviewTitle);
        projectDescription = view.findViewById(R.id.projectOverviewDesc);
        sprintName = view.findViewById(R.id.sprintRowNameProjectOverview);
        sprintDescription = view.findViewById(R.id.sprintRowDescriptionProjectOverview);
        sprintDates = view.findViewById(R.id.sprintRowStartToEndProjectOverview);

        userStoryProgressCircle = view.findViewById(R.id.projectOverviewProgressBar);
        userStoryProgressPercent = view.findViewById(R.id.projectOverviewProgressBarText);

        currentSprintCard.setVisibility(View.GONE);
        emptySprintView.setVisibility(View.VISIBLE);

        userStoryProgressCircle.setVisibility(View.GONE);
        userStoryProgressPercent.setVisibility(View.GONE);
        emptyProgressView.setVisibility(View.VISIBLE);


        return view;
    }

    // Set the provided details into the respective views
    @Override
    public void setProjectDetails(String titleViewText, String descriptionViewText) {
        projectTitle.setText(titleViewText);
        projectDescription.setText(descriptionViewText);

    }

    @Override
    public void setCurrentSprintDetails(final String currentSprintId, String sprintTitle, String sprintDesc, String dates) {

        if (currentSprintId.equals("")){
            currentSprintCard.setVisibility(View.GONE);
            emptySprintView.setVisibility(View.VISIBLE);
            return;
        }

        currentSprintCard.setVisibility(View.VISIBLE);
        emptySprintView.setVisibility(View.GONE);
        sprintName.setText(sprintTitle);
        sprintDescription.setText(sprintDesc);
        sprintDates.setText(dates);

        // Go to current sprint
        currentSprintCard.setOnClickListener(new View.
                OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), IndividualSprintActivity.class);
                    intent.putExtra("projectId", pid);
                    intent.putExtra("sprintId", currentSprintId);
                    startActivity(intent);
            }
        });
    }

    @Override
    public void setCurrentProgressCircle(long total, long completed) {

//        Handler handler = new Handler();
//
//        // Refresh in 5 seconds
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                projectOverviewPresenter.getUserStoryProgress();
//            }
//        }, 5000);

        if (total == 0){
            currentUserStoryCard.setVisibility(View.GONE);
            userStoryProgressCircle.setVisibility(View.GONE);
            userStoryProgressPercent.setVisibility(View.GONE);
            emptyProgressView.setVisibility(View.VISIBLE);
            return;
        }

        userStoryProgressCircle.setVisibility(View.VISIBLE);
        userStoryProgressPercent.setVisibility(View.VISIBLE);
        emptyProgressView.setVisibility(View.GONE);

        long percent = (completed*100)/total;

        userStoryProgressCircle.setProgress((int)percent);
        userStoryProgressPercent.setText(percent+"%");


    }

}
