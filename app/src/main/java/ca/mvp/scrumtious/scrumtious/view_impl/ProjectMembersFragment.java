package ca.mvp.scrumtious.scrumtious.view_impl;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProjectMembersPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectMembersViewInt;
import ca.mvp.scrumtious.scrumtious.presenter_impl.ProjectMembersPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectMembersFragment extends Fragment implements ProjectMembersViewInt {

    private ProjectMembersPresenterInt projectMembersPresenter;
    private RecyclerView membersList;
    public ProjectMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pid = getArguments().getString("projectId");
        this.projectMembersPresenter = new ProjectMembersPresenter(this, pid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_members, container, false);
        membersList = view.findViewById(R.id.membersListScreenRecyclerView);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView(){

        membersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        membersList.setAdapter(projectMembersPresenter.setupMembersAdapter(membersList));

    }

    public void onSuccessfulDeletion() {
        Intent intent = new Intent(getContext(), ProjectTabsScreenActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void onClickAddMember(View view){

    }

    public void onClickDelete(final String uid){
        LayoutInflater inflater = (this).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.alert_dialogue_delete_project, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete?")
                .setView(alertView)
                .setMessage("Are you sure you want to delete this member? Enter your password below to confirm.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate password and delete project
                        EditText passwordET = (EditText) alertView.findViewById(R.id.alert_dialogue_delete_password_text_field);
                        String password = passwordET.getText().toString().trim();
                        projectMembersPresenter.validatePassword(password, uid);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }

    @Override
    public void deleteMemberExceptionMessage(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView emailView;
        ImageButton deleteView;

        public MembersViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            emailView = (TextView) mView.findViewById(R.id.member_row_email);
            deleteView = (ImageButton) mView.findViewById(R.id.member_delete_btn);
        }


        // Populates each row of the recycler view with the project details
        public void setDetails(String emailAddress){
            emailView.setText(emailAddress);
        }

        public ImageButton getDeleteView(){
            return deleteView;
        }
    }

}
