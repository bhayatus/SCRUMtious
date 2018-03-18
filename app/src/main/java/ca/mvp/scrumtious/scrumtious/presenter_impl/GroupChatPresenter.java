package ca.mvp.scrumtious.scrumtious.presenter_impl;

/**
 * Created by Swetha on 2018-03-18.
 */

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

        Map messagesMap = new HashMap<>();

        // Get the current date long timeStamp = System.currentTimeMillis();
        long timeStamp = System.currentTimeMillis();

        //All changes made in one go, ensure atomicity
        messagesMap.put("/projects/" + pid + "/messages/" + messageId + "/", messageText);
        messagesMap.put("/projects/" + pid + "/messages/" + messageId + "/", senderEmail);
        messagesMap.put("/projects/" + pid + "/messages/" + messageId + "/", timeStamp);

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

        String userID = mAuth.getCurrentUser().getUid();

        mQuery = mRef.child("projects").child(pid).child("messages").orderByChild("timeStamp");

        FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder> messageListAdapter
                = new FirebaseRecyclerAdapter<Message, GroupChatActivity.MessagesViewHolder>(
                        Message.class,
                        R.layout.group_chat_message_row,
                        GroupChatActivity.MessagesViewHolder.class,
                        mQuery
        ){

            @Override
            protected void populateViewHolder(GroupChatActivity.MessagesViewHolder viewHolder, Message model, int position) {
                final GroupChatActivity.MessagesViewHolder mViewHolder = viewHolder;
                final Message messageModel = model;

                long timeStamp = model.getTimeStamp();
                String messageContent = model.getMessageText();
                String sender = model.getSenderEmail();
                String userEmail = mAuth.getCurrentUser().getEmail();
                final String mid = getRef(position).getKey();

                viewHolder.setDetails(messageContent, timeStamp, sender);
                if(sender.equals(userEmail)){
                    viewHolder.hideLeft();
                }else{
                    viewHolder.hideRight();
                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener(){

                    public void onClick(View v){
                        groupChatView.showMessageDetails(mid);
                    }
                });
            }
        };

        return messageListAdapter;
    }
}

