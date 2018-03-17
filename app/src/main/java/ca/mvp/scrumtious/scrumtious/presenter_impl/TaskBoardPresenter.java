package ca.mvp.scrumtious.scrumtious.presenter_impl;


import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.TaskBoardPresenterInt;
import ca.mvp.scrumtious.scrumtious.view_impl.TaskBoardFragment;

public class TaskBoardPresenter implements TaskBoardPresenterInt {

    private TaskBoardFragment taskBoardView;
    private String pid, usid;

    public TaskBoardPresenter(TaskBoardFragment taskBoardView, String pid, String usid){
        this.pid = pid;
        this.usid = usid;
    }

}
