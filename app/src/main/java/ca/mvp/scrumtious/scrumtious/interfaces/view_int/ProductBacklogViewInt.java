package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface ProductBacklogViewInt extends ListenerInt{
    void onProjectDeleted();
    void showMessage(String message, boolean showAsToast);
}
