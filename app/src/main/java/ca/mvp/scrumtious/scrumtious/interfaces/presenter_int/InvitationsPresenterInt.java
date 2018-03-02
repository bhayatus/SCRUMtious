package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import ca.mvp.scrumtious.scrumtious.model.UserInvite;
import ca.mvp.scrumtious.scrumtious.view_impl.InvitationsFragment;

public interface InvitationsPresenterInt {

    FirebaseRecyclerAdapter<UserInvite, InvitationsFragment.InvitationsViewHolder> setupInvitationListAdapter
            ();

    void acceptInvite(String projectId, String inviteId);
    void removeInvite(String inviteId);
}
