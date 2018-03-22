package ca.mvp.scrumtious.scrumtious.interfaces.view_int;


public interface GroupChatViewInt extends ListenerInt {
    void showMessage(String message, boolean showAsToast);
    void onSuccessfulSent();
    void scrollToBottom();
}