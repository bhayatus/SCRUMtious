package ca.mvp.scrumtious.scrumtious.view_impl;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProjectListViewInt;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectListFragment extends Fragment implements ProjectListViewInt{

    private ViewProjectsScreenPresenterInt viewProjectsScreenPresenter;

    public ProjectListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewProjectsScreenPresenter = new ViewProjectsScreenPresenter(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects_list, container, false);
    }

    @Override
    public void goToProjectScreen(String pid) {

    }

    public static class ProjectsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView titleView, ownerEmailAddressView, descriptionView;
        Button projectRowButton;

        public ProjectsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            titleView = (TextView) mView.findViewById(R.id.projectListTitle);
            ownerEmailAddressView = (TextView) mView.findViewById(R.id.projectListOwner);
            descriptionView = (TextView) mView.findViewById(R.id.projectListDesc);
            projectRowButton = (Button) mView.findViewById(R.id.projectListButton);
        }

        public Button getButtonInProjectRow(){
            return projectRowButton;
        }

        // Populates each row of the recycler view with the project details
        public void setDetails(Context context, String title, String ownerEmailAddress, String description){
            titleView.setText(title);
            ownerEmailAddressView.setText(ownerEmailAddress);
            descriptionView.setText(description);
        }
    }
}
