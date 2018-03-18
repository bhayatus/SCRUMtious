package ca.mvp.scrumtious.scrumtious.view_impl;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.List;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.GroupChatPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.GroupChatViewInt;
import ca.mvp.scrumtious.scrumtious.model.Message;

public class GroupChatActivity extends AppCompatActivity implements GroupChatViewInt {

    private GroupChatPresenterInt groupChatPresenterInt;
    private FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> groupChatAdapter;
    private ProgressDialog loadingMessagesDialog;
    private ImageButton sendMessageBtn;
    private RecyclerView messageList;

    public GroupChatActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupChatPresenterInt = new GroupChatPresenter(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_group_chat, container, false);
        messageList = (RecyclerView) view.findViewById(R.id.groupChatRecyclerView);
        setupRecyclerView();
        sendMessageBtn = (ImageButton) view.findViewById(R.id.groupChatSendBtn);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSendMessage(view);
            }
        });

        return view;

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

    private void setupRecyclerView(){
        loadingMessagesDialog = new ProgressDialog(getActivity());
        loadingMessagesDialog.setTitle("Load Messages");
        loadingMessagesDialog.setCancelable(false);
        loadingMessagesDialog.setMessage("Now loading your messages...");
        loadingMessagesDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingMessagesDialog.show();


        messageList.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupChatAdapter = groupChatPresenterInt.setupGroupChatAdapter(loadingMessagesDialog, false);

        messageList.setAdapter(groupChatAdapter);
    }

    public void onClickSendMessage(View view){

        //grab message from the edittext and add to db, addMessagesToDatabase(messagetext, final long timestamp)


    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView messageTextView, senderEmailView, timeStampView;
        ImageButton sendMessageBtn;

        public MessagesViewHolder(View itemView){
            super(itemView);
            this.mView = itemView;

            messageTextView = (TextView) mView.findViewById(R.id.groupChatRowMessage);
            senderEmailView = (TextView) mView.findViewById(R.id.groupChatRowSenderEmail);
            timeStampView = (TextView) mView.findViewById(R.id.groupChatRowTimeStamp);
        }


        public void setDetails(String messageText, String senderEmail, String timeStamp){

            messageTextView.setText(messageText);
            senderEmailView.setText("Sent by: " + senderEmail);
            timeStampView.setText(timeStamp);

        }
    }

}
