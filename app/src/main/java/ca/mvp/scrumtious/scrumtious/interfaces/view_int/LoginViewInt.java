package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

public interface LoginViewInt {
    void onSuccessfulLogin(String emailAddress, String password);
    void showMessage(String message, boolean showAsToast);
}
