package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface TaskBoardViewInt {
    void showMessage(String message, boolean showAsToast);
    void setEmptyStateView();
    void onClickDeleteTask(final String taskid);
}
