package ca.mvp.scrumtious.scrumtious.view_impl;


import android.media.Image;
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

/**
 * A simple {@link Fragment} subclass.
 */
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
