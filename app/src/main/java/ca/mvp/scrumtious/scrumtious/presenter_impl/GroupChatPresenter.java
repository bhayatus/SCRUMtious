package ca.mvp.scrumtious.scrumtious.presenter_impl;


import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.HashMap;
import java.util.Map;
import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.GroupChatPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.GroupChatViewInt;
import ca.mvp.scrumtious.scrumtious.model.Message;
import ca.mvp.scrumtious.scrumtious.view_impl.GroupChatActivity;


public class GroupChatPresenter extends AppCompatActivity implements GroupChatPresenterInt {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private Query mQuery;

    private GroupChatViewInt groupChatView;

    private String pid;

    public GroupChatPresenter(GroupChatViewInt groupChatView, String pid) {
        this.groupChatView = groupChatView;
        this.pid = pid;
    }

    public void addMessagesToDatabase(final String messageText) {
        mAuth = FirebaseAuth.getInstance();
        String senderEmail = mAuth.getCurrentUser().getEmail();

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid).child("messages");

        final String messageId = mRef.push().getKey();

        // Get the current date long timeStamp = System.currentTimeMillis();
        long timeStamp = System.currentTimeMillis();

        Map messagesMap = new HashMap<>();

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        //All changes made in one go, ensure atomicity
        messagesMap.put("/projects/" + pid + "/messages/" + messageId + "/messageText", messageText);
        messagesMap.put("/projects/" + pid + "/messages/" + messageId + "/senderEmail", senderEmail);
        messagesMap.put("/projects/" + pid + "/messages/" + messageId + "/timeStamp", timeStamp);

        mRef.updateChildren(messagesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    groupChatView.onSuccessfulSent();
                } else {
                    groupChatView.showMessage("An error occurred, failed to send message.", false);
                }
            }
        });
    }


    public FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> setupMessageAdapter() {
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        mQuery = mRef.child("projects").child(pid).child("messages").orderByChild("timeStamp");

        FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> messageListAdapter
                = new FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder>(
                        Message.class,
                        R.layout.group_chat_message_row,
                        GroupChatActivity.MessagesViewHolder.class,
                        mQuery
        ){

            @Override
            protected void populateViewHolder(final GroupChatActivity.MessagesViewHolder viewHolder, Message model, int position) {
                final GroupChatActivity.MessagesViewHolder mViewHolder = viewHolder;

                long timeStamp = model.getTimeStamp();
                String messageContent = model.getMessageText();
                String sender = model.getSenderEmail();

                mAuth = FirebaseAuth.getInstance();
                String userEmail = mAuth.getCurrentUser().getEmail();

                viewHolder.setDetails(messageContent, timeStamp, sender);

                // Current message was from current user
                if(sender.equals(userEmail)){
                    mViewHolder.showRightSide();
                }else{
                    mViewHolder.showLeftSide();
                }

                viewHolder.setDetails(messageContent, timeStamp, sender);
                // User clicked on the message
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Details are shown currently, hide them
                        if (mViewHolder.getGroupChatMessageRowTimestampLeftTextView().isShown()){
                            mViewHolder.hideLeftDetails();
                        }
                        else{
                            mViewHolder.showLeftDetails();
                        }
                        if (mViewHolder.getGroupChatMessageRowTimestampRightTextView().isShown()){
                            mViewHolder.hideRightDetails();
                        }
                        else{
                            mViewHolder.showRightDetails();
                        }
                    }
                });

            }

            @Override
            public void onDataChanged() {
                // If changes made to view, scroll to bottom
                groupChatView.scrollToBottom();

            }
        };



        return messageListAdapter;
    }
}

