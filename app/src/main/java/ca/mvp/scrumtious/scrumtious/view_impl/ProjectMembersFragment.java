package ca.mvp.scrumtious.scrumtious.view_impl;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
        this.projectMembersPresenter = new ProjectMembersPresenter(this);
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

    public void onClickAddMember(View view){

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
        public void setDetails(String emailAddrress){
            emailView.setText(emailAddrress);
        }

        public ImageButton getDeleteView(){
            return deleteView;
        }
    }

}
