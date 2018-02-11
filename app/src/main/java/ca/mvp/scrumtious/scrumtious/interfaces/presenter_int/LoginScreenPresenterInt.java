package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;


import android.app.Activity;

public interface LoginScreenPresenterInt {

    void attemptLogin(Activity context, String emailAddress, String password);
    void signOut();

}
