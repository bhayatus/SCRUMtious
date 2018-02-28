package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface BacklogViewInt {
    void showMessage(String message, boolean showAsToast);
    void goToUserStoryScreen(String usid);
    void onClickChangeStatus(String usid, boolean newStatus);
    void onClickDeleteUserStory(String usid);
    void setEmptyStateView();
    void onLongClickUserStory(String usid);
}
