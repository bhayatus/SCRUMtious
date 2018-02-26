package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;


public interface IndividualSprintPresenterInt {

    void setupProjectDeletedListener();
    void setupSprintDeletedListener();
    void checkIfOwner();
    void validatePassword(String password);
}
