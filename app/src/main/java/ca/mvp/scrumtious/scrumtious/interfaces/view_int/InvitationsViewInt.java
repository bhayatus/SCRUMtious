package ca.mvp.scrumtious.scrumtious.interfaces.view_int;


public interface InvitationsViewInt {

    void onClickAccept(String projectId, String inviteId);
    void onClickDecline(String inviteId);
}
