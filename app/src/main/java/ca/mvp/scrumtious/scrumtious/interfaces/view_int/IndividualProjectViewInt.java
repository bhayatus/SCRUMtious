package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface IndividualProjectViewInt extends ListenerInt{
    void onSuccessfulDeletion();
    void setDeleteInvisible();
    void showMessage(String message, boolean showAsToast);
}
