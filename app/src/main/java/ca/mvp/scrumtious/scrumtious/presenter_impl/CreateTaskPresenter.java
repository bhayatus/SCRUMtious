package ca.mvp.scrumtious.scrumtious.presenter_impl;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateTaskPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateTaskViewInt;

public class CreateTaskPresenter extends AppCompatActivity implements CreateTaskPresenterInt {

    private CreateTaskViewInt createTaskView;
    private String pid, usid;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    public CreateTaskPresenter(CreateTaskViewInt createTaskView, String pid, String usid){
        this.createTaskView = createTaskView;
        this.pid = pid;
        this.usid = usid;
    }

    public void addTaskToDatabase(final String taskDesc){

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(pid).child("user_stories")
                .child(usid);

        // Unique key for tasks
        final String taskId = mRef.push().getKey();

        mRef.child("numTasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numTasks = (long) dataSnapshot.getValue();
                numTasks++;

                Map taskMap = new HashMap<>();
                taskMap.put("/tasks/" + taskId + "/taskDesc", taskDesc);
                taskMap.put("/tasks/" + taskId + "/assignedTo", "");
                taskMap.put("/tasks/" + taskId + "/status", "not_started");

                taskMap.put("/numTasks", numTasks);

                mRef.updateChildren(taskMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            createTaskView.onSuccessfulCreateTask();
                        } else {
                            createTaskView.showMessage("An error occurred, failed to create" +
                                    " task.", false);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
