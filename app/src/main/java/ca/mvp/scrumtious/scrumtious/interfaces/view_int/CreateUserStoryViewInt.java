package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface CreateUserStoryViewInt extends ListenerInt {
    void showMessage(String message, boolean showAsToast);
    void onSuccessfulCreateUserStory();
}
