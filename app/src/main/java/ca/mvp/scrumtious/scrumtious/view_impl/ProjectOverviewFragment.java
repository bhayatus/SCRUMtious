package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.ContextThemeWrapper;
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

    public CardView projectOverviewSprintCard, projectOverviewUserStoryCard, projectOverviewFragmentVelocityCardView;
    private TextView projectOverviewFragmentTitleTextView, projectOverviewFragmentDescTextView, sprintRowNameProjectOverview,
            sprintRowDescriptionProjectOverview, sprintRowStartToEndProjectOverview, projectOverviewEmptyCurrentSprint, projectOverviewEmptyProgressView,
            projectOverviewVelocityNotEmptyStateView, projectOverviewFragmentVelocityTextView, projectOverviewDaysNotEmptyStateView, projectOverviewDays;
    private ProgressBar projectOverviewProgressBar;
    private TextView projectOverviewProgressBarText;

    private Handler refreshProgressHandler;
    private Runnable refreshRunnable;

    public ProjectOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pid = getArguments().getString("projectId");
        projectOverviewPresenter = new ProjectOverviewPresenter(this, pid);
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

        projectOverviewSprintCard = view.findViewById(R.id.projectOverviewSprintCard);
        projectOverviewUserStoryCard = view.findViewById(R.id.projectOverviewUserStoryCard);
        projectOverviewFragmentVelocityCardView = view.findViewById(R.id.projectOverviewFragmentVelocityCardView);
        projectOverviewEmptyCurrentSprint = view.findViewById(R.id.projectOverviewEmptyCurrentSprint);
        projectOverviewEmptyProgressView = view.findViewById(R.id.projectOverviewEmptyProgressView);
        projectOverviewFragmentTitleTextView = view.findViewById(R.id.projectOverviewFragmentTitleTextView);
        projectOverviewFragmentDescTextView = view.findViewById(R.id.projectOverviewFragmentDescTextView);
        sprintRowNameProjectOverview = view.findViewById(R.id.sprintRowNameProjectOverview);
        sprintRowDescriptionProjectOverview = view.findViewById(R.id.sprintRowDescriptionProjectOverview);
        sprintRowStartToEndProjectOverview = view.findViewById(R.id.sprintRowStartToEndProjectOverview);
        projectOverviewVelocityNotEmptyStateView = view.findViewById(R.id.projectOverviewVelocityNotEmptyStateView);
        projectOverviewFragmentVelocityTextView = view.findViewById(R.id.projectOverviewFragmentVelocityTextView);
        projectOverviewDaysNotEmptyStateView = view.findViewById(R.id.projectOverviewDaysNotEmptyStateView);
        projectOverviewDays = view.findViewById(R.id.projectOverviewDays);

        projectOverviewProgressBar = view.findViewById(R.id.projectOverviewProgressBar);
        projectOverviewProgressBarText = view.findViewById(R.id.projectOverviewProgressBarText);

        projectOverviewSprintCard.setVisibility(View.GONE);
        projectOverviewEmptyCurrentSprint.setVisibility(View.VISIBLE);

        projectOverviewProgressBar.setVisibility(View.GONE);
        projectOverviewProgressBarText.setVisibility(View.GONE);
        projectOverviewEmptyProgressView.setVisibility(View.VISIBLE);

        projectOverviewVelocityNotEmptyStateView.setVisibility(View.GONE);
        projectOverviewDaysNotEmptyStateView.setVisibility(View.GONE);
        projectOverviewFragmentVelocityTextView.setVisibility(View.VISIBLE);
        projectOverviewDays.setVisibility(View.VISIBLE);

        projectOverviewFragmentVelocityCardView.setOnClickListener(null);

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
        projectOverviewFragmentTitleTextView.setText(titleViewText);
        projectOverviewFragmentDescTextView.setText(descriptionViewText);

    }

    @Override
    public void setCurrentSprintDetails(final String currentSprintId, String sprintTitle, String sprintDesc, String dates) {

        if (currentSprintId.equals("")){
            projectOverviewSprintCard.setVisibility(View.GONE);
            projectOverviewEmptyCurrentSprint.setVisibility(View.VISIBLE);
            return;
        }

        projectOverviewSprintCard.setVisibility(View.VISIBLE);
        projectOverviewEmptyCurrentSprint.setVisibility(View.GONE);
        sprintRowNameProjectOverview.setText(sprintTitle);
        sprintRowDescriptionProjectOverview.setText(sprintDesc);
        sprintRowStartToEndProjectOverview.setText(dates);

        // Go to current sprint
        projectOverviewSprintCard.setOnClickListener(new View.
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
            projectOverviewUserStoryCard.setVisibility(View.GONE);
            projectOverviewProgressBar.setVisibility(View.GONE);
            projectOverviewProgressBarText.setVisibility(View.GONE);
            projectOverviewEmptyProgressView.setVisibility(View.VISIBLE);
            return;
        }

        projectOverviewProgressBar.setVisibility(View.VISIBLE);
        projectOverviewProgressBarText.setVisibility(View.VISIBLE);
        projectOverviewEmptyProgressView.setVisibility(View.GONE);

        long percent = (completed*100)/total;

        projectOverviewProgressBar.setProgress((int)percent);
        projectOverviewProgressBarText.setText(percent+"%");


    }

    @Override
    public void setCurrentVelocity(long currentVelocity) {
        // Error
        if (currentVelocity == -1){
            projectOverviewFragmentVelocityTextView.setVisibility(View.VISIBLE);
            projectOverviewVelocityNotEmptyStateView.setVisibility(View.GONE);
            projectOverviewFragmentVelocityCardView.setOnClickListener(null); // Don't let them change it
            return;
        }

        projectOverviewVelocityNotEmptyStateView.setText(String.valueOf(currentVelocity));
        projectOverviewVelocityNotEmptyStateView.setVisibility(View.VISIBLE);
        projectOverviewFragmentVelocityTextView.setVisibility(View.GONE);

        // User wants to change velocity
        projectOverviewFragmentVelocityCardView.setOnClickListener(new View.OnClickListener() {
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
            projectOverviewDays.setVisibility(View.VISIBLE);
            projectOverviewDaysNotEmptyStateView.setVisibility(View.GONE);
            return;
        }

        projectOverviewDaysNotEmptyStateView.setText(String.valueOf(days));
        projectOverviewDaysNotEmptyStateView.setVisibility(View.VISIBLE);
        projectOverviewDays.setVisibility(View.GONE);
    }

    private void onClickChangeVelocity(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_change_velocity, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.LoginAlertDialog));
        builder.setTitle("Change Velocity")
                .setView(alertView)
                .setMessage("Enter your new velocity.")
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText velocityET = alertView.findViewById(R.id.alertDialogueChangeVelocityEditText);

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
