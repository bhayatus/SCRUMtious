package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface CreateTaskViewInt extends ListenerInt {

    void onSuccessfulCreateTask();
    void showMessage(String message, boolean showAsToast);

}
