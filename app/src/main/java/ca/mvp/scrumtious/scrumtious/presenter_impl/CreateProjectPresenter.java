package ca.mvp.scrumtious.scrumtious.presenter_impl;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateProjectPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateProjectViewInt;

public class CreateProjectPresenter implements CreateProjectPresenterInt {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRootReference;

    private CreateProjectViewInt createProjectView;

    public CreateProjectPresenter(CreateProjectViewInt createProjectView) {
        this.createProjectView = createProjectView;
    }

    @Override
    public void addProjectToDatabase(String projectTitle, String projectDesc) {
        firebaseAuth = FirebaseAuth.getInstance();

        final String projectOwnerUid = firebaseAuth.getCurrentUser().getUid();
        String projectOwnerEmail = firebaseAuth.getCurrentUser().getEmail();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRootReference = firebaseDatabase.getReference();
        final String projectId = firebaseRootReference.push().getKey();

        // Get the current date
        long currentTimeInMilliseconds = System.currentTimeMillis();

        Map projectMap = new HashMap<>();

        // All the changes that need to be made in one go, to ensure atomicity
        projectMap.put("/projects/" + projectId + "/" + "projectTitle", projectTitle);
        projectMap.put("/projects/" + projectId + "/" + "projectDesc", projectDesc);
        projectMap.put("/projects/" + projectId + "/" + "projectOwnerUid", projectOwnerUid);
        projectMap.put("/projects/" + projectId + "/" + "projectOwnerEmail", projectOwnerEmail);
        projectMap.put("/projects/" + projectId + "/" + "creationTimeStamp", currentTimeInMilliseconds);
        projectMap.put("/projects/" + projectId + "/" + "numMembers", 1);
        projectMap.put("/projects/" + projectId + "/" + "numSprints", 0);
        projectMap.put("/projects/" + projectId + "/" + "currentVelocity", 0);
        projectMap.put("/projects/" + projectId + "/" + projectOwnerUid, "member");
        projectMap.put("/users/" + projectOwnerUid + "/" + projectId, "member");


        firebaseRootReference.updateChildren(projectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // All changes to database were successful, tell user
                if (task.isSuccessful()){
                     createProjectView.onSuccessfulCreateProject();
                }
                // Failed at some point, roll back changes and tell user
                else{
                    createProjectView.showMessage("An error occurred, failed to create the project.", false);
                }
            }
        });
    }
}
