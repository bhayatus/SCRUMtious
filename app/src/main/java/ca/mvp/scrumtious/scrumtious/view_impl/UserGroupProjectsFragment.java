package ca.mvp.scrumtious.scrumtious.view_impl;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.mvp.scrumtious.scrumtious.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserGroupProjectsFragment extends Fragment {


    public UserGroupProjectsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_group_projects, container, false);
    }

}
