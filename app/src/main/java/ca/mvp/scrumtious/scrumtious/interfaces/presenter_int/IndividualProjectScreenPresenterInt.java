package ca.mvp.scrumtious.scrumtious.interfaces.presenter_int;

public interface IndividualProjectScreenPresenterInt {
    void setupProjectDeleteListener();
    void checkIfOwner();
    void validatePassword(String password);
}
