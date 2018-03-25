package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectListPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectListViewInt;
import ca.mvp.scrumtious.scrumtious.model.Project;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectListPresenter;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;
import ca.mvp.scrumtious.scrumtious.utils.StringHelper;

public class ProjectListFragment extends Fragment implements ProjectListViewInt {

    private ProjectListPresenterInt projectListPresenterInt;
    private FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> allProjectsAdapter;
    private FirebaseRecyclerAdapter<Project, ProjectListFragment.ProjectsViewHolder> myProjectsAdapter;

    private LinearLayout projectListFragmentNoProjectsEmptyStateView;
    private TextView projectListFragmentOwnedProjectsTextView;
    private Switch projectListFragmentOwnedProjectsSwitch;
    private Button projectListFragmentAddProjectButton;
    private RecyclerView projectListFragmentRecyclerView;
    private ProgressDialog loadingProjectsDialog;

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
        projectListFragmentOwnedProjectsTextView = view.findViewById(R.id.projectListFragmentOwnedProjectsTextView);
        projectListFragmentRecyclerView = view.findViewById(R.id.projectListFragmentRecyclerView);
        projectListFragmentOwnedProjectsSwitch = view.findViewById(R.id.projectListFragmentOwnedProjectsSwitch);
        setupRecyclerView();
        projectListFragmentAddProjectButton = view.findViewById(R.id.projectListFragmentAddProjectButton);

        projectListFragmentAddProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddNewProject(view);
            }
        });

        projectListFragmentNoProjectsEmptyStateView = view.findViewById(R.id.projectListFragmentNoProjectsEmptyStateView);

        return view;

    }

    private void setupRecyclerView(){

        // Creates a dialog that appears to tell the user that projects are being loaded
        loadingProjectsDialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);;
        loadingProjectsDialog.setTitle("Load Projects");
        loadingProjectsDialog.setCancelable(false);
        loadingProjectsDialog.setMessage("Now loading your projects...");
        loadingProjectsDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingProjectsDialog.show();

        projectListFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // The adapter that displays only projects owned by the user
        myProjectsAdapter = projectListPresenterInt.setupProjectListAdapter(loadingProjectsDialog, true);

        // The adapter that displays all projects that the user is in
        allProjectsAdapter = projectListPresenterInt.setupProjectListAdapter(loadingProjectsDialog, false);

        // Show all projects by default
        projectListFragmentRecyclerView.setAdapter(allProjectsAdapter);

        projectListFragmentOwnedProjectsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // User only wants to see the projects that they own
                if (isChecked){

                    projectListFragmentRecyclerView.setAdapter(myProjectsAdapter);
                }
                // User wants to see all projects
                else{
                    projectListFragmentRecyclerView.setAdapter(allProjectsAdapter);
                }
            }
        });
    }

    // Switches between empty state screen and regular adapters based on item count being empty or not
    @Override
    public void setEmptyStateView(){
        if (allProjectsAdapter.getItemCount() == 0 && myProjectsAdapter.getItemCount() == 0){
            projectListFragmentNoProjectsEmptyStateView.setVisibility(View.VISIBLE);
            projectListFragmentOwnedProjectsSwitch.setVisibility(View.GONE);
            projectListFragmentOwnedProjectsTextView.setVisibility(View.GONE);
            projectListFragmentRecyclerView.setVisibility(View.GONE);
        }
        else{
            projectListFragmentNoProjectsEmptyStateView.setVisibility(View.GONE);
            projectListFragmentRecyclerView.setVisibility(View.VISIBLE);
            projectListFragmentOwnedProjectsTextView.setVisibility(View.VISIBLE);
            projectListFragmentOwnedProjectsSwitch.setVisibility(View.VISIBLE);
        }
    }

    // User clicked on a specific project
    @Override
    public void goToProjectScreen(String pid) {
        Intent intent = new Intent(getActivity(), IndividualProjectActivity.class);
        intent.putExtra("projectId", pid);
        startActivity(intent);
    }


    // User clicked on the add project button, go to the screen
    public void onClickAddNewProject(View view){
        Intent intent = new Intent(this.getActivity(), CreateProjectActivity.class);
        this.getActivity().startActivity(intent);
    }

    @Override
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

    // Viewholder class to display the project list
    public static class ProjectsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView projectRowTitleTextView, projectRowEmailAddressTextView, projectRowProjectDescTextView, projectRowProjectCreatedDateTextView, projectRowProjectNumberOfMembersTextView, projectRowProjectNumberOfSprintsTextView;
        ImageView projectRowProjectNumberOfMembersImageView, projectRowProjectNumberOfSprintsImageView;
        ImageButton projectRowExpandDescIconImageButton;

        // Don't show whole description by default
        boolean showFull = false;

        public ProjectsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            projectRowTitleTextView = mView.findViewById(R.id.projectRowTitleTextView);
            projectRowEmailAddressTextView = mView.findViewById(R.id.projectRowEmailAddressTextView);
            projectRowProjectDescTextView = mView.findViewById(R.id.projectRowProjectDescTextView);
            projectRowProjectCreatedDateTextView = mView.findViewById(R.id.projectRowProjectCreatedDateTextView);
            projectRowProjectNumberOfMembersTextView = mView.findViewById(R.id.projectRowProjectNumberOfMembersTextView);
            projectRowProjectNumberOfSprintsTextView = mView.findViewById(R.id.projectRowProjectNumberOfSprintsTextView);
            projectRowProjectNumberOfMembersImageView = mView.findViewById(R.id.projectRowProjectNumberOfMembersImageView);
            projectRowProjectNumberOfSprintsImageView = mView.findViewById(R.id.projectRowProjectNumberOfSprintsImageView);
            projectRowExpandDescIconImageButton = mView.findViewById(R.id.projectRowExpandDescIconImageButton);

        }


        // Populates each row of the recycler view with the project details
        public void setDetails(String title, String ownerEmailAddress, String description, String creationDate, String numMembers, String numSprints){

            // Show whole description by default
            String displayDesc = description;


            // Shorten the description
            if (!showFull){
                displayDesc = StringHelper.shortenDescription(description);
            }

            // Description is showing entirely, hide show more icon
            if (displayDesc.trim().equals(description)){
                showOrHideMoreIcon(true);
            }
            // Description has been shortened, don't show more icon
            else{
                showOrHideMoreIcon(false);
            }

            projectRowTitleTextView.setText(title);
            projectRowEmailAddressTextView.setText("Owner: "+ ownerEmailAddress);
            projectRowProjectDescTextView.setText(displayDesc);
            projectRowProjectCreatedDateTextView.setText(creationDate);
            projectRowProjectNumberOfMembersTextView.setText(numMembers);
            projectRowProjectNumberOfSprintsTextView.setText(numSprints);
        }

        // Either show or hide the more icon
        public void showOrHideMoreIcon(boolean hide){
            if (hide){
                projectRowExpandDescIconImageButton.setVisibility(View.GONE);
            }
            else{
                projectRowExpandDescIconImageButton.setVisibility(View.VISIBLE);
            }
        }

        public ImageButton getProjectRowExpandDescIconImageButton(){
            return projectRowExpandDescIconImageButton;
        }

        // User clicked on the show more icon, switch boolean state and reset description
        public void switchShowFull(String description){
            showFull = !showFull;

            // Show whole description by default
            String displayDesc = description;

            // Shorten the description
            if (!showFull){
                displayDesc = StringHelper.shortenDescription(description);
            }

            // Description is showing entirely, hide show more icon
            if (displayDesc.trim().equals(description)){
                showOrHideMoreIcon(true);
            }
            // Description has been shortened, don't show more icon
            else{
                showOrHideMoreIcon(false);
            }

            // Reset the description
            projectRowProjectDescTextView.setText(displayDesc);
        }


    }
}
