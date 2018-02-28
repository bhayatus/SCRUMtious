package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

import android.app.Activity;

public interface LoginPresenterInt {
    void attemptLogin(Activity context, String emailAddress, String password);
}
