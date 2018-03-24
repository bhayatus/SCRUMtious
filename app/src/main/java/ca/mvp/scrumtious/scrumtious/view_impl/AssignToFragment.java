package ca.mvp.scrumtious.scrumtious.view_impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.model.User;

public class AssignToFragment extends DialogFragment {

    private String pid, usid, tid;
    private FirebaseRecyclerAdapter<User, AssignToUserViewHolder> assignToListAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Query mQuery;

    private TextView assignToFragmentNoneTextView;
    private TaskBoardFragment taskBoardView;
    private RecyclerView assignToFragmentRecyclerView;

    public AssignToFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pid = getArguments().getString("projectId");
        usid = getArguments().getString("userStoryId");
        tid = getArguments().getString("taskId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_assign_to, container, false);

        assignToFragmentRecyclerView = view.findViewById(R.id.assignToFragmentRecyclerView);
        assignToFragmentNoneTextView = view.findViewById(R.id.assignToFragmentNoneTextView);

        assignToFragmentNoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assigning task to no one
                assignTask("");
            }
        });
        setupRecyclerView();

        return view;
    }

    // Constructs a new instance of this dialog, passing in important information needed
    public static AssignToFragment newInstance(String projectId, String userStoryId, String taskId){
        AssignToFragment assignToFragment = new AssignToFragment();
        Bundle bundle = new Bundle();
        bundle.putString("projectId", projectId);
        bundle.putString("userStoryId", userStoryId);
        bundle.putString("taskId", taskId);
        assignToFragment.setArguments(bundle);
        return assignToFragment;
    }


    // Necessary to call this after newInstance, sets up the task board view
    public void setTaskBoardView(TaskBoardFragment taskBoardView){
        this.taskBoardView = taskBoardView;
    }


    private void setupRecyclerView(){

        assignToFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(taskBoardView.getContext()));

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mQuery = databaseReference.child("users").orderByChild(pid).equalTo("member");

        // Generates the adapter to grab the list of users we can assign the task to
        assignToListAdapter = new FirebaseRecyclerAdapter<User, AssignToUserViewHolder>(
                User.class,
                R.layout.sendto_row,
                AssignToUserViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(AssignToUserViewHolder viewHolder, User model, int position) {
                final String emailAddress = model.getEmailAddress();

                viewHolder.setDetails(model.getEmailAddress());

                // If user is selected, assign the task to them
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assignTask(emailAddress);
                    }
                });
            }
            @Override
            public void onDataChanged() {

                // Don't bother showing recycler view if no items
                if (getItemCount() == 0){
                    assignToFragmentRecyclerView.setVisibility(View.GONE);
                }
                // Items to show, display recycler view
                else{
                    assignToFragmentRecyclerView.setVisibility(View.VISIBLE);
                }

            }
        };

        assignToFragmentRecyclerView.setAdapter(assignToListAdapter);
    }


    // Takes in name to assign task to
    private void assignTask(final String name){

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        Map assignTaskMap = new HashMap();

        assignTaskMap.put("/projects/" + pid + "/user_stories/" + usid + "/tasks/" + tid + "/assignedTo", name);

        databaseReference.updateChildren(assignTaskMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                // Successfully assigned the task
                if (task.isSuccessful()){
                    taskBoardView.showMessage("Successfully assigned the task.", false);
                }
                // Failed to assign task
                else{
                    taskBoardView.showMessage("An error occurred, failed to assign the task.", false);
                }
            }
        });

    }

    // Viewholder class to display users that we can assign a task to
    public static class AssignToUserViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView sendToRowNameTextView;

        public AssignToUserViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            sendToRowNameTextView = mView.findViewById(R.id.sendToRowNameTextView);
        }

        // Populates each row of the recycler view with the user name
        public void setDetails(String name){
            sendToRowNameTextView.setText(name);
        }

    }

}
