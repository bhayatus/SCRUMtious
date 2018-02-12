package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

/**
 * Created by Nensi on 2018-02-11.
 */

public interface CreateProjectScreenPresenterInt {

    void setupAuthenticationListener();
    void addProjectToDatabase(String title, String description);

}
