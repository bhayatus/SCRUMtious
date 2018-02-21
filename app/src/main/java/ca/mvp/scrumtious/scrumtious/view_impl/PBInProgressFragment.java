package ca.mvp.scrumtious.scrumtious.view_impl;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.PBInProgressPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.PBInProgressViewInt;
import ca.mvp.scrumtious.scrumtious.model.UserStory;
import ca.mvp.scrumtious.scrumtious.presenter_impl.PBInProgressPresenter;

public class PBInProgressFragment extends Fragment implements PBInProgressViewInt {


    private ProgressDialog deletingUserStoryDialog;
    private PBInProgressPresenterInt pbInProgressPresenter;
    private RecyclerView inProgressList;
    private FirebaseRecyclerAdapter<UserStory, InProgressViewHolder> inProgressAdapter;
    public PBInProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pid = getArguments().getString("projectId");
        this.pbInProgressPresenter = new PBInProgressPresenter(this, pid);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pbin_progress, container, false);
        inProgressList = view.findViewById(R.id.productBacklogInProgressRecyclerView);
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView(){

        inProgressList.setLayoutManager(new LinearLayoutManager(getActivity()));
        inProgressAdapter = pbInProgressPresenter.setupInProgressAdapter(inProgressList);
        inProgressList.setAdapter(inProgressAdapter);

    }



    // User clicks on a specific user story
    @Override
    public void goToUserStoryScreen(String usid) {
        // TODO
    }

    // User wants to mark user story as completed
    @Override
    public void onClickChangeStatus(final String usid, final boolean newStatus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Mark As Completed?")
                .setMessage("Are you sure you want to mark this user story as completed?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Change completed status of user story
                        pbInProgressPresenter.changeCompletedStatus(usid, newStatus);
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

    @Override
    public void onClickDeleteUserStory(final String usid) {
        LayoutInflater inflater = (this).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_delete_project, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete User Story?")
                .setView(alertView)
                .setMessage("Are you sure you want to delete this user story? Enter your password below to confirm.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password and delete project
                        EditText passwordET = (EditText) alertView.findViewById(R.id.alert_dialogue_delete_password_text_field);
                        String password = passwordET.getText().toString().trim();

                        // Cannot send null password
                        if(password == null){
                            showMessage("Incorrect password, could not delete user story.");
                        }

                        else {
                            // Cannot send empty string
                            if(password.length() == 0) {
                                showMessage("Incorrect password, could not delete user story.");
                            }
                            else {

                                // Creates a dialog that appears to tell the user that deleting a user story is occurring
                                deletingUserStoryDialog = new ProgressDialog(getContext());
                                deletingUserStoryDialog.setTitle("Delete User Story");
                                deletingUserStoryDialog.setCancelable(false);
                                deletingUserStoryDialog.setMessage("Attempting to delete user story...");
                                deletingUserStoryDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                deletingUserStoryDialog.show();

                                pbInProgressPresenter.validatePassword(password, usid);
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }

    @Override
    public void showMessage(String message) {

        // Close dialog if they it is still running
        if (deletingUserStoryDialog != null && deletingUserStoryDialog.isShowing()){
            deletingUserStoryDialog.dismiss();
        }

        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();

    }


    public static class InProgressViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView nameView, pointsView;
        ImageButton completed;
        ImageButton delete;

        public InProgressViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            nameView = (TextView) mView.findViewById(R.id.userStoryIPRowName);
            pointsView = (TextView) mView.findViewById(R.id.userStoryIPRowPoints);
            completed = (ImageButton) mView.findViewById(R.id.userStoryIPRowCompleted);
            delete = (ImageButton) mView.findViewById(R.id.userStoryIPRowDelete);
        }


        // Populates each row of the recycler view with the project details
        public void setDetails(String name, String points){
            nameView.setText(name);
            pointsView.setText("Points: "+ points);
        }

        public ImageButton getCompleted(){
            return this.completed;
        }

        public ImageButton getDelete(){
            return this.delete;
        }

        // Only owner should be able to delete user stories
        public void setDeleteInvisible(){
            this.delete.setVisibility(View.GONE);
        }
    }
}
