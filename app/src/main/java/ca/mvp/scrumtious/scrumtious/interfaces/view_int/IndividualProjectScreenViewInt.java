package ca.mvp.scrumtious.scrumtious.interfaces.view_int;


public interface IndividualProjectScreenViewInt {
    void onSuccessfulDeletion();
    void setDeleteInvisible();
    void deleteProjectExceptionMessage(String error);
}
