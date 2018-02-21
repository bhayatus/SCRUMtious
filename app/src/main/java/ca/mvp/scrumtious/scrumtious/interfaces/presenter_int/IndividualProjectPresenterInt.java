package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

public interface IndividualProjectPresenterInt {
    void setupProjectDeletedListener();
    void checkIfOwner();
    void validatePassword(String password);
}
