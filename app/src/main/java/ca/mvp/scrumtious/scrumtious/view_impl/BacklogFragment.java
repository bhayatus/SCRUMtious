package ca.mvp.scrumtious.scrumtious.view_impl;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.BacklogPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.BacklogViewInt;
import ca.mvp.scrumtious.scrumtious.model.UserStory;
import ca.mvp.scrumtious.scrumtious.presenter_impl.BacklogPresenter;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class BacklogFragment extends Fragment implements BacklogViewInt{

    private BacklogPresenterInt backlogPresenter;
    private String pid;

    private RecyclerView backlogFragmentRecyclerView;
    private FirebaseRecyclerAdapter<UserStory, BacklogViewHolder> backlogAdapter;

    private SendToFragment sendToFragment;
    private LinearLayout backlogFragmentNoUserStoriesEmptyStateView;
    private ProgressDialog deletingUserStoryDialog;


    public BacklogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pid = getArguments().getString("projectId");
        String type = getArguments().getString("type");
        String sprintId = getArguments().getString("sprintId");
        this.backlogPresenter = new BacklogPresenter(this, pid, type, sprintId);
    }

    @Override
    public void onPause() {

        // Pop up has to be dismissed
        if (sendToFragment != null){
            sendToFragment.dismiss();
        }

        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backlog, container, false);
        backlogFragmentRecyclerView = view.findViewById(R.id.backlogFragmentRecyclerView);
        backlogFragmentNoUserStoriesEmptyStateView = view.findViewById(R.id.backlogFragmentNoUserStoriesEmptyStateView);
        setupRecyclerView();

        return view;
    }


    private void setupRecyclerView(){

        backlogFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        backlogAdapter = backlogPresenter.setupBacklogAdapter();
        backlogFragmentRecyclerView.setAdapter(backlogAdapter);

    }

    // Show either the list of user stories, or the empty view
    @Override
    public void setEmptyStateView(){
        if(backlogAdapter.getItemCount() == 0){
            backlogFragmentNoUserStoriesEmptyStateView.setVisibility(View.VISIBLE);
            backlogFragmentRecyclerView.setVisibility(View.GONE);
        }
        else{
            backlogFragmentNoUserStoriesEmptyStateView.setVisibility(View.GONE);
            backlogFragmentRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    // User clicks on a specific user story
    @Override
    public void goToUserStoryScreen(String usid) {
        Intent intent = new Intent(getActivity(), IndividualUserStoryActivity.class);
        intent.putExtra("projectId", pid);
        intent.putExtra("userStoryId", usid);
        startActivity(intent);
    }

    // User wants to mark user story as userStoryRowCompletedUserStoryImageButton or in progress
    @Override
    public void onClickChangeStatus(final String usid, final boolean newStatus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // User wants to change to userStoryRowCompletedUserStoryImageButton
        if (newStatus){
            builder.setTitle("Mark As Completed?")
                    .setMessage("Are you sure you want to mark this user story as userStoryRowCompletedUserStoryImageButton?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Change status to userStoryRowCompletedUserStoryImageButton
                            backlogPresenter.changeCompletedStatus(usid, newStatus);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    })
                    .create().show();
        }
        // User wants to change to in progress
        else{
            builder.setTitle("Mark As In Progress?")
                    .setMessage("Are you sure you want to mark this user story as in progress?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Change userStoryRowCompletedUserStoryImageButton status of user story to in progress
                            backlogPresenter.changeCompletedStatus(usid, newStatus);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    })
                    .create().show();
        }


    }

    // Owner wants to userStoryRowDeleteUserStoryImageButton the user story
    @Override
    public void onClickDeleteUserStory(final String usid){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete User Story")
                .setMessage("Are you sure you want to userStoryRowDeleteUserStoryImageButton this user story?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deletingUserStoryDialog = new ProgressDialog(getContext());
                                deletingUserStoryDialog.setTitle("Delete User Story");
                                deletingUserStoryDialog.setCancelable(false);
                                deletingUserStoryDialog.setMessage("Attempting to userStoryRowDeleteUserStoryImageButton user story...");
                                deletingUserStoryDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                deletingUserStoryDialog.show();

                                // Now actually userStoryRowDeleteUserStoryImageButton it
                                backlogPresenter.deleteUserStory(usid);
                            }

                        }
                )
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remain in this screen
                    }
                })
                .create().show();
    }

    @Override
    public void showMessage(String message, boolean showAsToast) {

        if (sendToFragment != null){
            sendToFragment.dismiss();
        }

        // Close dialog if it is still running
        if (deletingUserStoryDialog != null && deletingUserStoryDialog.isShowing()){
            deletingUserStoryDialog.dismiss();
        }

        // Show message in toast so it persists across activity transitions
        if (showAsToast){
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }

        else {
            // Call the utils class method to handle making the snackbar
            SnackbarHelper.showSnackbar(getActivity(), message);
        }

    }

    // Open up the dialog fragment to choose which sprint to assign this user story to
    @Override
    public void onLongClickUserStory(String usid){

        sendToFragment = SendToFragment.newInstance(pid,usid);
        sendToFragment. setBacklogView(this);
        sendToFragment.show(getFragmentManager(), "SendToFragment");
    }


    // Viewholder class to display user stories
    public static class BacklogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView userStoryRowUserStoryNameTextView, userStoryRowPointsUserStoryPointsTextView,
                userStoryRowUserStoryDescTextView, userStoryAssignedUserStoryAssignedToMemberNameTextView, userStoryRowUserStoryCompletedDateTextView;
        ImageButton userStoryRowCompletedUserStoryImageButton;
        ImageButton userStoryRowDeleteUserStoryImageButton;
        LinearLayout userStoryRowAssignedToRelativeLayout;
        CardView userStoryRowCardView;

        public BacklogViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            userStoryRowUserStoryNameTextView = (TextView) mView.findViewById(R.id.userStoryRowUserStoryNameTextView);
            userStoryRowPointsUserStoryPointsTextView = (TextView) mView.findViewById(R.id.userStoryRowPointsUserStoryPointsTextView);
            userStoryRowUserStoryDescTextView = (TextView) mView.findViewById(R.id.userStoryRowUserStoryDescTextView);
            userStoryAssignedUserStoryAssignedToMemberNameTextView = (TextView) mView.findViewById(R.id.userStoryAssignedUserStoryAssignedToMemberNameTextView);
            userStoryRowUserStoryCompletedDateTextView = (TextView) mView.findViewById(R.id.userStoryRowUserStoryCompletedDateTextView);
            userStoryRowCompletedUserStoryImageButton = (ImageButton) mView.findViewById(R.id.userStoryRowCompletedUserStoryImageButton);
            userStoryRowDeleteUserStoryImageButton = (ImageButton) mView.findViewById(R.id.userStoryRowDeleteUserStoryImageButton);
            userStoryRowAssignedToRelativeLayout = (LinearLayout) mView.findViewById(R.id.userStoryRowAssignedToRelativeLayout);
            userStoryRowCardView = (CardView) mView.findViewById(R.id.userStoryRowCardView);
        }


        // Populates each row of the recycler view with the user story details
        public void setDetails(String name, String points, String assignedToName, String description){
            userStoryRowUserStoryNameTextView.setText(name);


            // If only 1 point, don't display as plural
            if (Integer.parseInt(points) == 1){
                userStoryRowPointsUserStoryPointsTextView.setText(points + " point");
            }
            else {
                userStoryRowPointsUserStoryPointsTextView.setText(points + " points");
            }

            userStoryAssignedUserStoryAssignedToMemberNameTextView.setText(assignedToName);

            userStoryRowUserStoryDescTextView.setText(description);

        }

        public void setCompletedDateDetails(String date){
            userStoryRowUserStoryCompletedDateTextView.setVisibility(View.VISIBLE);
            userStoryRowUserStoryCompletedDateTextView.setText(date);
        }

        public ImageButton getUserStoryRowCompletedUserStoryImageButton(){
            return this.userStoryRowCompletedUserStoryImageButton;
        }

        public ImageButton getUserStoryRowDeleteUserStoryImageButton(){
            return this.userStoryRowDeleteUserStoryImageButton;
        }

        public void setCompletedInvisible() { this.userStoryRowCompletedUserStoryImageButton.setVisibility(View.GONE);}

        // Only owner should be able to userStoryRowDeleteUserStoryImageButton user stories
        public void setDeleteInvisible(){
            this.userStoryRowDeleteUserStoryImageButton.setVisibility(View.GONE);
        }

        // Called when user story isn't assigned to a sprint
        public void setAssignedToLayoutInvisible(){
            userStoryRowAssignedToRelativeLayout.setVisibility(View.GONE);
        }

        public void setCardRed(){
            userStoryRowCardView.setCardBackgroundColor(Color.parseColor("#F44336"));
        }

        public void setCardGreen(){
            userStoryRowCardView.setCardBackgroundColor(Color.parseColor("#8BC34A"));
        }

    }
}
