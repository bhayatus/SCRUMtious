package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface SprintListViewInt extends ListenerInt {
    void onProjectDeleted();
    void goToSprintScreen(String sid);
    void setView();
    void showMessage(String message, boolean showAsToast);
}
