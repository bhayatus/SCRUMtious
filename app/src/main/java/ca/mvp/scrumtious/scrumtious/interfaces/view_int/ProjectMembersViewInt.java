package ca.mvp.scrumtious.scrumtious.interfaces.view_int;


public interface ProjectMembersViewInt {
    void onClickDelete(final String uid);
    void deleteMemberExceptionMessage(String error);
}
