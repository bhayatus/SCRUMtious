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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.GroupChatPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.GroupChatViewInt;
import ca.mvp.scrumtious.scrumtious.model.Message;
import ca.mvp.scrumtious.scrumtious.presenter_impl.GroupChatPresenter;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class GroupChatActivity extends AppCompatActivity implements GroupChatViewInt {

    private GroupChatPresenterInt groupChatPresenter;
    private FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> groupChatAdapter;
    private ProgressDialog loadingMessagesDialog;
    private ImageButton groupChatSendBtn;
    private RecyclerView messageList;
    private String pid;

    public GroupChatActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getIntent().getExtras();
        pid = data.getString("projectId");
        groupChatPresenter = new GroupChatPresenter(this, pid);
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

        messageList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        groupChatAdapter = groupChatPresenter.setupMessageAdapter();

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
        LinearLayout leftContainer, rightContainer;

        public MessagesViewHolder(View itemView){

            super(itemView);
            this.mView = itemView;

            messageContentRight = (TextView) mView.findViewById(R.id.message_content_right);
            messageContentLeft = (TextView) mView.findViewById(R.id.message_content_left);
            messageTimestampRight= (TextView) mView.findViewById(R.id.message_timestamp_right);
            messageTimestampLeft= (TextView) mView.findViewById(R.id.message_timestamp_left);
            messageSentByLeft = (TextView) mView.findViewById(R.id.message_sent_by_left);
            leftContainer = (LinearLayout) mView.findViewById(R.id.message_container_left);
            rightContainer = (LinearLayout) mView.findViewById(R.id.message_container_right);

        }

        public void hideLeft(){

            leftContainer.setVisibility(View.GONE);
            rightContainer.setVisibility(View.VISIBLE);

        }

        public void hideRight(){

            leftContainer.setVisibility(View.VISIBLE);
            rightContainer.setVisibility(View.GONE);

        }

        public void setDetails(String messageText, String timeStamp, String senderEmail){
            messageContentLeft.setText(messageText);
            messageTimestampLeft.setText(timeStamp);
            messageSentByLeft.setText(senderEmail);
            messageContentRight.setText(messageText);
            messageTimestampRight.setText(timeStamp);
        }
    }

    public void showMessage(String message, boolean showAsToast) {

        // Show message in toast so it persists across activity transitions
        if (showAsToast){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        else {
            // Call the utils class method to handle making the snackbar
            SnackbarHelper.showSnackbar(this, message);
        }

    }


}
