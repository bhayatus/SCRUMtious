package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    public CardView currentSprintCard, currentUserStoryCard, currentVelocityCard;
    private TextView projectTitle, projectDescription, sprintName, sprintDescription, sprintDates, emptySprintView,
    emptyProgressView, velocityView, emptyVelocityView, daysView, emptyDaysView;
    private ProgressBar userStoryProgressCircle;
    private TextView userStoryProgressPercent;

    private Handler refreshProgressHandler;
    private Runnable refreshRunnable;

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
        projectOverviewPresenter.setupCurrentVelocityListener();
        projectOverviewPresenter.setupDaysListener();
        projectOverviewPresenter.getUserStoryProgress();
        super.onResume();
    }

    @Override
    public void onPause() {
        projectOverviewPresenter.removeProjectDetailsListener();
        projectOverviewPresenter.removeCurrentSprintListener();
        projectOverviewPresenter.removeCurrentVelocityListener();
        projectOverviewPresenter.removeDaysListener();
        if (refreshProgressHandler != null){
            refreshProgressHandler.removeCallbacks(refreshRunnable);
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_overview, container, false);

        currentSprintCard = view.findViewById(R.id.projectOverviewSprintCard);
        currentUserStoryCard = view.findViewById(R.id.projectOverviewUserStoryCard);
        currentVelocityCard = view.findViewById(R.id.projectOverviewVelocityCard);
        emptySprintView = view.findViewById(R.id.projectOverviewEmptyCurrentSprint);
        emptyProgressView = view.findViewById(R.id.projectOverviewEmptyProgressView);
        projectTitle = view.findViewById(R.id.projectOverviewTitle);
        projectDescription = view.findViewById(R.id.projectOverviewDesc);
        sprintName = view.findViewById(R.id.sprintRowNameProjectOverview);
        sprintDescription = view.findViewById(R.id.sprintRowDescriptionProjectOverview);
        sprintDates = view.findViewById(R.id.sprintRowStartToEndProjectOverview);
        velocityView = view.findViewById(R.id.projectOverviewVelocityNotEmptyStateView);
        emptyVelocityView = view.findViewById(R.id.projectOverviewVelocity);
        daysView = view.findViewById(R.id.projectOverviewDaysNotEmptyStateView);
        emptyDaysView = view.findViewById(R.id.projectOverviewDays);

        userStoryProgressCircle = view.findViewById(R.id.projectOverviewProgressBar);
        userStoryProgressPercent = view.findViewById(R.id.projectOverviewProgressBarText);

        currentSprintCard.setVisibility(View.GONE);
        emptySprintView.setVisibility(View.VISIBLE);

        userStoryProgressCircle.setVisibility(View.GONE);
        userStoryProgressPercent.setVisibility(View.GONE);
        emptyProgressView.setVisibility(View.VISIBLE);

        velocityView.setVisibility(View.GONE);
        daysView.setVisibility(View.GONE);
        emptyVelocityView.setVisibility(View.VISIBLE);
        emptyDaysView.setVisibility(View.VISIBLE);

        currentVelocityCard.setOnClickListener(null);

        return view;
    }

    public void showMessage(String message, boolean showAsToast) {

        // Show message in toast so it persists across activity transitions
        if (showAsToast){
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }

        else {
            // Call the utils class method to handle making the snackbar
            SnackbarHelper.showSnackbar(getActivity(), message);
        }
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
                    intent.putExtra("cameFromOverviewScreen", "true");
                    startActivity(intent);
            }
        });
    }

    @Override
    public void setCurrentProgressCircle(long total, long completed) {

        refreshProgressHandler = new Handler();

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                projectOverviewPresenter.getUserStoryProgress();
            }
        };

        // Refresh in 5 seconds
        refreshProgressHandler.postDelayed(refreshRunnable, 5000);

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

    @Override
    public void setCurrentVelocity(long currentVelocity) {
        // Error
        if (currentVelocity == -1){
            emptyVelocityView.setVisibility(View.VISIBLE);
            velocityView.setVisibility(View.GONE);
            currentVelocityCard.setOnClickListener(null); // Don't let them change it
            return;
        }

        velocityView.setText(String.valueOf(currentVelocity));
        velocityView.setVisibility(View.VISIBLE);
        emptyVelocityView.setVisibility(View.GONE);

        // User wants to change velocity
        currentVelocityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChangeVelocity();
            }
        });
    }

    @Override
    public void setDays(long days) {
        // Error
        if (days == -1){
            emptyDaysView.setVisibility(View.VISIBLE);
            daysView.setVisibility(View.GONE);
            return;
        }

        daysView.setText(String.valueOf(days));
        daysView.setVisibility(View.VISIBLE);
        emptyDaysView.setVisibility(View.GONE);
    }

    private void onClickChangeVelocity(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_change_velocity, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Velocity")
                .setView(alertView)
                .setMessage("Enter your new velocity.")
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText velocityET = (EditText) alertView.findViewById(R.id.alert_dialogue_change_velocity_field);

                        if (velocityET.getText().toString().equals("") || velocityET.getText().toString() == null){
                            showMessage("Please enter a velocity.", false);
                            return;
                        }

                        long newVelocity = Long.parseLong(velocityET.getText().toString());

                        if (newVelocity < 1 || newVelocity > 9999){
                            showMessage("Please enter a velocity larger than 0.", false);
                            return;
                        }

                        projectOverviewPresenter.changeCurrentVelocity(newVelocity);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

}
