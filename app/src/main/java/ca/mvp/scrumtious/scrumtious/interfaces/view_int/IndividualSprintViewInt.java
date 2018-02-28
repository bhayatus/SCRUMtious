package ca.mvp.scrumtious.scrumtious.interfaces.view_int;


public interface IndividualSprintViewInt extends ListenerInt {

    void onProjectDeleted();
    void onSprintDeleted();
    void setDeleteInvisible();
    void showMessage(String message, boolean showAsToast);
}
