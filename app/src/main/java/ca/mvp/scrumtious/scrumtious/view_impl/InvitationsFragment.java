package ca.mvp.scrumtious.scrumtious.view_impl;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.InvitationsPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.InvitationsViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.InvitationsPresenter;

public class InvitationsFragment extends Fragment implements InvitationsViewInt{

    private RecyclerView invitationsList;
    private InvitationsPresenterInt invitationsPresenter;
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
        invitationsList = view.findViewById(R.id.userInviteScreenRecyclerView);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView(){
        invitationsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        invitationsList.setAdapter(invitationsPresenter.setupInvitationsAdapter(invitationsList));
    }

    // When user clicks on the accept button for an invite
    @Override
    public void onClickAccept(final String projectId, final String inviteId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Accept Invite?")
                .setMessage("Are you sure you want to accept this invite?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Decline Invite?")
                .setMessage("Are you sure you want to decline this invite?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

    public static class InvitationsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView projectTitleView, emailView;
        ImageButton acceptInvite, declineInvite;
        public InvitationsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            projectTitleView = (TextView) mView.findViewById(R.id.user_invite_row_project);
            emailView = (TextView) mView.findViewById(R.id.user_invite_row_invitee);
            acceptInvite = (ImageButton) mView.findViewById(R.id.user_invite_accept_btn);
            declineInvite = (ImageButton) mView.findViewById(R.id.user_invite_delete_btn);

        }


        // Populates each row of the recycler view with the project details
        public void setDetails(String projectTitle, String emailAddress){
            projectTitleView.setText(projectTitle);
            emailView.setText("by " + emailAddress);
        }

        public ImageButton getAcceptButton(){
            return acceptInvite;
        }

        public ImageButton getDeclineButton(){
            return declineInvite;
        }
    }

}
