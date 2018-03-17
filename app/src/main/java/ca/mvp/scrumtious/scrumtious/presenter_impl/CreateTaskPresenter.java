package ca.mvp.scrumtious.scrumtious.presenter_impl;


import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.CreateTaskPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.CreateTaskViewInt;

public class CreateTaskPresenter implements CreateTaskPresenterInt {

    private CreateTaskViewInt createTaskView;
    private String pid, usid;

    public CreateTaskPresenter(CreateTaskViewInt createTaskView, String pid, String usid){
        this.createTaskView = createTaskView;
        this.pid = pid;
        this.usid = usid;
    }
}
