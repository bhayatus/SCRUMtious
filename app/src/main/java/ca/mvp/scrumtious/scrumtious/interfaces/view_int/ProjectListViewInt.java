package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface ProjectListViewInt {
    void goToProjectScreen(String pid);
    void setView();
    void showMessage(String message, boolean showAsToast);
}
