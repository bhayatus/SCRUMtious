package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface CreateSprintViewInt extends ListenerInt {
    void showMessage(String message, boolean showAsToast);
    void onSuccessfulCreateSprint();
}
