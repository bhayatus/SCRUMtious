package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateUserStoryViewInt;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateUserStoryPresenterInt;

public class CreateUserStoryPresenter implements CreateUserStoryPresenterInt{

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;

    private CreateUserStoryViewInt createUserStoryView;

    private final String PROJECT_ID;

    public CreateUserStoryPresenter (CreateUserStoryViewInt createUserStoryView, String pid){
        this.createUserStoryView = createUserStoryView;
        this.PROJECT_ID = pid;
    }

    @Override
    public void addUserStoryToDatabase(String name, int points, String details){

        String stringPoints = Integer.toString(points);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        final String userStoryID = firebaseRootReference.push().getKey();

        HashMap userStoryMap = new HashMap<>();

        // All the changes that need to be made in one go, to ensure atomicity
        userStoryMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + userStoryID + "/" + "userStoryName", name);
        userStoryMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + userStoryID + "/" + "userStoryPoints", stringPoints);
        userStoryMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + userStoryID + "/" + "userStoryDetails", details);
        userStoryMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + userStoryID + "/" + "completed", "false");
        userStoryMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + userStoryID + "/" + "completedDate", 0);
        userStoryMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + userStoryID + "/" + "assignedTo", "null");
        userStoryMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + userStoryID + "/" + "assignedTo_completed", "null_false");
        userStoryMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + userStoryID + "/" + "assignedToName", "");
        userStoryMap.put("/projects/" + PROJECT_ID + "/" + "user_stories" + "/" + userStoryID + "/" + "numTasks", 0);

        firebaseRootReference.updateChildren(userStoryMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    createUserStoryView.onSuccessfulCreateUserStory();
                }else{
                    createUserStoryView.showMessage("An error occurred, failed to create the user story.", false);
                }
            }
        });

    }

}
