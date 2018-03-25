package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectMembersPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectMembersViewInt;
import ca.mvp.scrumtious.scrumtious.model.User;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectMembersPresenter;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class ProjectMembersFragment extends Fragment implements ProjectMembersViewInt {

    private ProjectMembersPresenterInt projectMembersPresenter;

    private TextView projectMembersFragmentNoMembersEmptyView;
    private RecyclerView projectMembersFragmentRecyclerView;
    private FirebaseRecyclerAdapter<User, ProjectMembersFragment.MembersViewHolder> membersListAdapter;
    private FloatingActionButton projectMembersFragmentAddMemberButton;
    private ProgressDialog invitingProgressDialog, deletingMemberProgressDialog;

    public ProjectMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pid = getArguments().getString("projectId");
        projectMembersPresenter = new ProjectMembersPresenter(this, pid);
        projectMembersPresenter.checkIfOwner(); // Only owner can invite users, check here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_members, container, false);
        projectMembersFragmentNoMembersEmptyView = view.findViewById(R.id.projectMembersFragmentNoMembersEmptyView);
        projectMembersFragmentRecyclerView = view.findViewById(R.id.projectMembersFragmentRecyclerView);
        setupRecyclerView();
        projectMembersFragmentAddMemberButton = view.findViewById(R.id.projectMembersFragmentAddMemberButton);

        projectMembersFragmentAddMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickInviteMember(view);
            }
        });

        return view;
    }

    // Only owner of the project can view and click the add member floating action button
    @Override
    public void setAddMemberInvisible() {
        projectMembersFragmentAddMemberButton.setVisibility(View.GONE);
    }

    private void setupRecyclerView(){

        // Sets up the layout so that results are displayed in reverse order, meaning new items are added to the bottom
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        projectMembersFragmentRecyclerView.setLayoutManager(mLayoutManager);
        membersListAdapter = projectMembersPresenter.setupMemberListAdapter();
        projectMembersFragmentRecyclerView.setAdapter(membersListAdapter);

    }

    // Sets the view to show if you are the only member
    @Override
    public void setEmptyStateView(){
        if (membersListAdapter.getItemCount() == 1){
            projectMembersFragmentNoMembersEmptyView.setVisibility(View.VISIBLE);
        }
        else{
            projectMembersFragmentNoMembersEmptyView.setVisibility(View.GONE);
        }
    }

    // Owner clicked on the invite member button
    public void onClickInviteMember(View view) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_add_member, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.LoginAlertDialog));
        builder.setTitle("Invite New Member")
                .setView(alertView)
                .setMessage("Enter the e-mail address of the user you want to invite.")
                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password before inviting member
                        EditText emailET = alertView.findViewById(R.id.alertDialogueAddMemberEmailEditText);
                        String emailAddress = emailET.getText().toString().trim();

                        // Cannot send null email address
                        if(emailAddress == null){
                            showMessage("Please enter the e-mail address of the user to invite.", false);
                        }

                        else {
                            // Cannot send empty string
                            if(emailAddress.length() == 0) {
                                showMessage("Please enter the e-mail address of the user to invite.", false);
                            }
                            else {

                                // Creates a dialog that appears to tell the user that the user is being invited
                                invitingProgressDialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);;
                                invitingProgressDialog.setTitle("Invite User");
                                invitingProgressDialog.setCancelable(false);
                                invitingProgressDialog.setMessage("Inviting user to project...");
                                invitingProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                invitingProgressDialog.show();

                                // Password is of valid type, send it to the backend to validate
                                projectMembersPresenter.checkBeforeInvite(emailAddress);
                            }
                        }
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


    // When the delete member button is clicked for an individual member
    public void onClickDelete(final String uid){
        LayoutInflater inflater = (this).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_delete_project, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.LoginAlertDialog));
        builder.setTitle("Delete Member?")
                .setView(alertView)
                .setMessage("Are you sure you want to delete this member? Enter your password below to confirm.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password and delete member
                        EditText passwordET = alertView.findViewById(R.id.alertDialogueDeletePasswordEditText);
                        String password = passwordET.getText().toString().trim();

                        // Cannot send null password
                        if(password == null){
                            showMessage("Incorrect password, could not delete the member.", false);
                        }

                        else {
                            // Cannot send empty string
                            if(password.length() == 0) {
                                showMessage("Incorrect password, could not delete the member.", false);
                            }
                            else {

                                // Creates a dialog that appears to tell the user that the member is being deleted
                                deletingMemberProgressDialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);;
                                deletingMemberProgressDialog.setTitle("Delete Member");
                                deletingMemberProgressDialog.setCancelable(false);
                                deletingMemberProgressDialog.setMessage("Attempting to delete member...");
                                deletingMemberProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                deletingMemberProgressDialog.show();

                                // Password is of valid type, send it to the backend to validate
                                projectMembersPresenter.validatePassword(password, uid);
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
    public void showMessage(String message, boolean showAsToast) {

        // Close dialogs if they are still running
        if(invitingProgressDialog != null && invitingProgressDialog.isShowing()){
            invitingProgressDialog.dismiss();
        }

        if(deletingMemberProgressDialog != null && deletingMemberProgressDialog.isShowing()){
            deletingMemberProgressDialog.dismiss();
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

    // Viewholder class to display members of a project
    public static class MembersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView memberRowEmailTextView;
        ImageButton memberRowDeleteImageButton;
        ImageButton memberRowOwnerImageButton;

        public MembersViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            memberRowEmailTextView = mView.findViewById(R.id.memberRowEmailTextView);
            memberRowDeleteImageButton = mView.findViewById(R.id.memberRowDeleteImageButton);
            memberRowOwnerImageButton = mView.findViewById(R.id.memberRowOwnerImageButton);
        }

        // Populates each row of the recycler view with the member details
        public void setDetails(String emailAddress){
            memberRowEmailTextView.setText(emailAddress);
        }

        // Gets the delete button
        public ImageButton getMemberRowDeleteImageButton(){
            return memberRowDeleteImageButton;
        }

        // Only owner should be able to view this button, with the exception of their own self
        public void setDeleteInvisible(){
            memberRowDeleteImageButton.setVisibility(View.GONE);
        }

        // Only owner should have this icon displayed
        public void setOwnerInvisible(){
            memberRowOwnerImageButton.setVisibility(View.GONE);}
    }

}
