package ca.mvp.scrumtious.scrumtious.view_impl;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import ca.mvp.scrumtious.scrumtious.R;
import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.TaskBoardPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.TaskBoardViewInt;
import ca.mvp.scrumtious.scrumtious.model.Task;
import ca.mvp.scrumtious.scrumtious.presenter_impl.TaskBoardPresenter;
import ca.mvp.scrumtious.scrumtious.utils.SnackbarHelper;

public class TaskBoardFragment extends Fragment implements TaskBoardViewInt {

    private String pid;
    private String type;
    private TaskBoardPresenterInt taskBoardPresenter;
    private RecyclerView taskBoardList;
    private LinearLayout emptyStateView;
    private FirebaseRecyclerAdapter<Task, TaskBoardViewHolder> taskBoardAdapter;
    private ProgressDialog deletingTaskDialog;
    public TaskBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pid = getArguments().getString("projectId");
        String userStoryId = getArguments().getString("userStoryId");
        type = getArguments().getString("type");
        this.taskBoardPresenter = new TaskBoardPresenter(this,pid,userStoryId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_board, container, false);
        taskBoardList = view.findViewById(R.id.taskBoardRecyclerView);
        emptyStateView = view.findViewById(R.id.taskBoardEmptyStateView);
        setupRecyclerView();
        return view;
    }
    public void setupRecyclerView(){
        taskBoardList.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Sets up the specified type of adapter
        taskBoardAdapter = taskBoardPresenter.setupTaskBoardAdapter(type);
        taskBoardList.setAdapter(taskBoardAdapter);
    }
    @Override
    public void showMessage(String message, boolean showAsToast) {
        if (deletingTaskDialog!=null && deletingTaskDialog.isShowing()){
            deletingTaskDialog.dismiss();
        }

        // Show message in toast so it persists across activity transitions
        if (showAsToast){
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }

        else {
            // Call the utils class method to handle making the snackbar
            SnackbarHelper.showSnackbar(getActivity(), message);
        }
    }

    @Override
    public void setEmptyStateView() {
        if(taskBoardAdapter.getItemCount() == 0){
            emptyStateView.setVisibility(View.VISIBLE);
            taskBoardList.setVisibility(View.GONE);
        }
        else{
            emptyStateView.setVisibility(View.GONE);
            taskBoardList.setVisibility(View.VISIBLE);
        }
    }
    public void onClickDeleteTask(final String tid){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deletingTaskDialog = new ProgressDialog(getContext());
                        deletingTaskDialog.setTitle("Delete Task");
                        deletingTaskDialog.setCancelable(false);
                        deletingTaskDialog.setMessage("Attempting to delete task...");
                        deletingTaskDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        deletingTaskDialog.show();

                        taskBoardPresenter.deleteTask(tid);
                        }

                    }
                )
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remain in this screen
                    }
                })
                .create().show();
    }
    public static class TaskBoardViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView desc;
        ImageButton taskSwitch;
        ImageButton taskDelete;
        CardView card;

        public TaskBoardViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            desc = (TextView) mView.findViewById(R.id.taskDescription);
            taskDelete = (ImageButton) mView.findViewById(R.id.taskDeleteBtn);
            taskSwitch = (ImageButton) mView.findViewById(R.id.taskSwitchStatesBtn);
            card = (CardView) mView.findViewById(R.id.taskRowCard);
        }
        public void setDetails(String description){
            desc.setText(description);
        }
        public ImageButton getTaskSwitch(){
            return this.taskSwitch;
        }

        public ImageButton getTaskDelete(){
            return this.taskDelete;
        }

        // Only owner should be able to delete user stories
        public void setDeleteInvisible(){
            this.taskDelete.setVisibility(View.GONE);
        }

        public void setCardRed(){
            card.setCardBackgroundColor(Color.parseColor("#F44336"));
        }

        public void setCardYellow(){
            card.setCardBackgroundColor(Color.parseColor("#FFC107"));
        }

        public void setCardGreen(){
            card.setCardBackgroundColor(Color.parseColor("#8BC34A"));
        }
    }
}