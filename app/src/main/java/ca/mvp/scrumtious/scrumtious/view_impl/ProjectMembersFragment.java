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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectMembersPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectMembersViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectMembersPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectMembersFragment extends Fragment implements ProjectMembersViewInt {

    private ProjectMembersPresenterInt projectMembersPresenter;
    private RecyclerView membersList;
    private Button addMemberBtn;

    private ProgressDialog invitingProgressDialog, deletingMemberProgressDialog;
    public ProjectMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pid = getArguments().getString("projectId");
        this.projectMembersPresenter = new ProjectMembersPresenter(this, pid);
        projectMembersPresenter.checkIfOwner();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_members, container, false);
        membersList = view.findViewById(R.id.projectMembersRecyclerView);
        setupRecyclerView();
        addMemberBtn = view.findViewById(R.id.projectMembersInviteMemberBtn);

        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickInviteMember(view);
            }
        });

        return view;
    }

    // Only owner of the project can view and click the add member fab
    @Override
    public void setAddMemberInvisible() {
        addMemberBtn.setVisibility(View.GONE);
    }


    // Sets up the recycler view to display info about members
    private void setupRecyclerView(){

        // Sets up the layout so that results are displayed in reverse order, meaning new items are added to the bottom
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        membersList.setLayoutManager(mLayoutManager);
        membersList.setAdapter(projectMembersPresenter.setupMembersAdapter(membersList));

    }

    public void onClickInviteMember(View view) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_add_member, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Invite New Member")
                .setView(alertView)
                .setMessage("Enter the e-mail address of the user you want to invite.")
                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password and delete project
                        EditText emailET = (EditText) alertView.findViewById(R.id.alert_dialogue_add_member_email_text_field);
                        String emailAddress = emailET.getText().toString().trim();

                        // Cannot send null email address
                        if(emailAddress == null){
                            showMessage("Please enter the e-mail address of the user to invite.");
                        }

                        else {
                            // Cannot send empty string
                            if(emailAddress.length() == 0) {
                                showMessage("Please enter the e-mail address of the user to invite.");
                            }
                            else {

                                // Creates a dialog that appears to tell the user that inviting a user is still occurring
                                invitingProgressDialog = new ProgressDialog(getContext());
                                invitingProgressDialog.setTitle("Invite User");
                                invitingProgressDialog.setCancelable(false);
                                invitingProgressDialog.setMessage("Inviting user to project...");
                                invitingProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                invitingProgressDialog.show();

                                // Password is of valid type, send it
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Member?")
                .setView(alertView)
                .setMessage("Are you sure you want to delete this member? Enter your password below to confirm.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password and delete project
                        EditText passwordET = (EditText) alertView.findViewById(R.id.alert_dialogue_delete_password_text_field);
                        String password = passwordET.getText().toString().trim();

                        // Cannot send null password
                        if(password == null){
                            showMessage("Incorrect password, could not delete member.");
                        }

                        else {
                            // Cannot send empty string
                            if(password.length() == 0) {
                                showMessage("Incorrect password, could not delete member.");
                            }
                            else {

                                // Creates a dialog that appears to tell the user that deleting a user is still occurring
                                deletingMemberProgressDialog = new ProgressDialog(getContext());
                                deletingMemberProgressDialog.setTitle("Delete Member");
                                deletingMemberProgressDialog.setCancelable(false);
                                deletingMemberProgressDialog.setMessage("Attempting to delete member...");
                                deletingMemberProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                deletingMemberProgressDialog.show();

                                // Password is of valid type, send it
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
    public void showMessage(String message) {

        // Close dialogs if they are still running
        if(invitingProgressDialog != null && invitingProgressDialog.isShowing()){
            invitingProgressDialog.dismiss();
        }

        if(deletingMemberProgressDialog != null && deletingMemberProgressDialog.isShowing()){
            deletingMemberProgressDialog.dismiss();
        }

        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();

    }

    // Viewholder class to display members of a project
    public static class MembersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView emailView;
        ImageButton deleteView;
        ImageButton owner;

        public MembersViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            emailView = (TextView) mView.findViewById(R.id.memberRowEmail);
            deleteView = (ImageButton) mView.findViewById(R.id.memberRowDeleteBtn);
            owner = (ImageButton) mView.findViewById(R.id.memberRowOwner);
        }


        // Populates each row of the recycler view with the member details
        public void setDetails(String emailAddress){
            emailView.setText(emailAddress);
        }

        // Gets the delete button
        public ImageButton getDeleteView(){
            return deleteView;
        }

        public ImageButton getOwner(){return owner;}

        // Under certain circumstances, delete member button should not be seen
        public void setDeleteInvisible(){
            deleteView.setVisibility(View.GONE);
        }

        public void setOwnerInvisible(){owner.setVisibility(View.GONE);}
    }

}
