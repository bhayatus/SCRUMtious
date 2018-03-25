package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.InvitationsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.InvitationsViewInt;
import ca.mvp.scrumtious.scrumtious.model.UserInvite;
import ca.mvp.scrumtious.scrumtious.presenter_impl.InvitationsPresenter;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class InvitationsFragment extends Fragment implements InvitationsViewInt{

    private InvitationsPresenterInt invitationsPresenter;
    private FirebaseRecyclerAdapter<UserInvite, InvitationsFragment.InvitationsViewHolder> invitationListAdapter;

    private RecyclerView invitationsFragmentRecyclerView;
    private LinearLayout invitationsFragmentNoInvitationsEmptyStateView;


    public InvitationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.invitationsPresenter = new InvitationsPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invitations, container, false);
        invitationsFragmentRecyclerView = view.findViewById(R.id.invitationsFragmentRecyclerView);
        invitationsFragmentNoInvitationsEmptyStateView = view.findViewById(R.id.invitationsFragmentNoInvitationsEmptyStateView);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView(){
        invitationsFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        invitationListAdapter = invitationsPresenter.setupInvitationListAdapter();
        invitationsFragmentRecyclerView.setAdapter(invitationListAdapter);
    }

    // Sets the view to either show the list of invitations, or nothing
    @Override
    public void setEmptyStateView(){
        if (invitationListAdapter.getItemCount() == 0){
            invitationsFragmentNoInvitationsEmptyStateView.setVisibility(View.VISIBLE);
            invitationsFragmentRecyclerView.setVisibility(View.GONE);
        }
        else{
            invitationsFragmentNoInvitationsEmptyStateView.setVisibility(View.GONE);
            invitationsFragmentRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    // When user clicks on the accept button for an invite
    @Override
    public void onClickAccept(final String projectId, final String inviteId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.LoginAlertDialog));
        builder.setTitle("Accept Invite?")
                .setMessage("Are you sure you want to accept this invite?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Accept the invite
                        invitationsPresenter.acceptInvite(projectId, inviteId);
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

    // When user clicks on the decline button for an invite
    @Override
    public void onClickDecline(final String inviteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.LoginAlertDialog));
        builder.setTitle("Decline Invite?")
                .setMessage("Are you sure you want to decline this invite?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove the invite
                        invitationsPresenter.removeInvite(inviteId);
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

    // Viewholder class to display invitations
    public static class InvitationsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView userInviteRowProjectTitleTextView, userInviteRowInviteeNameTextView;
        ImageButton userInviteRowAcceptInviteImageButton, userInviteRowDeclineInviteImageButton;
        public InvitationsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            userInviteRowProjectTitleTextView = mView.findViewById(R.id.userInviteRowProjectTitleTextView);
            userInviteRowInviteeNameTextView = mView.findViewById(R.id.userInviteRowInviteeNameTextView);
            userInviteRowAcceptInviteImageButton = mView.findViewById(R.id.userInviteRowAcceptInviteImageButton);
            userInviteRowDeclineInviteImageButton = mView.findViewById(R.id.userInviteRowDeclineInviteImageButton);

        }

        // Populates each row of the recycler view with the invitation details
        public void setDetails(String projectTitle, String emailAddress){
            userInviteRowProjectTitleTextView.setText(projectTitle);
            userInviteRowInviteeNameTextView.setText("from " + emailAddress);
        }

        public ImageButton getAcceptButton(){
            return userInviteRowAcceptInviteImageButton;
        }

        public ImageButton getDeclineButton(){
            return userInviteRowDeclineInviteImageButton;
        }
    }

}
