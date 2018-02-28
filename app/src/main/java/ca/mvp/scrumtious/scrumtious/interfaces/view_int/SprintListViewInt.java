package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface SprintListViewInt extends ListenerInt {
    void goToSprintScreen(String sid);
    void setEmptyStateView();
    void showMessage(String message, boolean showAsToast);
}
