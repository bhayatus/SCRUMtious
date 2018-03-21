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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import ca.mvp.scrumtious.scrumtious.utils.StringHelper;

public class BacklogFragment extends Fragment implements BacklogViewInt{

    private BacklogPresenterInt backlogPresenter;
    private String pid;

    private RecyclerView backlogList;
    private FirebaseRecyclerAdapter<UserStory, BacklogViewHolder> backlogAdapter;

    private SendToFragment sendToFragment;
    private LinearLayout emptyStateView;
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
        backlogList = view.findViewById(R.id.backlogRecyclerView);
        emptyStateView = view.findViewById(R.id.backlogEmptyStateView);
        setupRecyclerView();

        return view;
    }


    private void setupRecyclerView(){

        backlogList.setLayoutManager(new LinearLayoutManager(getActivity()));
        backlogAdapter = backlogPresenter.setupBacklogAdapter();
        backlogList.setAdapter(backlogAdapter);

    }

    // Show either the list of user stories, or the empty view
    @Override
    public void setEmptyStateView(){
        if(backlogAdapter.getItemCount() == 0){
            emptyStateView.setVisibility(View.VISIBLE);
            backlogList.setVisibility(View.GONE);
        }
        else{
            emptyStateView.setVisibility(View.GONE);
            backlogList.setVisibility(View.VISIBLE);
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

    // User wants to mark user story as completed or in progress
    @Override
    public void onClickChangeStatus(final String usid, final boolean newStatus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // User wants to change to completed
        if (newStatus){
            builder.setTitle("Mark As Completed?")
                    .setMessage("Are you sure you want to mark this user story as completed?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Change status to completed
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
                            // Change completed status of user story to in progress
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

    // Owner wants to delete the user story
    @Override
    public void onClickDeleteUserStory(final String usid){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete User Story")
                .setMessage("Are you sure you want to delete this user story?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deletingUserStoryDialog = new ProgressDialog(getContext());
                                deletingUserStoryDialog.setTitle("Delete User Story");
                                deletingUserStoryDialog.setCancelable(false);
                                deletingUserStoryDialog.setMessage("Attempting to delete user story...");
                                deletingUserStoryDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                deletingUserStoryDialog.show();

                                // Now actually delete it
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
        TextView nameView, pointsView, descriptionView, assignedToNameView, completedDateView;
        ImageButton completed;
        ImageButton delete;
        LinearLayout assignedToLayout;
        CardView card;

        ImageButton moreIcon;

        // Don't show whole description by default
        boolean showFull = false;


        public BacklogViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            nameView = (TextView) mView.findViewById(R.id.userStoryRowName);
            pointsView = (TextView) mView.findViewById(R.id.userStoryRowPoints);
            descriptionView = (TextView) mView.findViewById(R.id.userStoryRowDesc);
            assignedToNameView = (TextView) mView.findViewById(R.id.userStoryAssignedToName);
            completedDateView = (TextView) mView.findViewById(R.id.userStoryRowCompletedDate);
            completed = (ImageButton) mView.findViewById(R.id.userStoryRowCompleted);
            delete = (ImageButton) mView.findViewById(R.id.userStoryRowDelete);
            assignedToLayout = (LinearLayout) mView.findViewById(R.id.userStoryRowAssignedToLayout);
            card = (CardView) mView.findViewById(R.id.userStoryRowCardView);
            moreIcon = (ImageButton) mView.findViewById(R.id.userStoryRowMoreIcon);
        }


        // Populates each row of the recycler view with the user story details
        public void setDetails(String name, String points, String assignedToName, String description){
            nameView.setText(name);

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

            // If only 1 point, don't display as plural
            if (Integer.parseInt(points) == 1){
                pointsView.setText(points + " point");
            }
            else {
                pointsView.setText(points + " points");
            }

            assignedToNameView.setText(assignedToName);

            descriptionView.setText(displayDesc);

        }

        public void setCompletedDateDetails(String date){
            completedDateView.setVisibility(View.VISIBLE);
            completedDateView.setText(date);
        }

        public ImageButton getCompleted(){
            return this.completed;
        }

        public ImageButton getDelete(){
            return this.delete;
        }

        public void setCompletedInvisible() { this.completed.setVisibility(View.GONE);}

        // Only owner should be able to delete user stories
        public void setDeleteInvisible(){
            this.delete.setVisibility(View.GONE);
        }

        // Called when user story isn't assigned to a sprint
        public void setAssignedToLayoutInvisible(){
            assignedToLayout.setVisibility(View.GONE);
        }

        public void setCardRed(){
            card.setCardBackgroundColor(Color.parseColor("#F44336"));
        }

        public void setCardGreen(){
            card.setCardBackgroundColor(Color.parseColor("#8BC34A"));
        }

        // Either show or hide the more icon
        public void showOrHideMoreIcon(boolean hide){
            if (hide){
                moreIcon.setVisibility(View.GONE);
            }
            else{
                moreIcon.setVisibility(View.VISIBLE);
            }
        }

        public ImageButton getMoreIcon(){
            return moreIcon;
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
            descriptionView.setText(displayDesc);
        }

    }
}
