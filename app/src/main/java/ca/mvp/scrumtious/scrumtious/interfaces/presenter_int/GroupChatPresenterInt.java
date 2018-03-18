package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.model.Message;
import ca.mvp.scrumtious.scrumtious.view_impl.GroupChatActivity;

public interface GroupChatPresenterInt {

    void addMessagesToDatabase(String messageText);
    FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> setupMessageAdapter
            ();
}
