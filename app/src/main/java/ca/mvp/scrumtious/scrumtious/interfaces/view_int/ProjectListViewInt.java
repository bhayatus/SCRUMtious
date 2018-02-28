package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface ProjectListViewInt {
    void goToProjectScreen(String pid);
    void setEmptyStateView();
    void showMessage(String message, boolean showAsToast);
}
