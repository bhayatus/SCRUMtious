package ca.mvp.scrumtious.scrumtious.presenter_impl;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateUserStoryViewInt;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateUserStoryPresenterInt;

public class CreateUserStoryPresenter implements CreateUserStoryPresenterInt{

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private CreateUserStoryViewInt createUserStoryView;
    private String pid;

    public CreateUserStoryPresenter (CreateUserStoryViewInt createUserStoryView, String pid){
        this.createUserStoryView = createUserStoryView;
        this.pid = pid;
    }

    public void addUserStoryToDatabase(String name, int points, String details){

        HashMap<String, String> userStoryMap = new HashMap<>();
        String stringPoints = Integer.toString(points);
        userStoryMap.put("userStoryName", name);
        userStoryMap.put("userStoryPoints", stringPoints);
        userStoryMap.put("userStoryDetails", details);
        userStoryMap.put("completed", "false");
        userStoryMap.put("assignedTo", "null");
        userStoryMap.put("assignedTo_completed", "null_false");
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("projects").child(this.pid).child("user_stories");
        final String userStoryID = mRef.push().getKey();

        mRef.child(userStoryID).setValue(userStoryMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    createUserStoryView.onSuccessfulCreateUserStory();
                }else{
                    createUserStoryView.showMessage("Could not create user story.");
                }
            }
        });

    }


}
