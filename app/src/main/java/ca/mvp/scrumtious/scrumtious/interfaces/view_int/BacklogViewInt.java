package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface BacklogViewInt {
    void showMessage(String message);
    void goToUserStoryScreen(String usid);
    void onClickChangeStatus(String usid, boolean newStatus);
    void onClickDeleteUserStory(String usid);
    void setView();
}
