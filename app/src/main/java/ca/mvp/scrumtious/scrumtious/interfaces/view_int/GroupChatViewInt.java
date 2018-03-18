package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

/**
 * Created by Nensi on 2018-03-18.
 */

public interface GroupChatViewInt extends ListenerInt {
    void showMessage(String message, boolean showAsToast);
    void onSuccessfulSent();
}