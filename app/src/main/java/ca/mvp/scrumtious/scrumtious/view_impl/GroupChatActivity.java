package ca.mvp.scrumtious.scrumtious.view_impl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.GroupChatPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.GroupChatViewInt;
import ca.mvp.scrumtious.scrumtious.model.Message;

public class GroupChatActivity extends AppCompatActivity implements GroupChatViewInt {

    private GroupChatPresenterInt groupChatPresenterInt;
    FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> allMessagesAdapter;

    private Button sendMessageBtn;

    public GroupChatActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
    }

    @Override
    public void onProjectDeleted() {

    }

    @Override
    public void onSprintDeleted() {

    }

    @Override
    public void onUserStoryDeleted() {

    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public MessagesViewHolder(View itemView){
            super(itemView);
            this.mView = itemView;
        }
    }

}
