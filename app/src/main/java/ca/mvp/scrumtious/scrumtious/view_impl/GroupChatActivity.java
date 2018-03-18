package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.GroupChatPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.GroupChatViewInt;
import ca.mvp.scrumtious.scrumtious.model.Message;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class GroupChatActivity extends AppCompatActivity implements GroupChatViewInt {

    private GroupChatPresenterInt groupChatPresenter;
    private FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> groupChatAdapter;
    private ProgressDialog loadingMessagesDialog;
    private ImageButton groupChatSendBtn;
    private RecyclerView messageList;

    public GroupChatActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupChatPresenter = new GroupChatPresenter(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_group_chat, container, false);
        messageList = (RecyclerView) view.findViewById(R.id.groupChatRecyclerView);
        setupRecyclerView();
        groupChatSendBtn = (ImageButton) view.findViewById(R.id.groupChatSendBtn);
        return view;

    }

    //TODO
    @Override
    public void onProjectDeleted() {

    }

    //TODO
    @Override
    public void onSprintDeleted() {

    }

    //TODO
    @Override
    public void onUserStoryDeleted() {

    }

    private void setupRecyclerView(){
        loadingMessagesDialog = new ProgressDialog(getApplicationContext());
        loadingMessagesDialog.setTitle("Load Messages");
        loadingMessagesDialog.setCancelable(false);
        loadingMessagesDialog.setMessage("Now loading your messages...");
        loadingMessagesDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingMessagesDialog.show();

        messageList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        groupChatAdapter = groupChatPresenter.setupGroupChatAdapter(loadingMessagesDialog, false);

        messageList.setAdapter(groupChatAdapter);
    }

    public void onClickSendMessage(View view){

        EditText groupChatMessageInput = (EditText) findViewById(R.id.groupChatMessageInput);
        String messageInput = groupChatMessageInput.getText().toString();
        if (isValidMessage(messageInput)){
            groupChatPresenter.addMessagesToDatabase(messageInput);
        }
        else{
            //cannot send an empty message , toast it
        }

    }

    //Check if message is empty
    public boolean isValidMessage(String message){
        return (!message.trim().isEmpty());
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView messageContentRight, messageContentLeft,messageTimestampRight, messageTimestampLeft, messageSentByLeft;

        public MessagesViewHolder(View itemView){

            super(itemView);
            this.mView = itemView;

            messageContentRight = (TextView) mView.findViewById(R.id.message_content_right);
            messageContentLeft = (TextView) mView.findViewById(R.id.message_content_left);
            messageTimestampRight= (TextView) mView.findViewById(R.id.message_timestamp_right);
            messageTimestampLeft= (TextView) mView.findViewById(R.id.message_timestamp_left);
            messageSentByLeft = (TextView) mView.findViewById(R.id.message_sent_by_left);

        }
        //change it, use the containers
        public void hideLeft(){

            messageContentLeft.setVisibility(View.GONE);
            messageTimestampLeft.setVisibility(View.GONE);
            messageSentByLeft.setVisibility(View.GONE);
            messageContentRight.setVisibility(View.VISIBLE);
            messageTimestampRight.setVisibility(View.VISIBLE);
        }

        public void hideRight(){

            messageContentRight.setVisibility(View.GONE);
            messageTimestampRight.setVisibility(View.GONE);
            messageContentLeft.setVisibility(View.VISIBLE);
            messageTimestampLeft.setVisibility(View.VISIBLE);
            messageSentByLeft.setVisibility(View.VISIBLE);

        }

        public void setDetailsLeft(String messageText, String timeStamp, String senderEmail){

            messageContentLeft.setText(messageText);
            messageTimestampLeft.setText(timeStamp);
            messageSentByLeft.setText(senderEmail);

        }

        public void setDetailsRight(String messageText, String timeStamp){

            messageContentRight.setText(messageText);
            messageTimestampRight.setText(timeStamp);

        }
    }


}
